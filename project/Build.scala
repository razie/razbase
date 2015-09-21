import sbt._
import Keys._
import java.io.File

object V {
  val version      = "0.6.7"
  val scalaVersion = "2.10.4" 
  val organization = "com.razie"

  def snap = (if (V.version endsWith "-SNAPSHOT") "-SNAPSHOT" else "")
}

object MyBuild extends Build {

  def scalatest  = "org.scalatest"  %% "scalatest"       % "1.9.2"
  def junit      = "junit"           % "junit"           % "4.5" //     % "test->default"
  def json       = "org.json"        % "json"            % "20090211"
  def slf4jApi   = "org.slf4j"       % "slf4j-api"       % "1.6.1"
  def logback    = "ch.qos.logback"  % "logback-classic" % "1.0.0"
  def scalaSwing = "org.scala-lang"  % "scala-swing"     % V.scalaVersion

  lazy val root = Project(id="razbase",    base=file("."),
                          settings = defaultSettings ++ Seq()
                  ) aggregate (pbase, w20, w20s, web) dependsOn (pbase, w20, w20s, web)

  lazy val pbase = Project(id="base", base=file("base"),
                          settings = defaultSettings ++
                          Seq(libraryDependencies ++= Seq(scalatest, junit, json, slf4jApi, logback))
                  )

  lazy val w20  = Project(id="s20widgets", base=file("20widgets"),
                          settings = defaultSettings ++
                          Seq(libraryDependencies ++= Seq(scalatest, junit))
                  ) dependsOn (pbase)

  lazy val w20s = Project(id="s20swing", base=file("20swing"),
                          settings = defaultSettings ++
                          Seq(libraryDependencies ++= Seq(scalatest, junit, scalaSwing))
                  ) dependsOn (pbase, w20)

  lazy val web = Project(id="razweb", base=file("web"),
                          settings = defaultSettings ++
                          Seq(
                               libraryDependencies ++= Seq(scalatest, junit)
                              )
                  ) dependsOn (pbase, w20)


  def defaultSettings = baseSettings ++ Seq()
  def baseSettings = Defaults.defaultSettings ++ Seq (
    scalaVersion         := V.scalaVersion,
    version              := V.version,
    organization         := V.organization,
    organizationName     := "Razie's Pub",
    organizationHomepage := Some(url("http://www.razie.com")),

    publishTo <<= version { (v: String) =>
      if(v endsWith "-SNAPSHOT")
        Some ("Sonatype" at "https://oss.sonatype.org/content/repositories/snapshots/")
      else
//        Some ("Sonatype" at "https://oss.sonatype.org/content/repositories/releases/")
        Some ("Sonatype" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    }  )

}
 
