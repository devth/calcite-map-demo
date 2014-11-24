package devth.calcite

import org.eigenbase.rel.{RelNode, ProjectRel, ProjectRelBase}
// import org.eigenbase.relopt.RelOptRule
import org.eigenbase.relopt.Convention
import org.eigenbase.relopt.RelTrait
import org.eigenbase.relopt.RelTraitSet
import org.eigenbase.rel.convert.ConverterRule

import com.typesafe.scalalogging.StrictLogging
import org.eigenbase.relopt.RelOptRule.{operand, none}
import scala.collection.JavaConverters._

/* Base class for planner rules that convert a relational expression to
 * MapDB calling convention. */
abstract class MapConverterRule(
    clazz: Class[_ <: RelNode],
    in: RelTrait,
    val out: Convention,
    description: String) extends ConverterRule(clazz, in, out, description) {
}
