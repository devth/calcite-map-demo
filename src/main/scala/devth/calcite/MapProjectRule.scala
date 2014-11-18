package devth.calcite

import org.eigenbase.rel.ProjectRel
import org.eigenbase.relopt.RelOptRule
import org.eigenbase.relopt.RelOptRuleCall
import org.eigenbase.relopt.RelTraitSet
import com.typesafe.scalalogging.StrictLogging
import org.eigenbase.relopt.RelOptRule.{operand, none}
import scala.collection.JavaConverters._

class MapProjectRule
  extends RelOptRule(operand(classOf[ProjectRel],
    operand(classOf[MapTableScan], none())),
    "MapProjectRule")
  with StrictLogging {

  override def onMatch(call: RelOptRuleCall) {
    val project: ProjectRel = call.rel(0)
    val scan: MapTableScan = call.rel(1)
    val projects = project.getProjects()
    logger.info(s"onMatch $project ${project.getTraitSet} ${project.getProjects}")
    // TODO: determine fields to pass into MapTableScan
    // val fields = Seq.empty[String].asJava
    val mts = new MapTableScan(
      scan.getCluster,
      scan.getTable,
      scan.mapTable)
    call.transformTo(mts)
  }

}
