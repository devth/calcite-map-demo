package devth.calcite

import net.hydromatic.linq4j.expressions._

import net.hydromatic.optiq.BuiltinMethod
import net.hydromatic.optiq.prepare.OptiqPrepareImpl
import net.hydromatic.optiq.rules.java._
import net.hydromatic.optiq.runtime.Hook

import org.eigenbase.rel.RelNode
import org.eigenbase.rel.convert.ConverterRelImpl
import org.eigenbase.relopt._
import org.eigenbase.reltype.RelDataType
import org.eigenbase.util.Pair

import java.lang.reflect.Method
import java.util.{List => JList}

import scala.collection.JavaConverters._
import com.typesafe.scalalogging.StrictLogging

import net.hydromatic.optiq.rules.java.EnumerableRel.{Result, Prefer}

class MapToEnumerableConverter(cluster: RelOptCluster,
    traits: RelTraitSet,
    input: RelNode)
  extends ConverterRelImpl(cluster, ConventionTraitDef.INSTANCE, traits, input)
  with StrictLogging with EnumerableRel {

  override def copy(traitSet: RelTraitSet, inputs: java.util.List[RelNode]): RelNode =
    new MapToEnumerableConverter(getCluster, traitSet, inputs.get(0))

  def implement(implementor: EnumerableRelImplementor, pref: Prefer): Result = {
    val mapImplementor = new MapRel.Implementor
    mapImplementor.visitChild(0, getChild)

    val rowType = getRowType()
    val physType: PhysType = PhysTypeImpl.of(
      implementor.getTypeFactory(), rowType,
      pref.prefer(JavaRowFormat.CUSTOM))

    val fields = mapImplementor.getFields
    logger.info(s"fields from implementor: $fields")

    // `fields` should actually be a tree-like data structure that could
    // represent nested projections:
    // ITEM(ITEM($0, 'address'), 'city')" -> "EXPR$0

    // How could such a structure be represented in Linq4j?
    // Here's an example of an Algebraic Data Type in Scala that could represent
    // it well, but could not (guessing?) be represented in Linq4j:
    // Represents a potentially nested projection (e.g. address.city )
    sealed trait NestedProjection
    case class Field(fieldName: String) extends NestedProjection
    case class NestedField(field: Field, child: NestedProjection) extends NestedProjection


    val fieldConstantList: JList[Expression] = constantList(fields)
    val arrayExpr: NewArrayExpression =
      Expressions.newArrayInit(classOf[String], fieldConstantList)
    val fieldsExpression: MethodCallExpression = Expressions.call(
      BuiltinMethod.ARRAYS_AS_LIST.method, arrayExpr)

    val project: Method = classOf[MapTable].getMethod("project", classOf[JList[String]])

    implementor.result(
      physType,
      Blocks.toBlock(
        Expressions.call(mapImplementor.table.getExpression(classOf[MapTable]),
          project, fieldsExpression)))

  }

  private def constantList(values: Seq[String]): JList[Expression] =
    values.map { v => Expressions.constant(v).asInstanceOf[Expression] }.asJava

}
