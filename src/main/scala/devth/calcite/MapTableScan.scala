package devth.calcite

import net.hydromatic.optiq.rules.java.EnumerableConvention
import net.hydromatic.optiq.rules.java.EnumerableRel
import net.hydromatic.optiq.rules.java.EnumerableRelImplementor

import org.eigenbase.rel.RelNode
import org.eigenbase.rel.RelWriter
import org.eigenbase.rel.TableAccessRelBase
import org.eigenbase.relopt.RelOptCluster
import org.eigenbase.relopt.RelOptPlanner
import org.eigenbase.relopt.RelOptTable
import org.eigenbase.relopt.RelTraitSet
import org.eigenbase.reltype.RelDataType
import org.eigenbase.reltype.RelDataTypeFactory
import org.eigenbase.reltype.RelDataTypeField

import net.hydromatic.optiq.rules.java.PhysType
import net.hydromatic.optiq.rules.java.PhysTypeImpl

import net.hydromatic.optiq.rules.java.EnumerableRel
import net.hydromatic.optiq.rules.java.EnumerableRelImplementor

import net.hydromatic.linq4j.expressions.{Expressions, Expression, Blocks}

import scala.collection.JavaConverters._

import java.util.{List => JList}
import java.lang.reflect.Method
import org.eigenbase.sql.`type`.SqlTypeName

import com.typesafe.scalalogging.StrictLogging


class MapTableScan(val cluster: RelOptCluster,
  traitSet: RelTraitSet,
  table: RelOptTable,
  val mapTable: MapTable,
  val fields: JList[String] = new java.util.ArrayList[String])
  extends TableAccessRelBase(cluster, traitSet, table)
  with MapRel with StrictLogging {

  override def register(planner: RelOptPlanner) {
    planner.addRule(MapToEnumerableConverterRule.Instance)
    planner.addRule(MapProjectRule.Instance)
  }

  // override def deriveRowType(): RelDataType = projectRowType

  def implement(implementor: MapRel.Implementor) {

    // val physType = PhysTypeImpl.of(implementor.getTypeFactory(),
    //   getRowType(), pref.preferCustom())
    // val project: Method = classOf[MapTable].getMethod("project", classOf[JList[String]])
    // implementor.result(
    //   physType,
    //   Blocks.toBlock(
    //     Expressions.call(
    //       table.getExpression(classOf[MapTable]),
    //       project,
    //       Expressions.constant(fields))))

    implementor.mapTable = mapTable
    implementor.table = table
  }


}
