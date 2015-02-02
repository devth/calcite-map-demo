package org.apache.calcite.jdbc

import devth.calcite.MapSchema
import java.sql.DriverManager

import org.apache.calcite.avatica.Meta.Factory
import org.apache.calcite.schema.SchemaPlus

class MapConnectionFactory extends Factory {

  def create(x: java.util.List[String]): org.apache.calcite.avatica.Meta = {
    val schema = new MapSchema
    val connection = DriverManager.getConnection("jdbc:calcite:")
    val calciteConnection: CalciteConnection = connection.unwrap(classOf[CalciteConnection])
    val rootSchema: SchemaPlus = calciteConnection.getRootSchema()
    rootSchema.add(schema.name, schema)
    new CalciteMetaImpl(connection.asInstanceOf[CalciteConnectionImpl])
  }

}
