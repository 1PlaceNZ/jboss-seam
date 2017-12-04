Seam GroovyBooking Example
=================

This is the Hotel Booking example implemented in Groovy Beans and Hibernate JPA.
The application is deployed as a WAR rather than an EAR.

Running the example
-------------------

To deploy the example to a running JBoss AS instance, follow these steps:

1. In the example root directory run:

        mvn clean install

2. Set JBOSS_HOME environment property.

3. In the groovybooking-web directory run:

        mvn jboss-as:deploy

4. Open this URL in a web browser: http://localhost:8080/groovybooking-web


Testing the example
-------------------

This example is covered by functional tests. All tests use the following technologies:

* __Arquillian__ -  as the framework for EE testing, for managing of container lifecycle and deployment of test archive,
* __ShrinkWrap__ - to create the test archive (WAR).


### Functional tests

Functional tests are located in a separate project and are not executed during the build of the example. They test the built archive in an application server through browser-testing. They use:

* __Arquillian Graphene Extension__ - an advanced Ajax-capable type-safe Selenium-based browser testing tool,
* __Arquillian Drone Extension__ - to automatically run and stop browser instances.

_Note: It is necessary to first build and package the example, because the functional test references the built archive for automatic deployment to the server._

Run the functional test on JBoss AS instance with
    
    mvn -f groovybooking-ftest/pom.xml clean test

The `JBOSS_HOME` environment variable must be set and point to a JBoss AS instance directory.

Several variables can be configured:

* path to an alternative archive for testing

        -DtestDeployment=/path/to/archive.ear

* the browser to use for testing

        -Dbrowser=htmlUnit

* test on a running server

        -Dremote

Testing in JBDS
---------------
### Functional tests

It is not possible to run the functional tests of this example in JBDS, because they use the maven-dependency-plugin to copy test classes from a different maven artifact, which is not a configuration supported by JBDS.
