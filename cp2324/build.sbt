name := "CP2324"

version := "1.0"

scalaVersion := "2.12.18"

cancelable in Global := true

// set in run fork := true

resolvers ++= Seq(
"Sonatype OSS Snapshots" at
"https://oss.sonatype.org/content/repositories/snapshots",
"Sonatype OSS Releases" at
"https://oss.sonatype.org/content/repositories/releases",
"Typesafe Repository" at
"https://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
"commons-io" % "commons-io" % "2.4"
,"com.typesafe.akka" %% "akka-actor" % "2.8.5"
,"com.typesafe.akka" %% "akka-remote" % "2.8.5"
)