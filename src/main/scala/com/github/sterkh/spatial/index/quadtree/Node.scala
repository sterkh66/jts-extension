package com.github.sterkh.spatial.index.quadtree

import com.github.sterkh.spatial.index.Shape
import org.locationtech.jts.geom._
import org.locationtech.jts.io.WKTWriter


/**
  *
  * @param extent
  * @param depth
  * @param level
  * @param id
  * @tparam T
  */
class Node[T](extent: Geometry, depth: Int = 8, level: Int = 0, id: Int = 0) extends Serializable {

  var northWest: Node[T] = _
  var northEast: Node[T] = _
  var southWest: Node[T] = _
  var southEast: Node[T] = _

  var ids = Set.empty[Shape[T]]

  //  println(id)
  private val maxLevel = if (depth > QT_NODE_MAX_LEVEL) QT_NODE_MAX_LEVEL else depth

  private def toPolygon(env: Envelope): Polygon = {

    val coordinates = Array(
      new Coordinate(env.getMinX, env.getMinY),
      new Coordinate(env.getMinX, env.getMaxY),
      new Coordinate(env.getMaxX, env.getMaxY),
      new Coordinate(env.getMaxX, env.getMinY),
      new Coordinate(env.getMinX, env.getMinY)
    )

    val factory = new GeometryFactory()
    val linear = new GeometryFactory().createLinearRing(coordinates)

    new Polygon(linear, null, factory)
  }

  private def toWKT(geometry: Geometry): String = {

    val wkt = new WKTWriter()

    wkt.write(geometry)
  }

  private def addShape(shape: Shape[T]){
    ids = ids + shape
    //    if (level == 5 && nodeIdtoString(id) == "13322")
    //      println(level, id, nodeIdtoString(id), ids)
  }

  private def subdivide(): Boolean = {

    val env = extent.getEnvelopeInternal
    val cx = env.centre.x
    val cy = env.centre.y
    val dw = env.getWidth / 2.0
    val dh = env.getHeight / 2.0

    northWest = new Node(toPolygon(new Envelope(cx - dw, cx, cy, cy + dh)), maxLevel, level + 1, (id << 2) + 0)
    northEast = new Node(toPolygon(new Envelope(cx, cx + dw, cy, cy + dh)), maxLevel, level + 1, (id << 2) + 1)
    southWest = new Node(toPolygon(new Envelope(cx - dw, cx, cy - dh, cy)), maxLevel, level + 1, (id << 2) + 2)
    southEast = new Node(toPolygon(new Envelope(cx, cx + dw, cy - dh, cy)), maxLevel, level + 1, (id << 2) + 3)

    true
  }

  def queryNode(nodeId: String): Set[T] = {

    if (nodeId.nonEmpty  && northWest != null) {
      val quad = nodeId.head.toInt
      quad & 0x03 match {
        case 0x00 => northWest.queryNode(nodeId.tail)
        case 0x01 => northEast.queryNode(nodeId.tail)
        case 0x02 => southWest.queryNode(nodeId.tail)
        case 0x03 => southEast.queryNode(nodeId.tail)
      }
    } else ids.map(_.id)
  }

  def queryNode(nodeId: Int): Set[T] = {
    queryNode(nodeIdToString(nodeId))
  }

  def countNodes(node: Node[T], leavesOnly: Boolean = false): Int = {
    var numNodes = 0

    if (node.northWest != null) {
      if (!leavesOnly)
        numNodes += 1
      numNodes += countNodes(node.northWest, leavesOnly)
      numNodes += countNodes(node.northEast, leavesOnly)
      numNodes += countNodes(node.southWest, leavesOnly)
      numNodes += countNodes(node.southEast, leavesOnly)
    } else {
      numNodes += 1
    }

    numNodes
  }

  def nodeIdToString(nodeId: Int): String = {
    var idStr: String = ""

    var id = nodeId

    while (id > 0){
      idStr = idStr + (id & 0x03).toString
      id = id >> 2
    }

    idStr.reverse
  }

  def nodeIdFromString(str: String): Int = {
    var nodeId: Int = 0
    str.reverse.foreach(c => (nodeId << 2) + c.toInt )
    nodeId
  }

  def insert(shape: Shape[T]): Unit = {

    val geom = shape.geometry

    //    if (level == 5 && nodeIdToString(id) == "13322")
    //      println(level, id, nodeIdToString(id), ids, extent, extent.intersection(geom))

    if (level <= maxLevel) {

      if (geom.contains(extent)) {
        addShape(shape)
        //        println(nodeIdToString(id),"contains")
        return
      }

      val intersection = geom.intersection(extent)

      if (!intersection.isEmpty) {
        val intShape = Shape(shape.id, intersection)

        addShape(shape)

        if (level < maxLevel) {

          if (northWest == null) {
            subdivide()
          }

          northWest.insert(intShape)
          northEast.insert(intShape)
          southWest.insert(intShape)
          southEast.insert(intShape)
        }
      }
    }
  }

  private def queryPoint(point: Point): Set[Shape[T]] = {

    var result = Set.empty[Shape[T]]

    // если есть попадание в текущий квандрант
    if (extent.getEnvelopeInternal.contains(point.getX, point.getY)){
      // добавить в результирующий набор все ID из текущего квадранта
      result = ids
      //      println(nodeIdtoString(id), id, level, ids)

      // и проверить дочерние квадранты, если они есть
      if (northWest != null){
        result = northWest.queryPoint(point) ++
          northEast.queryPoint(point) ++
          southWest.queryPoint(point) ++
          southEast.queryPoint(point)
      } else {
        // если дочерних нет, уточнить результат при необходимости
        if (result.size > 1){
          //println("refine", nodeIdtoString(id))
          result = result.filter(s => s.geometry.contains(point))
        }
      }
    }

    result
  }

  private def queryPoint(x: Double, y: Double): Set[Shape[T]] = {

    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    queryPoint(point)
  }

  def query(x: Double, y: Double): Set[T] = {

    queryPoint(x, y).map(_.id)
  }

  def query(point: Point): Set[T] = {

    queryPoint(point).map(_.id)
  }
}
