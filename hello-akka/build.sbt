import scala.collection.immutable.Seq

name := "hello-akka"

organization := "com.github.dnvriend"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.8"

fork in Test := true

parallelExecution in Test := false

libraryDependencies ++= {
  val akkaVersion = "2.4.17"
  val akkaHttpVersion = "10.0.4"
  // https://github.com/typesafehub/conductr-lib/releases
  val conductRLibVersion = "1.6.0"
  val scalazVersion = "7.2.9"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    // https://github.com/typesafehub/conductr-lib#typesafe-conductr-bundle-library
    "com.typesafe.conductr" %% "akka24-conductr-bundle-lib" % conductRLibVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "org.scalaz" %% "scalaz-core" % scalazVersion
  )
}

libraryDependencies += "io.gatling" % "gatling-test-framework" % "2.2.3" % Test
libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.3" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test

// enable scala code formatting //
import java.util.UUID

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import sbt.complete.DefaultParsers

// Scalariform settings //
SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

// start of setup conductr bundle //
normalizedName in Bundle := "hello-akka" // the human readable name for your bundle

BundleKeys.system := "HelloAkka" // a common name to associate multiple bundles together

// scheduling parameters //
import ByteConversions._

// set what the minimum and maximum heap memory footprint of this individual bundle is
javaOptions in Universal := Seq(
  "-J-Xmx128m",
  "-J-Xms128m"
)

BundleKeys.nrOfCpus := 0.1 // how much total CPU usage is required by this bundle
BundleKeys.memory := 384.MiB // represent all resident memory needed
BundleKeys.diskSpace := 50.MB // how much disk space is needed on the server node to handle the expanded bundle as well as any configuration it contains

BundleKeys.startCommand += "-main com.github.dnvriend.Main" // configure what main to run at startup

//
// service registry
//
// the endpoint key is used to form a set of environment variables for your components.
// e.g. for the endpoint key "helloworld" ConductR creates the environment variable HELLOWORLD_BIND_PORT
// with the configuration below the endpoint will be proxied on the offset /web in Conductr,
// this means that the 'helloworld' service will be available at: http://192.168.99.100/web/helloworld
// instead at http://localhost:8080/helloworld when running eg. in SBT
//
// Note: the service name is: 'web'
//
// Endpoints are declared using an `endpoint` setting using a Map of
// Map[EndpointName -> Endpoint(bindProtocol, bindPort, services*] declaration
//
// the bind port allocated for use by the bundle will be dynamically allocated
// and an environment variable with the convention ENDPOINTNAME_BIND_PORT and ENDPOINTNAME_BIND_IP
//
BundleKeys.endpoints := Map(
  "helloworld" -> Endpoint("http", 0, Set(URI("http://:9000/web"))),
  "akka-remote" -> Endpoint("tcp")
)

// end of setup conductr bundle //

// enable updating file headers //
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

enablePlugins(JavaAppPackaging, AutomateHeaderPlugin)

/////////////////////////////////////////////////////////////////
//
// Enable the Cinnamon Lightbend Monitoring sbt plugin
//
/////////////////////////////////////////////////////////////////
enablePlugins(Cinnamon)
libraryDependencies += Cinnamon.library.cinnamonSandbox

resolvers += "lightbend-contrail" at "https://dl.bintray.com/typesafe/commercial-maven-releases"

// Add the Monitoring Agent for run and test
cinnamon in run := true
cinnamon in test := true

// Use Mapped Diagnostic Context for adding extra identifiers to log messages.
//libraryDependencies += Cinnamon.library.cinnamonSlf4jMdc

// Use Coda Hale Metrics
// http://metrics.dropwizard.io/3.1.0/
//libraryDependencies += Cinnamon.library.cinnamonCHMetrics

// Use Akka instrumentation
//libraryDependencies += Cinnamon.library.cinnamonAkka

val helloWorldUrl = settingKey[String]("HelloWorld Url")
helloWorldUrl := "192.168.10.1:9000/web/helloworld"

val randomId = taskKey[String]("Random UUID")
randomId := UUID.randomUUID.toString

val callHelloWorld = inputKey[String]("Calling helloWorld service")

callHelloWorld := {
  import scala.concurrent._
  import scala.concurrent.blocking
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
  val logger = streams.value.log
  def callUrl(x: Int): Future[String] = Future {
    val cmd = s"http GET ${helloWorldUrl.value}"
    logger.info(s"[$x] Executing '$cmd'")
    val resp = blocking(Process(cmd).lines.mkString)
    logger.info(s"[$x] $resp")
    resp
  }

  val numberOfTimes: Int = (DefaultParsers.Space ~> (DefaultParsers.IntBasic ?? 1)).parsed
  val f = for {
    x <- 1 to numberOfTimes
  } yield callUrl(x)

  Await.result(Future.sequence(f), 60.seconds).mkString
}