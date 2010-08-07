import sbt._

class Razbase(info: ProjectInfo) extends DefaultProject(info)
{
  val scalaToolsSnapshots = ScalaToolsSnapshots
  val scalatest = "org.scalatest" % "scalatest" % "1.2"

  lazy val hi = task { println("Hello World"); None }

  override def outputDirectoryName = "bin"

  override def mainScalaSourcePath = "src"
//  override def mainResourcesPath = "resources"

  override def testScalaSourcePath = "test_src"
//  override def testResourcesPath = "test-resources"

}

