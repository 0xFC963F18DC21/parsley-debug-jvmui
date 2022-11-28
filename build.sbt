Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    scalaVersion     := "2.13.10",
    version          := "0.1.0-SNAPSHOT",
    organization     := "com.github",
    organizationName := "0xfc963f18dc21",
    javacOptions    ++= Seq("-source", "1.8"),
    scalacOptions   ++= Seq("-deprecation", "-unchecked", "-feature")
  )
)

lazy val root = (project in file("."))
  .settings(
    name                := "parsley-gui-db",
    resolvers          ++= Opts.resolver.sonatypeOssSnapshots,
    libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.1-R28",
    libraryDependencies += "com.github.j-mie6" %% "parsley" % "4.0.0+172-82da81f2-SNAPSHOT" % Provided,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test
  )
