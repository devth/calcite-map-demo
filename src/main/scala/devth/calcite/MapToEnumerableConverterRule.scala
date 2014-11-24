package devth.calcite

import net.hydromatic.optiq.rules.java.EnumerableConvention

import org.eigenbase.rel.RelNode
import org.eigenbase.rel.convert.ConverterRule
import org.eigenbase.relopt.RelTraitSet

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
