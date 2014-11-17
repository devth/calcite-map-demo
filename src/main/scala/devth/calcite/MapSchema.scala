package devth.calcite

import net.hydromatic.optiq.Table
import net.hydromatic.optiq.impl.AbstractSchema

import scala.collection.JavaConverters._

class MapSchema extends AbstractSchema {

  val name = "foo"

  override def getTableMap: java.util.Map[String, Table] = {
    val table: Table = new MapTable("foo")
    Map("foo" -> table).asJava
  }

}
