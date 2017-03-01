# hello-play
A simple study project to integrating conductr with play v2.5

## Installation
You should launch the conductr sandbox which is explained in the root of this project.
Afterwards you can just type `install` in the sbt console of the `hello-play` project.
Sbt will introspect the project and any sub projects, generate `bundles` and their configuration,
restart the sandbox to ensure a clean state and then load and run the application.
You can then access your application at `http://docker-host-ip:9000`, eg. `http://localhost:9000`

`
## Scheduling Parameters
[Scheduling parameters](http://conductr.lightbend.com/docs/1.1.x/CreatingBundles#Producing-a-bundle) are parameters that describe what resources are used by your application or service and are used
to determine which machine they will run on.

The Play and Lagom bundle plugins provide [default scheduling parameters](https://github.com/typesafehub/sbt-conductr/blob/master/README.md#scheduling-parameters), i.e. it is not mandatory to declare scheduling parameters for
these kind of applications. However, it is recommended to define custom settings for each of your application. The defaults are:

```scala
import ByteConversions._

javaOptions in Universal := Seq(
  "-J-Xmx128m",
  "-J-Xms128m"
)

BundleKeys.nrOfCpus := 0.1
BundleKeys.memory := 384.MiB
BundleKeys.diskSpace := 200.MB
```

### com.typesafe.conductr.bundlelib.play.api.StatusService
Conduct's StatusService is required by a bundle component in order to signal when it has started. A successful startup is anything that the application is required to do to become available for processing. For play applications this is done [automatically](http://conductr.lightbend.com/docs/1.1.x/DevQuickStart#Signaling-application-state).

### com.typesafe.conductr.bundlelib.play.api.LocationService 
ConductR's LocationService is able to respond with a URI declaring where a given service (as named by a bundle component's endpoint) resides:

### com.typesafe.conductr.bundlelib.play.api.Env

## Injection
The conductr services above are expected to be injected using Play 2.5's dependency injection mechanism:

```scala
class MyGreatController @Inject() (locationService: LocationService, locationCache: CacheLike) extends Controller {
  ...
  locationService.lookup("known", URI(""), locationCache)
  ...
}
```

The following components are available for injection:

- CacheLike
- ConnectionContext
- LocationService
- StatusService

## Environment Variables
Please read the [environment variables reference](http://conductr.lightbend.com/docs/1.1.x/BundleEnvironmentVariables#Standard-environment-variables) to review which variables are available to a bundle component at runtime.

## Play akka configuration
Play's default configuration will look for the akka configuration in the 'normal' location in the Typesafe configuration
which is the `akka` root node as can be seen in the configuration below:
```
The play akka configuration is defined in the library `com.typesafe.play:play:2.5.x` in reference.conf:

```
play {
  akka {
      # The name of the actor system that Play creates
      actor-system = "application"

      # How long Play should wait for Akka to shutdown before timing it.  If null, waits indefinitely.
      shutdown-timeout = null

      # The location to read Play's Akka configuration from
      config = "akka"

      # The blocking IO dispatcher, used for serving files/resources from the file system or classpath.
      blockingIoDispatcher {
        fork-join-executor {
          parallelism-factor = 3.0
        }
      }

      # The dev mode actor system. Play typically uses the application actor system, however, in dev mode, an actor
      # system is needed that outlives the application actor system, since the HTTP server will need to use this, and it
      # lives through many application (and therefore actor system) restarts.
      dev-mode {
        # Turn off dead letters until Akka HTTP server is stable
        log-dead-letters = off
        # Disable Akka-HTTP's transparent HEAD handling. so that play's HEAD handling can take action
        http.server.transparent-head-requests = false
      }
    }
}
```

# About hello-play
This project has been generated with Activator and contains the following:

- HomeController.scala: Shows how to handle simple HTTP requests.
- AsyncController.scala: Shows how to do asynchronous programming when handling a request.
- CountController.scala: Shows how to inject a component into a controller and use the component when handling requests.
- Module.scala: Shows how to use Guice to bind all the components needed by your application.
- Counter.scala: An example of a component that contains state, in this case a simple counter.
- ApplicationTimer.scala: An example of a component that starts when the application starts and stops when the application stops.
- Filters.scala: Creates the list of HTTP filters used by your application.
- ExampleFilter.scala: A simple filter that adds a header to every response.