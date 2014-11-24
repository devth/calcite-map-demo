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

import net.hydromatic.optiq.rules.java.EnumerableRel.{Result, Prefer}

class MapToEnumerableConverter(cluster: RelOptCluster,
    traits: RelTraitSet,
    input: RelNode)
  extends ConverterRelImpl(cluster, ConventionTraitDef.INSTANCE, traits, input)
  with EnumerableRel {

  override def copy(traitSet: RelTraitSet, inputs: java.util.List[RelNode]): RelNode =
    new MapToEnumerableConverter(getCluster, traitSet, inputs.get(0))

  def implement(implementor: EnumerableRelImplementor, pref: Prefer): Result = {
    val mapImplementor = new MapRel.Implementor
    mapImplementor.visitChild(0, getChild)

    val rowType = getRowType()
    val physType: PhysType = PhysTypeImpl.of(
      implementor.getTypeFactory(), rowType,
      pref.prefer(JavaRowFormat.CUSTOM))

    // val project: Method = classOf[MapTable].getMethod("project")

    // TODO figure out why empty - problem with ProjectRel not being fired?
    val whyIsThisEmpty = mapImplementor.getFields.asJava
    val fields = Seq("name")

    // HOWTO?
    val fieldConstantList: JList[Expression] = constantList(fields)
    val arrayExpr: NewArrayExpression =
      Expressions.newArrayInit(classOf[String], fieldConstantList)
    val fieldsExpression: MethodCallExpression = Expressions.call(
      BuiltinMethod.ARRAYS_AS_LIST.method, arrayExpr)

    // constantArrayList(fields, classOf[String])
    // Expressions.call(
    //   BuiltinMethod.ARRAYS_AS_LIST,
    //   Expressions.newArrayInit(classOf[String], cons

    // enumImplementor.result(physType,
    //   Blocks.toBlock(Expressions.call(factTable.getExpression(OLAPTable.class),
    //     "executeHiveQuery", enumImplementor.getRootExpression())));

    // return implementor.result(
    //     physType,
    //     Blocks.toBlock(
    //         Expressions.call(table.getExpression(CsvTable.class), "project",
    //             Expressions.constant(fields))));

    val project: Method = classOf[MapTable].getMethod("project", classOf[JList[String]])

    implementor.result(
      physType,
      Blocks.toBlock(
        Expressions.call(mapImplementor.table.getExpression(classOf[MapTable]),
          project, fieldsExpression)))

  }

  /** E.g. {@code constantList("x", "y")} returns
   * {@code {ConstantExpression("x"), ConstantExpression("y")}}. */
  private def constantList(values: Seq[String]): JList[Expression] = {
    values.map { v =>
      Expressions.constant(v).asInstanceOf[Expression] }.asJava
  }

  // private def constantArrayList(values: JList[_], clazz: Class[_]): MethodCallExpression =
  //   Expressions.call(
  //     BuiltinMethod.ARRAYS_AS_LIST.method,
  //     Expressions.newArrayInit(clazz, constantList(values)))



}
