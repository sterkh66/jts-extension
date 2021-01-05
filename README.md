# Scala spatial extension over Java Topology Suite (JTS)

Provides various spatial utils over JTS such as:
* polygon map quad tree (trie) spatial indexing
  * implementing https://en.wikipedia.org/wiki/Quadtree
  * fast querying: 1 million points in less than 3 seconds !
* improved STRTree index search with result refinement and neighboors detecting

Example:
Query polygons containing point

```scala
      import com.github.sterkh.spatial.index.quadtree.QuadTree
      import com.github.sterkh.spatial.index.Shape
      import org.locationtech.jts.io.WKTReader
      

      val wkt = new WKTReader()
      val shape = Shape("s1", wkt.read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"))
      val qt = new QuadTree[String]()

      qt.createIndex(List(shape), 8)
      qt.queryIndex(3.1, 3.5)
      
      Seq("s1")
```