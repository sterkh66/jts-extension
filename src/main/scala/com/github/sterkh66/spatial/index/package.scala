package com.github.sterkh66.spatial

import org.locationtech.jts.geom.Geometry

package object index {
  case class Shape[T: Ordering](id: T, geometry: Geometry)
}
