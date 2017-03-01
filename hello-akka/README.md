# helloworld
A small akka-based application with the name 'conductr-test' that shows how to setup and deploy such an application using conductr.

## Creating a bundle
When conductr sandbox is running, a bundle can be created with the command:

```bash
sbt "bundle:dist"
```

## Commercial features
To use the following, you'll have to create a
(free) [Lightbend Developer Account](https://www.lightbend.com/account).
You then have to create your '.credentials' file, the necessary credentials be
requested at the [Lightbend Credentials Page](https://www.lightbend.com/product/lightbend-reactive-platform/credentials)
and with that file you'll have so setup your [Lightbend Reactive Platform](https://developer.lightbend.com/docs/reactive-platform/2.0/setup/setup-sbt.html).


Create a file in `~/.lightbend/commercial.credentials`:

```
realm = Bintray
host = dl.bintray.com
user = <your very long userid@lightbend here>
password = <your very long password here>
```

## Available features:

- proxying
- visualization
- logging
- lite-logging
- monitoring

## Configure address aliases
In order to run a ConductR cluster locally, we use network address aliases. These address aliases will allow ConductR
to bind to the required ports to run locally without port collisions. Since we will be starting 3 node cluster,
3 address aliases are required for each node respectively.

The address aliases are temporary. If you reboot, you'll need to run the above commands before running the sandbox again.

For macOS, execute the following commands to create the address aliases:

```bash
sudo sh -c "ifconfig lo0 alias 192.168.10.1 255.255.255.0 && \
ifconfig lo0 alias 192.168.10.2 255.255.255.0 && \
ifconfig lo0 alias 192.168.10.3 255.255.255.0"
```

## Enable monitoring
Run sandbox with monitoring:

```bash
sandbox run 2.0.2 -n 3 --feature visualization --feature monitoring
```

After a while (a minute or so) you should see the following:

```bash
$ conduct info
ID               NAME                     #REP  #STR  #RUN
73595ec          visualizer                  1     0     1
bdfa43d-e5f3504  conductr-haproxy            1     0     1
06d370b          conductr-kibana             1     0     1
d4bdc6c          cinnamon-grafana-docker     1     0     1
85dd265          conductr-elasticsearch      1     0     1
```

## Explore
As described in the [cinnamon user manual](https://developer.lightbend.com/docs/monitoring/latest/sandbox/explore.html):

- [Proxy port 9000](http://192.168.10.1:9000)
- [ConductR visualizer on port 9999](http://192.168.10.1:9999)
- [Kibana on port 5601](http://192.168.10.1:5601)
- [Grafana on port 3000](http://192.168.10.1:3000)
