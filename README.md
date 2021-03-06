# conductr-test
A small study project on [Lightbend Conductr](https://conductr.lightbend.com/) which is a microservice management solution that provides service gateways, service locators, consolidated logging, monitoring, service scaling, configuration, scheduling and integrates great with Scala and Akka applications.

Conductr can be used for free for __development purposes only__ and for that reason comes with a preconfigured sandbox environment that runs in a Docker container, preferably a Docker-Toolbox Docker which runs in a Virtualbox. My experience is that works out of the box; I had some issues with Docker-Platform and Conductr Sandbox (the almost native solution for OSX and Windows).

Conductr doesn't need Docker at all, it _can_, but it isn't a requirement. Running the sandbox is for development and trying out purposes only. When used in production it is __required__ to have a [Lightbend Subscription](https://www.lightbend.com/platform/subscription).

## ConductR v2.x.x
[ConductR v2.0.x](http://conductr.lightbend.com/docs/2.0.x/) is being released.

The difference between ConductR v1 and v2 is that ConductR now consists of `conductr-core` and `conductr-agent`.
Conductr-core and conductr-agent run as separate processes and are separate services

### ConductR Core
ConductR core provides cluster and application information as well as its control interface via the REST API.
ConductR core is responsible for cluster-wide scaling and replication decisions as well as hosting the application
files or the bundles. By default, `conductr-core` exposes its Control REST interface at port `9005`.

By default, the `conductr-core` service runs under the `conductr` user along with the `conductr` group. Its pid file
is written to `/var/run/conductr/running.pid` and its install location is `/usr/share/conductr`.

### ConductR Agent
The ConductR agent is responsible for executing the application process. By default, the `conductr-agent` runs under the
`conductr-agent` user along with the `conductr-agent` group. Its pud file is written to `/var/run/conductr-agent/running.pid`
and its install location is `/usr/share/conductr-agent`.

### Seed node
The first `conductr-core` process that runs on a machine is called the `seed` node, and is the initial contact point
for all other `conductr-core` nodes that need to join the cluster. So all new `conductr-core` processes will need
a `--seed` configuration set. Afterwards new nodes can join

## DC/OS
ConductR can run as a Framework within DC/OS. A Mesos framework receives resource offers and decides whether to accept these
resources and schedule work on them, or to decline the resource offer.

## What is conductr?
1. Clustered Akka application with special features:
- haproxy for location transparency (your apps can resolve other apps through ConductR API)
- CLI and REST API
- Docker support

2. Play application (visual console)

3. Sbt Plugin
- local development and deploy
- build environment deploy

As the end user of ConductR, you are expected to install it in a network, and use the sbt plugin to build your executable bundle.

An __Application__ in ConductR is a collection of one or more __Bundles__. The developer decides what bundles make up an Application, and then aggregates them with a configuration attribute ("system").

Each Bundle can contain one or more __Components__, typically just one. This represents a process in ConductR's lifecycle management terms.

When you package your Application, a uniquely named ZIP file (shazam) will be created for each bundle, containing a manifest.

Here is an example of bundle configuration with one Component as specified in build.sbt:

```scala
enablePlugins(JavaAppPackaging,ConductRPlugin)
BundleKeys.nrOfCpus := 1.0
BundleKeys.memory := 10.MiB
BundleKeys.diskSpace := 5.MB
BundleKeys.endpoints := Map("singlemicro" -> Endpoint("http",0,services = Set(URI("http:/singlemicro"))))
```

This will result in the following __bundle.conf__ manifest that will be included in your .zip artifact:

```bash
version    = "1.0.0"
name       = "singlemicro"
system     = "singlemicro-1.0.0"
nrOfCpus   = 1.0
memory     = 67108864
diskSpace  = 5000000
roles      = ["backend"]
components = {
  "singlemicro-1.0.0" = {
    description      = "singlemicro"
    file-system-type = "universal"
    start-command    = ["singlemicro-1.0.0/bin/singlemicro", "-J-Xms67108864", "-J-Xmx67108864"]
    endpoints        = {
      "singlemicro" = {
        bind-protocol  = "http"
        bind-port = 0
        services  = ["http:/singlemicro"]
      }
    }
  }
}
```

## Conductr v1.x.x
The following is for ConductR v1.x.x

## Installing Conductr
1. You'll have to install [Python 3](https://www.python.org/downloads/mac-osx/), so install it on your system because the Conductr
CLI tools need it. Also you need pip3 (the package manager for Python) to be installed because it will install Conductr.

2. Installing the conductr-cli:

```bash
sudo pip3 install conductr-cli
```

Upgrading the conductr-cli:

```bash
sudo pip3 install conductr-cli --upgrade
```

3. You should get a __free__ [Developer account](https://www.lightbend.com/account/login) at Lightbend.

4. Use the provided scripts to launch the sandbox environment.

## /etc/hosts file
You should add an alias 'boot2docker' to the `/etc/hosts` file. If you don't, the conductr containers will not communicate and the sandbox will not work. Please add the following:

```bash
127.0.0.1	localhost boot2docker
```

## Docker for mac
When running with Docker for mac, you will have a whale icon at the top of your screen indicating that Docker has been installed. It can also be used for configuring the docker instance eg. memory and cpu usage.

The sandbox can be launched in Docker for mac with the following command:

```bash
sandbox run 1.1.13 --feature visualization --nr-of-containers 3
```

The available list of sandbox images can be found [here](https://bintray.com/typesafe/registry-for-subscribers-only/conductr%3Aconductr).

This will start a conductr environment consisting of three nodes. It will also start the visualizer, which that allows you to see a visualization of the current state of the cluster, such as nodes or bundles. The visualizer is available at:

```bash
http://boot2docker:9999
```

## conductr-cli
Conductr provides a REST api to query the information on loaded bundles and running services and to
manage the lifecycle of said bundles (load, run, stop, unload). The [conductr-cli](http://conductr.lightbend.com/docs/2.0.x/CLI) is a handy tool, implemented in Python, to let you operate on Conductr using the comfort of you command-line interface.

| Command          | Description                                            |
| ---------------- | ------------------------------------------------------ |
| conduct version  | print the conductr-cli version that has been installed |
| conduct info     | print cluster information ie. what has been installed on the cluster |
| conduct services | print service information that a bundle exposes        |
| conduct acls tcp / http | print request ACL information for allowing a bundle's service to be accessed or not |
| conduct load path | load a bundle |
| conduct run id | run a bundle |
| conduct stop id | stop a bundle |
| conduct unload id | unload a bundle |
| conduct events id | show events of a bundle |
| conduct logs id | show logs of a bundle |

## sbt-conductr
[sbt-conductr](https://github.com/typesafehub/sbt-conductr) is a sbt plugin that provides commands in sbt to:

- Produce a ConductR bundle
- Start and stop a local ConductR cluster
- Manage a ConductR cluster within a sbt session

| Command     | Description                                                                      |
| ----------- | -------------------------------------------------------------------------------- |
| bundle:dist |	Produce a ConductR bundle for all projects that have the native packager enabled |
| conduct load | Loads a bundle and an optional configuration to the ConductR                    |

# Port Configuration
One of the difficult things in creating clustered services is the port and host configuration. These settings are not static and can change per environment and will change when scaling up or down a clustered application.

ConductR addresses this with its Endpoint configuration declaration:

```scala
BundleKeys.endpoints := Map("singlemicro" -> Endpoint("http", 0, Set(URI("http:/singlemicroservice"))))
``` 

When this bundle is run by ConductR, two system env properties are created called `SINGLEMICRO_BIND_IP` and `SINGLEMICRO_BIND_PORT`.

These are available to your app, both in application.conf:

```bash
singlemicro {
  ip = "127.0.0.1"
  ip = ${?SINGLEMICRO_BIND_IP}
  port = 8096
  port = ${?SINGLEMICRO_BIND_PORT}
}
```

and programatically:

```scala
sys.env.get("SINGLEMICRO_BIND_IP")
```

If you need these env properties passed in to your app's main as args, use the startCommand attribute:

```scala
BundleKeys.startCommand += "-Dhttp.address=$SINGLEMICRO_BIND_IP -Dhttp.port=$SINGLEMICRO_BIND_PORT"
```

## Conductr Ports
Conductr uses the following ports on each node:

- 9004: Akka remoting
- 9005: REST Control API
- 9006: Bundle streaming between ConductR nodes
- 10000 - 10999: default range of ports allocated for bundle endpoints

## Control API
Conductr (core) exposes a control API, which is a REST interface that exposes [the following functionality](http://conductr.lightbend.com/docs/2.0.x/ControlAPI):

__Bundle API:__
- load a bundle
- scale a bundle
- unload a bundle
- query bundle state
- query logs by bundle
- query events by bundle

__Cluster Membership API:__
- query membership state

The Control API is available at port `9005` by default and can be queried eg. the sandbox with:

```bash
$ http :9005/v2/members

HTTP/1.1 200 OK
Content-Length: 109
Content-Type: application/json
Date: Wed, 26 Oct 2016 16:48:53 GMT
Server: akka-http/2.4.10

{
    "members": [],
    "selfNode": {
        "address": "akka.tcp://conductr@172.17.0.2:9004",
        "uid": -918763336
    },
    "unreachable": []
}

$ http :9015/v2/members

HTTP/1.1 200 OK
Content-Length: 108
Content-Type: application/json
Date: Wed, 26 Oct 2016 16:54:21 GMT
Server: akka-http/2.4.10

{
    "members": [],
    "selfNode": {
        "address": "akka.tcp://conductr@172.17.0.3:9004",
        "uid": 587595788
    },
    "unreachable": []
}

$ http :9025/v2/members

HTTP/1.1 200 OK
Content-Length: 109
Content-Type: application/json
Date: Wed, 26 Oct 2016 16:54:03 GMT
Server: akka-http/2.4.10

{
    "members": [],
    "selfNode": {
        "address": "akka.tcp://conductr@172.17.0.4:9004",
        "uid": -463293053
    },
    "unreachable": []
}
```


## Conductr Manual
You should read the [Conductr manual](https://conductr.lightbend.com/docs/1.1.x/Home) to get confortable with the Conductr concepts (the terms and such) so you know what a bundle is, which CLI and SBT tools there are and which commands to use to do a certain thing. The manual is great, so go and read!

## The helloworld-application
The HelloWorld application is a test application that can be build, bundled and deployed in the Conductr sandbox environment. What is great about the environment that it launches a three node (docker) conductr cluster. That cluster manages your applications that use the concept of a __Conductr Bundle__. Bundles can be deployed to Conductr by means of SBT.

The only thing I couldn't figure out is how to script the load process to Conductr. The thing is that sbt builds a bundle and puts it in the target directory (as a zip) with a random ID which is a pain to script. Chances are that I just didn't find the correct method how to get the load/run scriptable. Too bad, because ansible/chef can do that out of the box.

Besides the load/run everything is great!! I could use centralized logging, scale the application, and have a central endpoint to communicate with a __scalable HTTP service__ so my client does not need to know how many nodes there are and where the endpoints are. Conductr does everything for you.

The helloworld application can be used to see how to configure a small working application with which keys and sbt-plugins.

All in all Conductr seems like a viable product to be used in a production environment.

Have fun!



