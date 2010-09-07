import sbt._

class Razbase(info: ProjectInfo) extends DefaultProject(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.2"

  override def mainScalaSourcePath = "src"
  override def testScalaSourcePath = "test_src"
}

