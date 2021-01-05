package com.github.sterkh66.spatial.index.quadtree

import org.locationtech.jts.geom.{Envelope, LineSegment}
import org.scalatest._


class UtilsSpec extends FlatSpec with Matchers {

  val env = new Envelope(3, 5, 1, 3)


  "line segment does not intersect envelope" should "be correct" in  {

    val line = new LineSegment(2, 2, 3, 4)

    Utils.intersects(env, line) should  be (false)
  }

  "line segment completely inside the envelope" should "be correct" in  {

    val line = new LineSegment(3.2, 1.2, 4.2, 2.2)

    Utils.intersects(env, line) should  be (true)
  }

  "line segment matches envelope's boundary" should "be correct" in  {

    val line = new LineSegment(3, 1, 5, 1)

    Utils.intersects(env, line) should  be (true)
  }

  "line segment does intersect envelope side" should "be correct" in  {

    val line = new LineSegment(2, 1, 4, 2)

    Utils.intersects(env, line) should  be (true)
  }

  "line segment does intersect envelope at corner" should "be correct" in  {

    val line = new LineSegment(3, 1, 4, 2)

    Utils.intersects(env, line) should  be (true)
  }
}

