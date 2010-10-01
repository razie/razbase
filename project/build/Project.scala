import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
  // need to use defs for dependencies - thejy're used in sub-projects
  def scalatest = "org.scalatest" % "scalatest" % "1.2"
  def junit = "junit" % "junit" % "4.5"

  lazy val razBase = project("base", "base", new BaseProject(_))
  lazy val w20Project = project("20widgets", "20widgets", new W20Project(_), razBase)
  lazy val swingProject = project("20w-swing", "20widgets-swing", new SwingProject(_), w20Project)
  lazy val webProject = project("web", "razweb", new WebProject(_), razBase, w20Project)

  class BaseProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit)

    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib") / "json.jar")
  }

  class W20Project(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit)

    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib") / "json.jar")
  }

  class SwingProject(info: ProjectInfo) extends DefaultProject(info) {
    def scalaSwing = "org.scala-lang" % "scala-swing" % "2.8.1.RC1"
    override def libraryDependencies = Set(scalatest, junit, scalaSwing)

    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib") / "json.jar")
  }

  class WebProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit)

    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib") / "json.jar") +++
        (Path.fromFile("lib") / "mime-util.jar")
  }

}

