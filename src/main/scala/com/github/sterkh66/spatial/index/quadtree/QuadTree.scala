package com.github.sterkh66.spatial.index.quadtree

import com.github.sterkh66.spatial.index.{Shape, SpatialIndex}
import org.locationtech.jts.geom._

/**
  *
  * @tparam T
  */
class QuadTree[T]() extends SpatialIndex {

  var extent: Geometry = _
  var shapes = List.empty[Shape[T]]
  var index: Node[T] = _

  var attributes: Map[String, Map[Int, String]] = _

  def createIndex(shapeList: Seq[Shape[T]], depth: Int): Node[T] = {
    shapes = shapeList.toList

    val geomFactory = new GeometryFactory()
    val geomCollection = geomFactory.createGeometryCollection(shapes.map(_.geometry).toArray)

    extent = geomCollection.getEnvelope
    index = new Node(extent, depth)

    shapes.foreach(shape => {
      index.insert(shape)
    })
    index
  }

  def getIndex: Node[T] = {
    index
  }

  def queryIndex(point: Point): Set[T] = {
    index.query(point)
  }

  def queryIndex(x: Double, y: Double): Set[T] = {
    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    index.query(point)
  }

  def writeIndex(filePath: String): Unit = {
    import java.io.FileOutputStream
    import java.io.ObjectOutputStream

    val out = new ObjectOutputStream(new FileOutputStream(filePath))
    out.writeObject(index)
  }

  def readIndex(filePath: String): Unit = {
    import java.io.FileInputStream
    import java.io.ObjectInputStream

    val in = new ObjectInputStream(new FileInputStream(filePath))
    index = in.readObject().asInstanceOf[Node[T]]
  }

}
