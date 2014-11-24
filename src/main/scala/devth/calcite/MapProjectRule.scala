package devth.calcite

import org.eigenbase.rel.{RelNode, ProjectRel, ProjectRelBase}
// import org.eigenbase.relopt.RelOptRule
import org.eigenbase.relopt.Convention
import org.eigenbase.relopt.RelTraitSet
import org.eigenbase.rel.convert.ConverterRule

import com.typesafe.scalalogging.StrictLogging
import org.eigenbase.relopt.RelOptRule.{operand, none}
import scala.collection.JavaConverters._

class MapProjectRule
  extends MapConverterRule(classOf[ProjectRel],
    Convention.NONE, MapRel.CONVENTION, "MapProjectRule")
  with StrictLogging {

  val i = 0

  def convert(rel: RelNode): RelNode = {

    val project: ProjectRel = rel.asInstanceOf[ProjectRel]

    val traitSet: RelTraitSet = project.getTraitSet().replace(out)

    new MapProjectRel(project.getCluster(),
          traitSet,
          project.getChild,
          project.getProjects,
          project.getRowType,
          ProjectRelBase.Flags.BOXED)
  }

}

object MapProjectRule {
  val Instance = new MapProjectRule
}
