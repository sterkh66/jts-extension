package com.github.sterkh66.spatial.index.quadtree

import org.locationtech.jts.geom.{Envelope, GeometryFactory, LineSegment}


object Utils {

  def intersects (env: Envelope, line: LineSegment): Boolean  = {

    // fast check if envelop fully contains line segment's envelope
    if (env.contains(line.toGeometry(new GeometryFactory()).getEnvelopeInternal))
      return true

    val x = line.p0.x
    val y = line.p0.y

    val vx = line.p1.x - line.p0.x
    val vy = line.p1.y - line.p0.y

    val left = env.getMinX
    val right = env.getMaxX
    val bottom = env.getMinY
    val top = env.getMaxY

    val p = List(-vx, vx, -vy, vy)
    val q = List(x - left, right - x, y - bottom, top - y)

    var u1 = Double.MinValue
    var u2 = Double.MaxValue

    for (i <- 0 to 3) {
      if (p(i) == 0) {
        if (q(i) < 0)
          return false
      } else {
        val t = q(i) / p(i)
        if (p(i) < 0 && u1 < t)
          u1 = t
        else if (p(i) > 0 && u2 > t)
          u2 = t
      }
    }

    if (u1 > u2 || u1 > 1 || u1 < 0)
      return false

    // intersection point
//    println(x + u1 * vx, y + u1 * vy)

    true
  }

}
