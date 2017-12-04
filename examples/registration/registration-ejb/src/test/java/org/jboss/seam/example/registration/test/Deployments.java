package org.jboss.seam.example.registration.test;

import java.io.File;

import org.jboss.seam.example.registration.RegisterAction;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments
{
   
   public static WebArchive registrationDeployment() {

      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies()
            // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
            .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();

      return ShrinkWrap.create(WebArchive.class, "seam-registration.war")
              .addPackage(RegisterAction.class.getPackage())
              .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
              .addAsWebInfResource("components-test.xml", "components.xml")
              .addAsResource("persistence.xml", "META-INF/persistence.xml")
              .addAsWebInfResource("jboss-deployment-structure.xml")
              .addAsResource("seam.properties")
              .addAsWebInfResource("web.xml")
              .addAsWebResource("index.html")
              .addAsWebResource("register.xhtml")
              .addAsWebResource("registered.xhtml")
              .addAsLibraries(libs);
      
   }

}
