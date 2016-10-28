name := "rle-cache"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.11"

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"
