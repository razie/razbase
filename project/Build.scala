import sbt._
import Keys._
import java.io.File

object V {
  val version      = "0.9.2-SNAPSHOT"
  val scalaVersion = "2.11.8" 
  val organization = "com.razie"

  def snap = (if (V.version endsWith "-SNAPSHOT") "-SNAPSHOT" else "")
}

object MyBuild extends Build {

  def scalatest  = "org.scalatest"  %% "scalatest"       % "2.1.3"
  def junit      = "junit"           % "junit"           % "4.5" //     % "test->default"
  def json       = "org.json"        % "json"            % "20160810"
  def logback    = "ch.qos.logback"  % "logback-classic" % "1.0.13"

  lazy val root = Project(id="razbase",    base=file("."),
                          settings = defaultSettings ++ Seq()
                  ) aggregate (pbase) dependsOn (pbase)

  lazy val pbase = Project(id="base", base=file("base"),
                          settings = defaultSettings ++
                          Seq(libraryDependencies ++= Seq(scalatest, junit, json, logback))
                  )

//  lazy val w20  = Project(id="s20widgets", base=file("20widgets"),
//                          settings = defaultSettings ++
//                          Seq(libraryDependencies ++= Seq(scalatest, junit))
//                  ) dependsOn (pbase)

//  def scalaSwing = "org.scala-lang.modules" %% "scala-swing"    % "2.0.0-M2"
//
//  lazy val w20s = Project(id="s20swing", base=file("20swing"),
//                         settings = defaultSettings ++
//                          Seq(libraryDependencies ++= Seq(scalatest, junit, scalaSwing))
//                  ) dependsOn (pbase, w20)

//  lazy val web = Project(id="razweb", base=file("web"),
//                          settings = defaultSettings ++
//                          Seq(
//                               libraryDependencies ++= Seq(scalatest, junit)
//                              )
//                  ) dependsOn (pbase, w20)


  def defaultSettings = Defaults.defaultSettings ++ Seq (
    scalaVersion         := V.scalaVersion,
    version              := V.version,
    organization         := V.organization,
    organizationName     := "Razie's Pub",
    organizationHomepage := Some(url("http://www.razie.com")),
    licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
    homepage := Some(url("http://www.razie.com")),


    publishTo <<= version { (v: String) =>
      if(v endsWith "-SNAPSHOT")
        Some ("Sonatype" at "https://oss.sonatype.org/content/repositories/snapshots/")
      else
        Some ("Sonatype" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    }  )

}
 
