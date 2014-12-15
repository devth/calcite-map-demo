package devth.calcite

import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema

import scala.collection.JavaConverters._

class MapSchema extends AbstractSchema {

  val name = "foo"

  override def getTableMap: java.util.Map[String, Table] = {
    val table: Table = new MapTable("foo")
    Map("foo" -> table).asJava
  }

}
