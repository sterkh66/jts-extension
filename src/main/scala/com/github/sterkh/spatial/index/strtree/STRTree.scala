package com.github.sterkh.spatial.index.strtree

import com.github.sterkh.spatial.index.{Shape, SpatialIndex}
import org.locationtech.jts.geom.{Coordinate, GeometryFactory, Point}
import org.locationtech.jts.index.strtree.{ItemBoundable, ItemDistance, STRtree}


/**
  *
  */
class STRTree[T] extends SpatialIndex {

  import STRTree._

  val strTree: STRtree = new STRtree()

  def createIndex(features: Seq[Shape[T]]): Unit = {

    features.foreach(x => {
      val env = x.geometry.getEnvelopeInternal
      strTree.insert(env, x)
    })
  }

  def queryIndex(x: Double, y: Double): Set[T] = {
    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    queryIndex(point)
  }

  def queryIndex(x: Double, y: Double, radiusDeg: Double): Set[T] = {
    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    queryIndex(point, radiusDeg)
  }

  def queryIndex(x: Double, y: Double, neighboursMax: Int): Set[T] = {
    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    queryIndex(point, radiusDeg = 0.0, neighboursMax)
  }

  def queryIndex(x: Double, y: Double, radiusDeg: Double, neighboursMax: Int): Set[T] = {
    val point = new GeometryFactory().createPoint(new Coordinate(x, y))
    queryIndex(point,radiusDeg, neighboursMax)
  }

  def queryIndex(point: Point, radiusDeg: Double = 0.0, neighboursMax: Int = 3): Set[T] = {
    import collection.JavaConverters._

    val hits = strTree.query(point.getEnvelopeInternal).asScala.map(_.asInstanceOf[Shape[T]])

    hits.filter(_.geometry.intersects(point)).map(_.id) match {
      case s @ Seq(_, _*) => s.toSet
      case Seq() if radiusDeg > 0.0 =>
        val neighbours = strTree.nearestNeighbour(point.getEnvelopeInternal,
          Shape(0L, point), new Distance, neighboursMax).map(_.asInstanceOf[Shape[T]])

        neighbours.filter(_.geometry.isWithinDistance(point, radiusDeg)).map(_.id).toSet

      case _ => Set.empty[T]
    }
  }
}

object STRTree extends Serializable {

  class Distance[T] extends ItemDistance {
    override def distance(itemBoundable: ItemBoundable, itemBoundable1: ItemBoundable): Double = {
      val geom = itemBoundable.getItem.asInstanceOf[Shape[T]].geometry
      val geom1 = itemBoundable1.getItem.asInstanceOf[Shape[T]].geometry
      geom.distance(geom1)
    }
  }
}
