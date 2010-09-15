import sbt._

class Razbase(info: ProjectInfo) extends DefaultProject(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val junit     = "junit" % "junit" % "4.5" % "test->default"

  val razXml  = "com.razie" %% "razxml"          % Vals.razXmlVer
  
  override def mainScalaSourcePath = "src"
  override def testScalaSourcePath = "test_src"

  import java.io._

  lazy val bubu = task {
    println("bubu-task is me")
    scala.io.Source.fromFile("/home/razvanc/.profile").getLines foreach println
    None
  }
}

