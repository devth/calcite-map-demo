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
      implementor.getTypeFactory(), rowType, pref.prefer(JavaRowFormat.CUSTOM))

    val projects = mapImplementor.getProjects
    logger.info(s"projects from implementor: $projects")

    val projectMethod: Method = classOf[MapTable].getMethod("project", classOf[JList[RexNode]])

    implementor.result(
      physType,
      Blocks.toBlock(
        Expressions.call(mapImplementor.table.getExpression(classOf[MapTable]),
          projectMethod,
            implementor.stash[JList[RexNode]](projects, classOf[JList[RexNode]]))))
  }

  private def constantList(values: Seq[String]): JList[Expression] =
    values.map { v => Expressions.constant(v).asInstanceOf[Expression] }.asJava

}
