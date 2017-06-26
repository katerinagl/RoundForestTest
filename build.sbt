
name := "RoundForest"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.7"

libraryDependencies+= "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.7"

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"

libraryDependencies += "com.opencsv" % "opencsv" % "3.9"
