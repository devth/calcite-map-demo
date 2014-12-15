package devth.calcite

import org.apache.calcite.rel._
import org.apache.calcite.plan._
import org.apache.calcite.rel.`type`._
import org.apache.calcite.rex._
import org.apache.calcite.util.Pair
import org.apache.calcite.util.Util
import org.apache.calcite.rel.core.Project

import scala.collection.JavaConverters._

class MapProjectRel(cluster: RelOptCluster, traitSet: RelTraitSet,
    child: RelNode, exps: java.util.List[RexNode], rowType: RelDataType, flags: Int)
  extends Project(cluster, traitSet, child, exps, rowType, flags)
  with MapRel {

  assert(getConvention() == MapRel.CONVENTION)
  assert(getConvention() == child.getConvention())

  override def copy(traitSet: RelTraitSet, input: RelNode,
    exps: java.util.List[RexNode], rowType: RelDataType): Project =
    new MapProjectRel(getCluster(), traitSet, input, exps, rowType, flags)

  override def computeSelfCost(planner: RelOptPlanner): RelOptCost =
    super.computeSelfCost(planner).multiplyBy(0.1)

  def implement(implementor: MapRel.Implementor) {
    implementor.visitChild(0, getInput)

    //  How to represent nested fields? e.g.
    //  ITEM(ITEM($0, 'address'), 'city')" -> "EXPR$0
    // val fields: Seq[String] = getNamedProjects.asScala.map { pair => pair.right }
    // implementor.addFields(fields)

    implementor.setProjects(getProjects)


  }
}
