Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    scalaVersion     := "2.13.10",
    version          := "0.1.0-SNAPSHOT",
    organization     := "com.github",
    organizationName := "0xfc963f18dc21",
    scalacOptions   ++= Seq("-deprecation", "-unchecked", "-feature")
  )
)

lazy val root = (project in file("."))
  .settings(
    name                := "parsley-gui-db",
    resolvers          ++= Opts.resolver.sonatypeOssSnapshots,
    libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.1-R28",
    libraryDependencies += "com.github.j-mie6" %% "parsley" % "4.0.0+158-647533e7-SNAPSHOT" % Provided,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test
  )
