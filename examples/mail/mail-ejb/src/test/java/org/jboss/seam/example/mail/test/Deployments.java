package org.jboss.seam.example.mail.test;

import java.io.File;

import org.jboss.seam.example.mail.MailExample;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments
{
   
   public static WebArchive mailDeployment() {
   
      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies()
            // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
            .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();

      return ShrinkWrap.create(WebArchive.class, "seam-mail.war")
              .addPackage(MailExample.class.getPackage())
              .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
              .addAsWebInfResource("components-test.xml", "components.xml")
              .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
              .addAsWebInfResource("seam.properties", "classes/seam.properties")
              .addAsWebInfResource("web.xml", "web.xml")
              .addAsWebResource("org/jboss/seam/example/mail/test/errors1.xhtml", "org/jboss/seam/example/mail/test/errors1.xhtml")
              .addAsWebResource("org/jboss/seam/example/mail/test/errors2.xhtml", "org/jboss/seam/example/mail/test/errors2.xhtml")
              .addAsWebResource("org/jboss/seam/example/mail/test/errors3.xhtml", "org/jboss/seam/example/mail/test/errors3.xhtml")
              .addAsWebResource("org/jboss/seam/example/mail/test/errors4.xhtml", "org/jboss/seam/example/mail/test/errors4.xhtml")
              .addAsWebResource("org/jboss/seam/example/mail/test/sanitization.xhtml", "org/jboss/seam/example/mail/test/sanitization.xhtml")
              .addAsWebResource("simple.xhtml")
              .addAsWebResource("plain.xhtml")
              .addAsWebResource("html.xhtml")
              .addAsWebResource("templating.xhtml")
              .addAsWebResource("template.xhtml")
              .addAsLibraries(libs);
   }

}
