package devth.calcite

import org.apache.calcite.rel.RelNode
import org.apache.calcite.plan.{Convention, RelTrait}
import org.apache.calcite.rel.convert.ConverterRule

/* Base class for planner rules that convert a relational expression to
 * MapDB calling convention. */
abstract class MapConverterRule(
    clazz: Class[_ <: RelNode],
    in: RelTrait,
    val out: Convention,
    description: String) extends ConverterRule(clazz, in, out, description) {
}
