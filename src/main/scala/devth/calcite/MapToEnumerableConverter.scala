package devth.calcite

import org.apache.calcite.linq4j.tree.{Expressions, Expression, Blocks,
  NewArrayExpression, MethodCallExpression}
import org.apache.calcite.util.BuiltInMethod
import org.apache.calcite.adapter.enumerable._
import org.apache.calcite.runtime.Hook
import org.apache.calcite.rex.RexNode
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.convert.ConverterImpl
import org.apache.calcite.plan._
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.util.Pair
import org.apache.calcite.adapter.enumerable.EnumerableRel.{Result, Prefer}

import java.lang.reflect.Method
import java.util.{List => JList}

import scala.collection.JavaConverters._
import com.typesafe.scalalogging.StrictLogging

class MapToEnumerableConverter(cluster: RelOptCluster,
    traits: RelTraitSet,
    input: RelNode)
  extends ConverterImpl(cluster, ConventionTraitDef.INSTANCE, traits, input)
  with StrictLogging with EnumerableRel {

  override def copy(traitSet: RelTraitSet, inputs: java.util.List[RelNode]): RelNode =
    new MapToEnumerableConverter(getCluster, traitSet, inputs.get(0))

  def implement(implementor: EnumerableRelImplementor, pref: Prefer): Result = {
    val mapImplementor = new MapRel.Implementor
    mapImplementor.visitChild(0, getInput)

    val rowType = getRowType()
    val physType: PhysType = PhysTypeImpl.of(
      implementor.getTypeFactory(), rowType,
      pref.prefer(JavaRowFormat.CUSTOM))

    val projects = mapImplementor.getProjects
    logger.info(s"projects from implementor: $projects")

    // `fields` should actually be a nested-list-like data structure that could
    // represent nested projections:
    // ITEM(ITEM($0, 'address'), 'city')" -> "EXPR$0
    // How could such a structure be represented in Linq4j?
    // Here's an example of an Algebraic Data Type in Scala that could represent
    // it well, but could not (guessing?) be represented in Linq4j:
    // Represents a potentially nested projection (e.g. address.city )
    // sealed trait NestedProjection
    // case class Field(fieldName: String) extends NestedProjection
    // case class NestedField(field: Field, child: NestedProjection) extends NestedProjection


    val projectsConstantList: JList[Expression] = projects.asScala.map { p =>
      implementor.stash(p, classOf[RexNode]) }.asJava
    val arrayExpr: NewArrayExpression =
      Expressions.newArrayInit(classOf[RexNode], projectsConstantList)
    val projectsExpression: MethodCallExpression = Expressions.call(
      BuiltInMethod.ARRAYS_AS_LIST.method, arrayExpr)

    val project: Method = classOf[MapTable].getMethod("project", classOf[JList[RexNode]])

    implementor.result(
      physType,
      Blocks.toBlock(
        Expressions.call(mapImplementor.table.getExpression(classOf[MapTable]),
          project, projectsExpression)))

  }

  private def constantList(values: Seq[String]): JList[Expression] =
    values.map { v => Expressions.constant(v).asInstanceOf[Expression] }.asJava

}
