package devth.calcite

import org.apache.calcite.adapter.enumerable.{EnumerableConvention, EnumerableRules}
import org.apache.calcite.adapter.java.AbstractQueryableTable
import org.apache.calcite.linq4j.QueryProvider
import org.apache.calcite.linq4j.{Enumerator, AbstractEnumerable, Enumerable}
import org.apache.calcite.plan.RelOptTable
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.`type`.{RelDataType, RelDataTypeFactory}
import org.apache.calcite.rex.RexNode
import org.apache.calcite.schema.{TranslatableTable, SchemaPlus}
import org.apache.calcite.schema.impl.AbstractTableQueryable
import org.apache.calcite.sql.`type`.SqlTypeName

import java.util.{List => JList}

class MapTable(name: String) extends AbstractQueryableTable(classOf[Seq[AnyRef]]) with TranslatableTable {

  // getRowType – This method should return the table row headers and their
  // types in two arrays like [Name, Age, Country] and [String, Integer, String]
  // _MAP type supports heirarchical structure.
  override def getRowType(typeFactory: RelDataTypeFactory): RelDataType =
    typeFactory.builder().add("_MAP",
      typeFactory.createMapType(
        typeFactory.createSqlType(SqlTypeName.VARCHAR),
        typeFactory.createSqlType(SqlTypeName.ANY))).build()

  // asQueryable – returns an Enumerator. The Enumerator will have methods to
  // iterate the actual rows in the table.
  override def asQueryable[AnyRef](queryProvider: QueryProvider,
    schema: SchemaPlus, tableName: String) =
      new AbstractTableQueryable[AnyRef](queryProvider, schema, this, tableName) {
        def enumerator(): Enumerator[AnyRef] =
          new MapEnumerator().asInstanceOf[Enumerator[AnyRef]]
      }

  override def toRel(context: RelOptTable.ToRelContext, relOptTable: RelOptTable): RelNode = {
    val cluster = context.getCluster
    new MapTableScan(cluster, cluster.traitSetOf(MapRel.CONVENTION),
      relOptTable, this)
  }

  def project(): Enumerable[AnyRef] = {
    new AbstractEnumerable[AnyRef] {
      def enumerator(): Enumerator[AnyRef] =
        new MapEnumerator(null).asInstanceOf[Enumerator[AnyRef]]
    }
  }

}
