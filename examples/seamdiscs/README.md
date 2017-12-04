Seam SeamDiscs Example
======================

The seamdiscs example is a simple example built using the Seam Application 
Framework which allows you to record your favourite albums and artists.  It 
uses a mix of RichFaces and Trinidad components. It also uses the "inplace 
editing" pattern; the same facelets are used for editing and display of data 
(login to edit a disc or artist).

The Seam-Trinidad integration (for now built into the example) provides a 
`DataModel` which, when backed by a Seam Application Framework Query, provides 
lazy loading of data for paging, sorting in the Persistence Context and strong 
row keys.  If you want to use, you'll need to copy the classes in 
`org.jboss.seam.trinidad` to your own project.  This may be offered as a 
standalone jar in the Seam project in the future.  One caveat is that you must 
ensure the rows property on the `<tr:table>` is the same as the `maxResults`
property on the Query.

Example

    <tr:table value="#{discs.dataModel}" rows="#{discs.maxResults}">
      <tr:column>
         ...
      </tr:column
    </tr:table>


Running the example
-------------------

To deploy the example to a running JBoss AS instance, follow these steps:

1. In the example root directory run:

        mvn clean install

2. Set JBOSS_HOME environment property.

3. In the seamdiscs-ear directory run:

        mvn jboss-as:deploy

4. Open this URL in a web browser: http://localhost:8080/seam-seamdiscs


Testing the example
-------------------

This example is covered by integration and functional tests. All tests use the following technologies:

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

### Functional tests

Functional tests are located in a separate project and are not executed during the build of the example. They test the built archive in an application server through browser-testing. They use:

* __Arquillian Graphene Extension__ - an advanced Ajax-capable type-safe Selenium-based browser testing tool,
* __Arquillian Drone Extension__ - to automatically run and stop browser instances.

_Note: It is necessary to first build and package the example, because the functional test references the built archive for automatic deployment to the server._

Run the functional test on JBoss AS instance with
    
    mvn -f seamdiscs-ftest/pom.xml clean test

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
### Integration tests

1. Open JBDS and start a configured instance of JBoss AS
2. Import the example project and its submodules
3. In the _Project Explorer_, select the EJB module project, then
    1. Type `Ctrl+Alt+P` (_Select Maven Profiles_) and check `integration-tests` and `arq-jbossas-7-remote`
    2. Right-click the module and select _Run As_ - _JUnit Test_

### Functional tests

1. Open JBDS and start a configured instance of JBoss AS
2. Import the `ftest` project of the example
3. In the _Project Explorer_, select the ftest project, then
    1. Type `Ctrl+Alt+P` (_Select Maven Profiles_) and activate `arq-jbossas-7-remote` profile and deactivate `arq-jbossas-7-managed` profile
    2. Right-click the module and select _Run As_ - _JUnit Test_
