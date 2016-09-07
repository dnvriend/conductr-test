# conductr-test
A small study project on [Lightbend Conductr](https://conductr.lightbend.com/) which is a microservice management solution
that provides service gateways, service locators, consolidated logging, monitoring, service scaling, configuration, scheduling
and integrates great with Scala and Akka applications.

Conductr can be used for free for __development purposes only__ and for that reason comes with a preconfigured sandbox environment
that runs in a Docker container, preferably a Docker-Toolbox Docker which runs in a Virtualbox. My experience is that works
out of the box; I had some issues with Docker-Platform and Conductr Sandbox (the almost native solution for OSX and Windows).

Conductr doesn't need Docker at all, it _can_, but it isn't a requirement. Running the sandbox is for development and trying
out purposes only. When used in production it is __required__ to have a [Lightbend Subscription](https://www.lightbend.com/platform/subscription).

# Installation
1. You'll have to install [Python 3](https://www.python.org/downloads/mac-osx/), so install it on your system because the Conductr
CLI tools need it. Also you need pip3 (the package manager for Python) to be installed because it will install Conductr.

2. Install Conductr using pip3:

```
sudo pip3 install conductr-cli
```

3. You should get a __free__ [Developer account](https://www.lightbend.com/account/login) at Lightbend.

4. Use the provided scripts to launch the sandbox environment.

# Conductr Manual
You should read the [Conductr manual](https://conductr.lightbend.com/docs/1.1.x/Home) to get confortable with the
Conductr concepts (the terms and such) so you know what a bundle is, which CLI and SBT tools there are and which commands
to use to do a certain thing. The manual is great, so go and read!

# The helloworld-application
The HelloWorld application is a test application that can be build, bundled and deployed in the Conductr sandbox environment.
What is great about the environment that it launches a three node (docker) conductr cluster. That cluster manages your applications
that use the concept of a __Conductr Bundle__. Bundles can be deployed to Conductr my means of SBT.

The only thing I couldn't figure out is how to script the load process to Conductr. The thing is that sbt builds a bundle and puts it
in the target directory (as a zip) with a random ID which is a pain to script. Chances are that I just didn't find the correct method how
to get the load/run scriptable. Too bad, because ansible/chef can do that out of the box.

Besides the load/run everything is great!! I could use centralized logging, scale the application, and have a central endpoint to
communicate with a __scalable HTTP service__ so my client does not need to know how many nodes there are and where the endpoints are.
Conductr does everything for you.

The helloworld application can be used to see how to configure a small working application with which keys and sbt-plugins.

All in all Conductr seems like a viable product to be used in a production environment.

Have fun!



