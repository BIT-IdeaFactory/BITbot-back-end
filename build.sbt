name := "BitBot"

version := "0.1"

scalaVersion := "2.12.7"
lazy val akkaVersion = "2.5.18"
lazy val akkaHttpVersion  = "10.1.5"
lazy val sangriaVersion = "1.4.2"
lazy val reactiveMongoVersion = "0.16.0"
lazy val sangriaSprayVersion = "1.0.0"
lazy val sangriaCircleVersion = "1.2.1"
lazy val sprayJSONVersion = "10.0.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % sprayJSONVersion,
  "org.reactivemongo" %% "reactivemongo" % reactiveMongoVersion,
  "org.sangria-graphql" %% "sangria" % sangriaVersion,
  "org.sangria-graphql" %% "sangria-spray-json" % sangriaSprayVersion,
  "com.pauldijou" %% "jwt-core" % "1.0.0",
  // reactive momgo deps
  "org.apache.logging.log4j" % "log4j-api" % "2.11.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
)

