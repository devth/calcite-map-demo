package devth.calcite

import net.hydromatic.optiq.impl.java.JavaTypeFactory

import org.eigenbase.rel._
import org.eigenbase.relopt._
import org.eigenbase.reltype._
import org.eigenbase.rex._
import org.eigenbase.util.Pair
import org.eigenbase.util.Util

// import java.util._
import scala.collection.JavaConverters._

class MapProjectRel(cluster: RelOptCluster, traitSet: RelTraitSet,
    child: RelNode, exps: java.util.List[RexNode], rowType: RelDataType, flags: Int)
  extends ProjectRelBase(cluster, traitSet, child, exps, rowType, flags)
  with MapRel {

  assert(getConvention() == MapRel.CONVENTION)
  assert(getConvention() == child.getConvention())

  override def copy(traitSet: RelTraitSet, input: RelNode,
    exps: java.util.List[RexNode], rowType: RelDataType): ProjectRelBase =
    new MapProjectRel(getCluster(), traitSet, input, exps, rowType, flags)

  override def computeSelfCost(planner: RelOptPlanner): RelOptCost =
    super.computeSelfCost(planner).multiplyBy(0.1)

  def implement(implementor: MapRel.Implementor) {
    implementor.visitChild(0, getChild())

    //  How to represent nested fields? e.g.
    //  ITEM(ITEM($0, 'address'), 'city')" -> "EXPR$0
    // val fields: Seq[String] = getNamedProjects.asScala.map { pair => pair.right }
    // implementor.addFields(fields)

    implementor.setProjects(getProjects)


  }
}
