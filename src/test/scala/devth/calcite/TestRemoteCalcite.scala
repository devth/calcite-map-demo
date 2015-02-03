package devth.calcite

import com.typesafe.scalalogging.StrictLogging

import org.scalatest.{FunSuite, BeforeAndAfterAll, Matchers, Assertions}

import java.sql.{Connection, DatabaseMetaData, ResultSet}
import java.sql.DriverManager

import org.apache.calcite.avatica.server.{Main, HttpServer}


class TestRemoteCalcite extends FunSuite with BeforeAndAfterAll with StrictLogging {

  var start: Option[HttpServer] = None

  // start a server
  override def beforeAll() {
    start = Some(Main.start(Array("org.apache.calcite.jdbc.MapConnectionFactory")))
  }

  override def afterAll() {
    start.map(_.stop)
  }

  test("Remote query") {
    val port = 8765
    val connection: Connection =
      DriverManager.getConnection(s"jdbc:avatica:remote:url=http://localhost:$port")

    val md: DatabaseMetaData = connection.getMetaData()

    // val crs: ResultSet = md.getCatalogs
    // while (crs.next()) {
    //   logger.info(s"catalogs: ${crs.getString(1)}")
    // }

    // val rs: ResultSet = md.getTables(null, null, "%", null)
    // while (rs.next()) {
    //   logger.info(s"table: ${rs.getString(3)}")
    // }

    val schemas = md.getSchemas
    val schemaMetaData = schemas.getMetaData
    assertResult(2)(schemaMetaData.getColumnCount())
    assertResult("TABLE_SCHEM")(schemaMetaData.getColumnName(1))

    logger.info("schemas:")
    schemas.next()
    logger.info(schemas.getString(1))
    logger.info(schemas.getString(2))
    schemas.next()
    logger.info(schemas.getString(1))
    logger.info(schemas.getString(2))
    schemas.close()


    val query = "select _MAP['name'], _MAP['address']['city'] as rowcount from \"foo\".\"foo\""
    val queryResults = connection.createStatement().executeQuery(query)
    while (queryResults.next) {
      logger.info("query row:")
      logger.info(queryResults.getString(1))
      logger.info(queryResults.getString(2))
    }

    assert(!connection.isClosed)
    connection.close()
    assert(connection.isClosed)
  }


}
