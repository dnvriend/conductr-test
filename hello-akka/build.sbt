name := "hello-akka"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVersion = "2.4.11"
  // https://github.com/typesafehub/conductr-lib/releases
  val conductRLibVersion = "1.4.12"
  val scalazVersion = "7.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
    // https://github.com/typesafehub/conductr-lib#typesafe-conductr-bundle-library
    "com.typesafe.conductr" %% "akka24-conductr-bundle-lib" % conductRLibVersion,
    "ch.qos.logback" % "logback-classic" % "1.1.7",
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.typelevel" %% "scalaz-scalatest" % "1.0.0" % Test,
    "org.scalatest" %% "scalatest" % "2.2.6" % Test
  )
}

// enable scala code formatting //
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

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
BundleKeys.memory := 256.MiB // represent all resident memory needed
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
