package com.github.sterkh66.spatial.index.quadtree

import com.github.sterkh66.spatial.index.Shape
import org.locationtech.jts.io.WKTReader
import org.locationtech.jts.geom.{Coordinate, GeometryFactory}
import org.scalatest._

class QuadTreeSpec extends FlatSpec with Matchers {

  val wkt = new WKTReader()

  val square1 = Shape("s1", wkt.read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"))
  val square2 = Shape("s2", wkt.read("POLYGON ((10 10, 10 11, 11 11, 11 10, 10 10))"))
  val square3 = Shape("s3", wkt.read("POLYGON ((4 3, 4 4, 5 4, 5 3, 4 3))"))
  val square4 = Shape("s4", wkt.read("POLYGON ((3 2, 3 3, 4 3, 4 2, 3 2))"))

  val concave = Shape("cc", wkt.read("POLYGON ((0 4, 3 5, 4 8, 5 5, 8 4, 5 3, 4 0, 3 3, 0 4))"))
  val convex = Shape("cv", wkt.read("POLYGON ((4 8, 5 10, 8 11, 10 7, 8 4, 5 5, 4 8))"))

  "quadtree create index " should "be correct" in  {

    val qt = new QuadTree[String]()
    val qti = qt.createIndex(List(square1, square2), 4)

    qti should not be null
  }

  "quadtree count nodes" should "be correct" in  {

    val qt = new QuadTree[String]()
    val qti = qt.createIndex(List(square1, square2), 4)

    qti.countNodes(qti) should be (45)
    qti.countNodes(qti, leavesOnly = true) should be (34)
  }

  "quadtree simple query index " should "be correct" in  {

    val qt = new QuadTree[String]()
    val qti = qt.createIndex(List(square1, square2), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(3.001, 3.001))

    val result = qti.query(point)

    result should contain theSameElementsAs Set("s1")
  }

  "quadtree simple query index by parent" should "be correct" in  {

    val qt = new QuadTree[String]()
    qt.createIndex(List(square1, square2), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(3.001, 3.001))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s1")
  }

  "quadtree concave query index " should "be correct" in  {

    val qt = new QuadTree[String]()
    val qti = qt.createIndex(List(concave), 8)

    val result = qti.query(7.1, 4.1)
//    println(qti.queryNode("13322211"))

    result should contain theSameElementsAs Set("cc")
  }

  "quadtree neighbours query index " should "be correct" in  {

    val qt = new QuadTree[String]()
    val qti = qt.createIndex(List(concave, convex), 8)

    val result = qti.query(7.2, 4.2)
//    println(qti.queryNode("3013322"))

    result should contain theSameElementsAs Set("cc")
  }

  "intersects" should "be correct" in  {

    val shape1 = new WKTReader().read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))")
    val shape2 = new WKTReader().read("POLYGON ((3 4, 3 5, 4 5, 4 4, 3 4))")

//    println(shape1.intersection((shape2)))
//    println(square1.geometry.intersection((square2.geometry)).isEmpty)

    shape1.touches(shape2) should equal(true)
    shape1.contains(shape2) should equal(false)
    shape1.intersects(shape2) should equal(true)
  }

  "quadtree query index with nearest neighbour s1" should "be correct" in  {

    val qt = new QuadTree[String](3.0)
    qt.createIndex(List(square1, square2), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(5.0, 5.0))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s1")
  }

  "quadtree query index with nearest neighbour s2" should "be correct" in  {

    val qt = new QuadTree[String](3.0)
    qt.createIndex(List(square1, square2), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(12.0, 12.0))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s2")
  }

  "quadtree query index with nearest neighbour too far" should "be correct" in  {

    val qt = new QuadTree[String](3.0)
    qt.createIndex(List(square1, square2), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(15.0, 15.0))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set()
  }

  "quadtree query index with nearest neighbour contest 1" should "be correct" in  {

    val qt = new QuadTree[String](2.0)
    qt.createIndex(List(square1, square3), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(4.0, 2.0))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s1")
  }

  "quadtree query index with nearest neighbour contest 2" should "be correct" in  {

    val qt = new QuadTree[String](2.0)
    qt.createIndex(List(square1, square3, square4), 4)
    val point = new GeometryFactory().createPoint(new Coordinate(4.5, 2.5))

    val result = qt.queryIndex(point)

    result should contain theSameElementsAs Set("s1")
  }
}
