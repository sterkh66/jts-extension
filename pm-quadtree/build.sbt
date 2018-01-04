name := "pm-quadtree"

organization := "sterkh66"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.opencsv" % "opencsv" % "3.8",
  "com.vividsolutions" % "jts" % "1.13",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

//resolvers += DefaultMavenRepository

//publishTo := Some(DefaultMavenRepository)
publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/releases")


        