package com.github.sterkh66.spatial.index.strtree

import com.github.sterkh66.spatial.index.Shape
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import org.locationtech.jts.io.WKTReader
import org.scalatest._


class STRTreeSpec extends FlatSpec with Matchers {

  val wkt = new WKTReader()

  val square1 = Shape("s1", wkt.read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"))
  val square2 = Shape("s2", wkt.read("POLYGON ((10 10, 10 11, 11 11, 11 10, 10 10))"))
  val concave = Shape("cc", wkt.read("POLYGON ((0 4, 3 5, 4 8, 5 5, 8 4, 5 3, 4 0, 3 3, 0 4))"))
  val convex = Shape("cv", wkt.read("POLYGON ((4 8, 5 10, 8 11, 10 7, 8 4, 5 5, 4 8))"))

  "strtree create index " should "be correct" in  {

    val qt = new STRTree[String]()

    qt.createIndex(List(square1, square2))

  }

  "strtree simple query index " should "be correct" in  {

    val qt = new STRTree[String]()
    qt.createIndex(List(square1, square2))
    val point = new GeometryFactory().createPoint(new Coordinate(3.001, 3.001))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s1")
  }

  "strtree concave query index " should "be correct" in  {

    val qt = new STRTree[String]()
    qt.createIndex(List(concave))
    val point = new GeometryFactory().createPoint(new Coordinate(7.1, 4.1))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("cc")
  }

  "strtree with neighbours check query index " should "be correct" in  {

    val qt = new STRTree[String]()
    qt.createIndex(List(concave, convex))
    val point = new GeometryFactory().createPoint(new Coordinate(11.0, 11.0))

    val result = qt.queryIndex(point, 3.0, 2)

    result should contain theSameElementsAs Set("cv")
  }

}
