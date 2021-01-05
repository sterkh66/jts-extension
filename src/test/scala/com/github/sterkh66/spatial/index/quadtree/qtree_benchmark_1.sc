import com.github.sterkh66.spatial.index.SpatialIndex
import com.github.sterkh66.spatial.index.quadtree.QuadTree

import scala.util.Random


val rand = new Random()

val (lon1,lon2,lat1,lat2) = (35.155f, 40.218f, 54.254f, 56.979f)
val (dlon, dlat) = (lon2 - lon1, lat2 - lat1)

val coords = (1 to 100).map(e => {(lon1 + dlon * rand.nextFloat, lat1 + dlat * rand.nextFloat)})

val qt = new QuadTree[String]()
val shapes = SpatialIndex.readShapes[String]("/Users/yuri/Documents/mdt/transp_districts.csv",
  1, 0, 1)
val qti = qt.createIndex(shapes, 10)

coords.foreach(c => {println(qti.query(c._1, c._2))})
