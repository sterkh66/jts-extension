# Scala extension for Java Topology Suite (JTS)

Provides various spatial utils over JTS such as:
* polygon map quad tree (trie) spatial indexing; fast querying: 1 million points in less than 3 seconds !

Example:
query polygons containing point

```
      import com.github.sterkh.spatial.{QuadTree, Shape}
      import com.vividsolutions.jts.io.WKTReader
      

      val wkt = new WKTReader()
      val shape = Shape("s1", wkt.read("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"))
      val qt = new QuadTree()
      val qti = qt.createIndex(List(shape), 8)
  
      qti.query(3.1, 3.5)
      
      Seq("s1")
```