package devth.calcite

import org.apache.calcite.adapter.enumerable.{PhysType, PhysTypeImpl,
  EnumerableConvention, EnumerableRel, EnumerableRelImplementor}
import org.apache.calcite.linq4j.tree.{Expressions, Expression, Blocks}
import org.apache.calcite.plan.RelOptCluster
import org.apache.calcite.plan.RelOptPlanner
import org.apache.calcite.plan.RelOptTable
import org.apache.calcite.plan.RelTraitSet
import org.apache.calcite.rel.`type`.RelDataType
import org.apache.calcite.rel.`type`.RelDataTypeFactory
import org.apache.calcite.rel.`type`.RelDataTypeField
import org.apache.calcite.rel.core.TableScan

import scala.collection.JavaConverters._

import java.util.{List => JList}
import java.lang.reflect.Method

import com.typesafe.scalalogging.StrictLogging


class MapTableScan(val cluster: RelOptCluster,
  traitSet: RelTraitSet,
  table: RelOptTable,
  val mapTable: MapTable,
  val fields: JList[String] = new java.util.ArrayList[String])
  extends TableScan(cluster, traitSet, table)
  with MapRel with StrictLogging {

  assert(getConvention() == MapRel.CONVENTION)
  assert(mapTable != null)

  override def register(planner: RelOptPlanner) {
    planner.addRule(MapToEnumerableConverterRule.Instance)
    planner.addRule(MapProjectRule.Instance)
  }

  def implement(implementor: MapRel.Implementor) {
    implementor.mapTable = mapTable
    implementor.table = table
  }

}
