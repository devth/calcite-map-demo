package devth.calcite

import scala.collection.mutable

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

import net.hydromatic.optiq.SchemaPlus
import net.hydromatic.optiq.jdbc.OptiqConnection

import org.scalatest.FunSuite

import com.typesafe.scalalogging.StrictLogging

class TestMapCalcite extends FunSuite with StrictLogging {

  val schema = new MapSchema
  Class.forName("net.hydromatic.optiq.jdbc.Driver")
  val connection = DriverManager.getConnection("jdbc:calcite:")
  val optiqConnection: OptiqConnection = connection.unwrap(classOf[OptiqConnection])
  val rootSchema: SchemaPlus = optiqConnection.getRootSchema()
  rootSchema.add(schema.name, schema)
  val statement = connection.createStatement();

  test("Select everything") {
    printResults(statement.executeQuery("""
      select * from "foo"."foo"
    """))
  }

  test("Select a few things") {
    val results: ResultSet = statement.executeQuery("""
      select _MAP['name'], _MAP['address']['city'] from "foo"."foo"
      """)
    printResults(results)
  }

  test("Aggregate") {
    // count
    val countResult = statement.executeQuery("""
      select count(_MAP['name']) as rowcount from "foo"."foo"
    """)
    printResults(countResult)
  }

  def printResults(results: ResultSet) {
    val meta = results.getMetaData
    while (results.next()) {
      val vals = (1 to meta.getColumnCount).map { i =>
        results.getString(i)
      }
      println(vals.mkString(","))
    }
  }

  connection.close()

}
