package devth.calcite

import org.apache.calcite.rex.RexNode
import org.apache.calcite.linq4j.Enumerator

import scala.collection.JavaConverters._
import scala.collection.mutable
import com.typesafe.scalalogging.StrictLogging


import java.util.{Map => JMap, List => JList, HashMap => JHashMap}

class MapEnumerator(projects: JList[RexNode] = Seq.empty.asJava) extends Enumerator[Any]
  with StrictLogging {

  // TODO: only lookup projected projects in `projects`
  logger.info(s"MapEnumerator with projects $projects")

  type Row = JMap[String, java.lang.Object]

  // Demonstrates:
  // 1. Nested data structures
  // 2. Possibly missing projects (e.g. coords only present for Seattle)
  val data: Seq[Row] = Seq(
    Map("name" -> "foo", "address" -> Map("city" -> "seattle", "state" -> "wa",
      "coords" -> Map("lat" -> "47.609722", "long" -> "-122.333056").asJava).asJava),
    Map("name" -> "bar", "address" -> Map("city" -> "denver", "state" -> "co").asJava),
    Map("name" -> "baz", "address" -> Map("city" -> "chicago", "state" -> "il").asJava),
    Map("name" -> "qux", "address" -> Map("city" -> "san francisco", "state" -> "ca").asJava),
    Map("name" -> "norf", "address" -> Map("city" -> "new york city", "state" -> "ny").asJava)
  ).map(_.asJava)

  // Calcite doesn't like this because we're not returning the expected
  // [Ljava.lang.Object], but we could easily fix that...
  // val iterator = data.map(_.values.toArray).toIterator

  // Produce a static Array containing only the name. Later we could implement
  // the actual projection by flattening the map via cartesian product.
  val iterator = {
    // data.map(jm => Array(jm.get("name"))).toIterator
    data.map { jm =>
      val name: AnyRef = jm.get("name")
      val city: AnyRef = jm.get("address").asInstanceOf[JMap[String, AnyRef]].get("city")

      // Array[AnyRef](name, city)
      Array[AnyRef](name, city)
   }
  }.toIterator


  private var _current: Array[AnyRef] = null

  // Enumerator impl
  def current: Array[AnyRef] = _current

  def moveNext(): Boolean = {
    if (iterator.hasNext) {
      _current = iterator.next()
      true
    } else {
      false
    }
  }

  def reset() {
    throw new UnsupportedOperationException()
  }

  def close() {
  }

}
