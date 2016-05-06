
name := "razbase"

retrieveManaged := true // copy libs in lib_managed

publishMavenStyle := true

publishArtifact in Test := false

publishArtifact in (Compile, packageDoc) := false

pomIncludeRepository := { x => false }

pomExtra := (
  <scm>
    <url>git@github.com:razie/razbase.git</url>
    <connection>scm:git:git@github.com:razie/razbase.git</connection>
  </scm>
  <developers>
    <developer>
      <id>razie</id>
      <name>Razvan Cojocaru</name>
      <url>http://www.razie.com</url>
    </developer>
  </developers>
)

