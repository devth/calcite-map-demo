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
  table: RelOptTable,
  val mapTable: MapTable,
  val fields: JList[String] = new java.util.ArrayList[String])
  extends TableAccessRelBase(cluster, cluster.traitSetOf(EnumerableConvention.INSTANCE), table)
  with EnumerableRel with StrictLogging {

  override def register(planner: RelOptPlanner) {
    planner.addRule(new MapProjectRule)
  }

  // What is this?
  override def deriveRowType(): RelDataType = {
    logger.debug("Table scan derive row type call received.")
    val fieldList = table.getRowType().getFieldList()
    val builder = getCluster().getTypeFactory().builder()
    fieldList.asScala.foreach { field => builder.add(field) }
    builder.build()
  }


  def implement(implementor: EnumerableRelImplementor, pref:
    EnumerableRel.Prefer): EnumerableRel.Result = {

    val physType = PhysTypeImpl.of(implementor.getTypeFactory(),
      getRowType(), pref.preferCustom())

    val project: Method = classOf[MapTable].getMethod("project", classOf[JList[String]])

    implementor.result(
      physType,
      Blocks.toBlock(
        Expressions.call(
          table.getExpression(classOf[MapTable]),
          project,
          Expressions.constant(fields))))
  }


}
