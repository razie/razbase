import sbt._

class Razbase(info: ProjectInfo) extends DefaultProject(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val junit     = "junit" % "junit" % "4.5" % "test->default"

//  val razXml  = "com.razie" %% "razxml"          % "0.1-SNAPSHOT"
  
  override def mainScalaSourcePath = "src"
  override def testScalaSourcePath = "test_src"

}

