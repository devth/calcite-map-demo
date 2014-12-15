package devth.calcite

import org.apache.calcite.plan.Convention
import org.apache.calcite.plan.RelTraitSet
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.core.Project
import org.apache.calcite.rel.logical.LogicalProject

import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConverters._

class MapProjectRule
  extends MapConverterRule(classOf[LogicalProject],
    Convention.NONE, MapRel.CONVENTION, "MapProjectRule")
  with StrictLogging {

  val i = 0

  def convert(rel: RelNode): RelNode = {

    val project: LogicalProject = rel.asInstanceOf[LogicalProject]

    val traitSet: RelTraitSet = project.getTraitSet().replace(out)

    new MapProjectRel(project.getCluster(),
          traitSet,
          project.getInput,
          project.getProjects,
          project.getRowType,
          Project.Flags.BOXED)
  }

}

object MapProjectRule {
  val Instance = new MapProjectRule
}
