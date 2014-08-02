jicunit
=======

JICUnit is a Java testing framework for JEE in-container testing. JICUnit is inspired by JUnitEE and Jakarta Cactus, both which are not developed any more.

In short the JUnit tests are package in a test WAR and deployed to the JEE container. 

JICUnit has a JUnit test runner in the container just like JUnitEE and Cactus. It also has a Maven plugin that generates additional files which must be included in the test WAR. And finally it has an Eclipse plugin which makes it possible to run the tests from Eclipse just like a unit test, but the tests are executed in the container. 

To run in-container tests from a phase in Maven just use Surefire or Failsafe plugins with an extra system property (a URL) that points to the server where the test WAR is deployed. 

The Eclipse plugin is really only a convenient way to set the system property.


The JICUnit framework also has a web GUI front end for the runner. It is implemented with JSF and MyFaces/Trinidad and depends on CDI and Servlet 3.0 for activation. The JICUnit framework is actually a web fragment. 

For more information, please visit:

* [Wiki](https://github.com/Lucas3oo/jicunit/wiki)
* [Download and Install guide](https://github.com/Lucas3oo/jicunit/wiki/Download-and-Install)
* [Getting Started](https://github.com/Lucas3oo/jicunit/wiki/Getting-started)
