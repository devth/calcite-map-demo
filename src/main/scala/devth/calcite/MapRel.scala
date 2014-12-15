package devth.calcite

import org.apache.calcite.rex.RexNode
import org.apache.calcite.rel.RelNode
import org.apache.calcite.plan.Convention
import org.apache.calcite.plan.RelOptTable
import org.apache.calcite.util.Pair

import java.util.ArrayList
import java.util.{List => JList}

/**
 * Relational expression that uses Map calling convention.
 */
object MapRel {
  /** Calling convention for relational operations that occur in MapDB. */
  val CONVENTION = new Convention.Impl("MAP", classOf[MapRel])

  /** Callback for the implementation process that converts a tree of
   * {@link MapRel} nodes into a MapDB query. */
  class Implementor {

    var table: RelOptTable = _
    var mapTable: MapTable = _

    // hold whatever data structures we need here to pass into the enumerator
    // later on

    // @volatile private var fields: Seq[String] = Seq.empty
    // def addFields(fs: Seq[String]) = fields ++= fs
    // def getFields = fields

    @volatile private var projects: JList[RexNode] = null
    def setProjects(ps: JList[RexNode]) { projects = ps }
    def getProjects = projects


    def visitChild(ordinal: Int, input: RelNode) {
      assert(ordinal == 0) // ?
      input.asInstanceOf[MapRel].implement(this)
    }

  }

}
trait MapRel extends RelNode {

  def implement(implementor: MapRel.Implementor)
}
