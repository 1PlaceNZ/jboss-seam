This directory contains all jars needed to compile Seam
and run the unit tests.

The only jars required to run Seam in a full Java EE 5 
environment are:

  jbpm-3.1.1.jar
  thirdparty-all.jar

To use Seam with Hibernate3 in a J2EE 1.4 environment, 
the following extra jars are minimally required: 

Hibernate3:
  hibernate-all.jar

Or use your preferred JSF 1.1/1.2 implementation.

To use the JBoss Cache, the following additional jars
are required:

  jgroups.jar
  jboss-aop-jdk50.jar
  jboss-cache-jdk50.jar

To use Seam in with JBoss Embeddable EJB 3.0 or JBoss 
Microcontainer, the following extra jars are required:

  jboss-ejb3-all.jar
  hibernate-all.jar

You will also need a JBoss Microcontainer configuration:

JBoss Microcontainer with JTA/JCA:
  microcontainer/conf
  
JBoss Embeddable EJB 3.0:
  embedded-ejb/conf
