name := "BitBot"

version := "0.1"

scalaVersion := "2.12.7"
lazy val akkaVersion = "2.15.16"
lazy val akkaHttpVersion  = "10.1.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "org.reactivemongo" %% "reactivemongo" % "0.11.14"
)
