name := "geospatial"

organization := "com.github.sterkh"

version := "0.2"

description := "Java geospatial utils"

scalaVersion := "2.11.8"

licenses := Seq("GPL-3.0" -> url("https://opensource.org/licenses/GPL-3.0"))

homepage := Some(url("https://github.com/sterkh66/geospatial"))

scmInfo := Some(ScmInfo(url("https://github.com/sterkh66/geospatial"), "git@github.com:sterkh66/geospatial.git"))

developers := List(
  Developer(
    id    = "sterkh66",
    name  = "Yuri Z.",
    email = "pmcc@yandex.ru",
    url   = url("https://github.com/sterkh66")
  )
)

pgpReadOnly := false

libraryDependencies ++= Seq(
  "com.opencsv" % "opencsv" % "3.8",
  "com.vividsolutions" % "jts" % "1.13",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"

  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
        