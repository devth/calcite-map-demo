package devth.calcite

import net.hydromatic.optiq._
import scala.collection.JavaConverters._

class MapSchemaFactory extends SchemaFactory {

  def create(parentSchema: SchemaPlus, name: String,
    operand: java.util.Map[String, java.lang.Object]): Schema =
      new MapSchema

}
