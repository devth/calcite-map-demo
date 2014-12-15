package devth.calcite

import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.convert.ConverterRule
import org.apache.calcite.plan.RelTraitSet
import org.apache.calcite.adapter.enumerable.EnumerableConvention

/**
 * Rule to convert a relational expression from
 * {@link MapRel#CONVENTION} to {@link EnumerableConvention}.
 */
class MapToEnumerableConverterRule
  extends ConverterRule(classOf[RelNode], MapRel.CONVENTION,
    EnumerableConvention.INSTANCE, "MapToEnumerableConverterRule") {

  override def convert(rel: RelNode): RelNode = {
    val newTraitSet: RelTraitSet = rel.getTraitSet.replace(getOutConvention())
    new MapToEnumerableConverter(rel.getCluster, newTraitSet, rel);
  }

}

object MapToEnumerableConverterRule {
  val Instance = new MapToEnumerableConverterRule
}
