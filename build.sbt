name := "jts-extension"

organization := "com.github.sterkh"

version := "0.3.0-SNAPSHOT"

description := "Scala extension for Java Topology Suite (JTS)"

scalaVersion := "2.11.8"

licenses := Seq("GPL-3.0" -> url("https://opensource.org/licenses/GPL-3.0"))

homepage := Some(url("https://github.com/sterkh66/jts-extension"))

scmInfo := Some(ScmInfo(url("https://github.com/sterkh66/jts-extension"), "git@github.com:sterkh66/jts-extension.git"))

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
  "org.locationtech.jts" % "jts-core" % "1.17.1",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"

  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
        