name := "geospatial"

organization := "com.github.sterkh66"

version := "0.1"

scalaVersion := "2.12.4"

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
        