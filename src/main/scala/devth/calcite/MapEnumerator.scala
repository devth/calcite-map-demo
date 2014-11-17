package devth.calcite

import net.hydromatic.linq4j.Enumerator
import scala.collection.JavaConverters._
import scala.collection.mutable

import java.util.{Map => JMap, List => JList, HashMap => JHashMap}

class MapEnumerator(fields: JList[String] = Seq.empty.asJava) extends Enumerator[AnyRef] {

  // TODO: only lookup projected fields in `fields`

  type Row = JMap[String, java.lang.Object]

  // Demonstrates:
  // 1. Nested data structures
  // 2. Possibly missing fields (e.g. coords only present for Seattle)
  val data: Seq[Row] = Seq(
    Map("name" -> "foo", "address" -> Map("city" -> "seattle", "state" -> "wa",
      "coords" -> Map("lat" -> "47.609722", "long" -> "-122.333056").asJava).asJava),
    Map("name" -> "bar", "address" -> Map("city" -> "denver", "state" -> "co").asJava),
    Map("name" -> "baz", "address" -> Map("city" -> "chicago", "state" -> "il").asJava),
    Map("name" -> "qux", "address" -> Map("city" -> "san francisco", "state" -> "ca").asJava),
    Map("name" -> "norf", "address" -> Map("city" -> "new york city", "state" -> "ny").asJava)
  ).map(_.asJava)

  val iterator = data.toIterator

  private var _current: AnyRef = null

  // Enumerator impl
  def current: AnyRef = _current

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
