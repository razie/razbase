import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
  override def managedStyle = ManagedStyle.Maven
  val publishTo = 
    if (version.toString endsWith "-SNAPSHOT") 
      "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
    else 
      "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/"
  Credentials(Path.userHome / ".ivy2.credentials", log)

  val SCALAVER = "2.9.1"
    
  // need to use defs for dependencies - thejy're used in sub-projects
  def scalatest = "org.scalatest"  % "scalatest_2.9.1" % "1.6.1"
  def junit     = "junit"          % "junit"           % "4.5"
  def json      = "org.json"       % "json"            % "20090211"
  def slf4jApi  = "org.slf4j"      % "slf4j-api"       % "1.6.1"
  def logback   = "ch.qos.logback" % "logback-classic" % "0.9.28" 

  lazy val razBase      = project("base",      "base",            new BaseProject(_))
  lazy val w20Project   = project("20widgets", "20widgets",       new W20Project(_),   razBase)
  lazy val swingProject = project("20swing",   "20widgets-swing", new SwingProject(_), w20Project)
  lazy val webProject   = project("web",       "razweb",          new WebProject(_),   razBase, w20Project)

  class BaseProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit, json, slf4jApi, logback)
  }

  class W20Project(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit)
  }

  class SwingProject(info: ProjectInfo) extends DefaultProject(info) {
    def scalaSwing = "org.scala-lang" % "scala-swing" % SCALAVER
    override def libraryDependencies = Set(scalatest, junit, scalaSwing)
  }

  class WebProject(info: ProjectInfo) extends DefaultProject(info) {
    override def libraryDependencies = Set(scalatest, junit)

    override def unmanagedClasspath =
      super.unmanagedClasspath +++
        (Path.fromFile("lib") / "mime-util.jar")
  }
}

