package devth.calcite

import net.hydromatic.linq4j.{Enumerator, AbstractEnumerable, Enumerable}
import net.hydromatic.linq4j.QueryProvider
import net.hydromatic.optiq._
import net.hydromatic.optiq.impl.AbstractTableQueryable
import net.hydromatic.optiq.impl.java.AbstractQueryableTable
import net.hydromatic.optiq.rules.java.EnumerableConvention
import net.hydromatic.optiq.rules.java.JavaRules

import org.eigenbase.rel.RelNode
import org.eigenbase.relopt.RelOptTable
import org.eigenbase.reltype.{RelDataType, RelDataTypeFactory}
import org.eigenbase.sql.`type`.SqlTypeName
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

  override def toRel(context: RelOptTable.ToRelContext, relOptTable: RelOptTable): RelNode =
    new MapTableScan(context.getCluster(), relOptTable, this)

  def project(fields: java.util.List[String]): Enumerable[AnyRef] = {
    new AbstractEnumerable[AnyRef] {
      def enumerator(): Enumerator[AnyRef] =
        new MapEnumerator(fields).asInstanceOf[Enumerator[AnyRef]]
    }
  }

}
