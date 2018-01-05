import com.vividsolutions.jts.io.WKTReader
import com.vividsolutions.jts.geom.{Coordinate, GeometryFactory, Point}
import org.scalatest._
import com.github.sterkh.spatial.{QuadTree, Shape, Node}

class QuadTreeSpec extends FlatSpec with Matchers {

  val wkt = new WKTReader()

  val square1 = Shape("s1", wkt.read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"))
  val square2 = Shape("s2", wkt.read("POLYGON ((10 10, 10 11, 11 11, 11 10, 10 10))"))
  val concave = Shape("cc", wkt.read("POLYGON ((0 4, 3 5, 4 8, 5 5, 8 4, 5 3, 4 0, 3 3, 0 4))"))
  val convex = Shape("cv", wkt.read("POLYGON ((4 8, 5 10, 8 11, 10 7, 8 4, 5 5, 4 8))"))

  "quadtree create index " should "be correct" in  {

    val qt = new QuadTree()

    val qti = qt.createIndex(List(square1, square2), 4)

    qti should not be (null)
  }

  "quadtree simple query index " should "be correct" in  {

    val qt = new QuadTree()

    val qti = qt.createIndex(List(square1, square2), 4)

    val point = new GeometryFactory().createPoint(new Coordinate(3.001, 3.001))

    val result = qti.query(point)

    result should contain theSameElementsAs Set("s1")
  }

  "quadtree concave query index " should "be correct" in  {

    val qt = new QuadTree()

    val qti = qt.createIndex(List(concave), 8)

    val result = qti.query(7.1, 4.1)
//    println(qti.queryNode("13322211"))

    result should contain theSameElementsAs Set("cc")
  }

  "quadtree neighbours query index " should "be correct" in  {

    val qt = new QuadTree()

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

  "get valid attribute" should "be correct" in  {

    val path = getClass.getResource("/shapes.csv").toURI.getPath
    val qt = new QuadTree()

    qt.createIndex(path, 1, 0, 4, List(2))

    val result = qt.getAttr("f1", 2)

    result should equal("1")
  }

  "get invalid attribute" should "be correct" in  {

    val path = getClass.getResource("/shapes.csv").toURI.getPath
    val qt = new QuadTree()

    qt.createIndex(path, 1, 0, 4, List(2))

    val result = qt.getAttr("f1", 10)

    result should equal("")
  }
}
