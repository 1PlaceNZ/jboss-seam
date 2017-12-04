Seam RestBay Example
====================

This example shows Seam/JAX-RS RESTful HTTP webservices integration.
It runs an EAR.

Running the example
-------------------

To deploy the example to a running JBoss AS instance, follow these steps:

1. In the example root directory run:

        mvn clean install

2. Set JBOSS_HOME environment property.

3. In the restbay-ear directory run:

        mvn jboss-as:deploy

4. Open this URL in a web browser: http://localhost:8080/seam-restbay


Testing the example
-------------------

This example is covered by integration tests. All tests use the following technologies:

* __Arquillian__ -  as the framework for EE testing, for managing of container lifecycle and deployment of test archive,
* __ShrinkWrap__ - to create the test archive (WAR).


### Integration tests

Integration tests cover core application logic and reside in the EJB module. In addition to Arquillian and ShrinkWrap, the integration tests also use:

* __JUnitSeamTest__ - to hook into the JSF lifecycle and assert server-side state,
* __ShrinkWrap Resolver__ - to resolve dependencies of the project for packaging in the test archive.

The tests are executed in Maven's test phase. By default they are skipped and can be executed on JBoss AS with:

    mvn clean test -Darquillian=jbossas-managed-7

The `JBOSS_HOME` environment variable must be set and point to a JBoss AS instance directory.

To test on a running server, use

    mvn clean test -Darquillian=jbossas-remote-7


Testing in JBDS
---------------
### Integration tests

1. Open JBDS and start a configured instance of JBoss AS
2. Import the example project and its submodules
3. In the _Project Explorer_, select the EJB module project, then
    1. Type `Ctrl+Alt+P` (_Select Maven Profiles_) and check `integration-tests` and `arq-jbossas-7-remote`
    2. Right-click the module and select _Run As_ - _JUnit Test_

