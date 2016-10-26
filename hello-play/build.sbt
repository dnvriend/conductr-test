name := "hello-play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

// Declares endpoints. The default is Map("web" -> Endpoint("http", 0, Set.empty)).
// The endpoint key is used to form a set of environment variables for your components,
// e.g. for the endpoint key "web" ConductR creates the environment variable WEB_BIND_PORT.
BundleKeys.endpoints := Map(
  "play" -> Endpoint(bindProtocol = "http", bindPort = 0, services = Set(URI("http://:9000/play"))),
  "akka-remote" -> Endpoint("tcp")
)

normalizedName in Bundle := "hello-play" // the human readable name for your bundle

BundleKeys.system := "HelloPlaySystem" // represents the clustered ActorSystem

BundleKeys.startCommand += "-Dhttp.address=$PLAY_BIND_IP -Dhttp.port=$PLAY_BIND_PORT"