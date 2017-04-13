import xsbti.compile

name := "auctions"

version := "0.1.0-alpha"

scalaVersion := "2.12.1"

// Useful scala compiler options
scalacOptions ++= Seq(
  "-feature",  // tells the compiler to provide information about misused language features
  "-language:implicitConversions",  // eliminates the need to import implicit conversions for each usage
  "-Ywarn-unused-import",
  "-Ywarn-dead-code"
)

// Additional dependencies
libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.typelevel"  %% "squants"  % "1.2.0"
)

// In our project Java depends on Scala, but not the other way round!
compileOrder := CompileOrder.ScalaThenJava