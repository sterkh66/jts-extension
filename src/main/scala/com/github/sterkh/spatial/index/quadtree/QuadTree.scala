package com.github.sterkh.spatial.index.quadtree

import java.io.{BufferedReader, FileReader}

import com.github.sterkh.spatial.index.Shape
import com.opencsv.CSVReader
import org.locationtech.jts.geom._
import org.locationtech.jts.io.{WKTReader, WKTWriter}

/**
  *
  * @tparam T
  */
class QuadTree[T]() extends Serializable {

  var extent: Geometry = _
  var shapes = List.empty[Shape[T]]
  var index: Node[T] = _

  var attributes: Map[String, Map[Int, String]] = _

  def createIndex(shapeList: List[Shape[T]], depth: Int): Node[T] = {

    shapes = shapeList

    val geomFactory = new GeometryFactory()
    val geomCollection = geomFactory.createGeometryCollection(shapes.map(_.geometry).toArray)

    extent = geomCollection.getEnvelope
    index = new Node(extent, depth)

    shapes.foreach(shape => {
      index.insert(shape)
    })

    index
  }

  def createIndex(csvFilePath: String,
                  idColumn: Int,
                  wktColumn: Int,
                  depth: Int,
                  attrs: List[Int] = List.empty[Int],
                  separator: Char = ',',
                  quoteChar: Char = '\"',
                  fromLine: Int = 1): Node[T] = {

    import collection.JavaConverters._

    val bufReader = new BufferedReader(new FileReader(csvFilePath))

    val csvReader = new CSVReader(bufReader, separator, quoteChar, fromLine)

    val lines: List[Array[String]] = csvReader.readAll().asScala.toList

    val wkt = new WKTReader()

    val shapeList = lines.map(line => {
      Shape(line(idColumn).asInstanceOf[T], wkt.read(line(wktColumn)))
    })

    val lineBuf = scala.collection.mutable.Map[String, Map[Int, String]]()

    // read attributes
    if (attrs.nonEmpty) {
      lines.foreach(line => {
        val id = line(idColumn)
        val attrBuf = scala.collection.mutable.Map[Int, String]()
        attrs.foreach(attr => {
          attrBuf(attr) = line(attr)
        })
        lineBuf(id) = attrBuf.toMap
      })
      attributes = lineBuf.toMap
    }

    createIndex(shapeList, depth)
  }
  
  def getIndex: Node[T] = {
    index
  }

  def getAttr(id: String, attr: Int): String = {
    var res = ""
    
    if (attributes != null) {
      val map = attributes.getOrElse(id, Map.empty[Int, String])
      res = map.getOrElse(attr, "")
    }
    
    res
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
