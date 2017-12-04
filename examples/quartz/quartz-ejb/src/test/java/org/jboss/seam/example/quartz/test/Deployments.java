package org.jboss.seam.example.quartz.test;

import java.io.File;

import org.jboss.seam.example.quartz.PaymentProcessor;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {
    
    public static WebArchive quartzDeployment() {
       
       File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
             .importCompileAndRuntimeDependencies()
             // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
             .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();
       
       File[] dbunitLibs = Maven.resolver().loadPomFromFile("pom.xml")
             .resolve("org.dbunit:dbunit:jar:2.2")
             .withoutTransitivity().asFile();
       
       JavaArchive ejb =  ShrinkWrap.create(JavaArchive.class,"quartz-ejb.jar");
       ejb.addPackage(PaymentProcessor.class.getPackage());

       return ShrinkWrap.create(WebArchive.class, "seam-quartz.war")
               .addPackage(AsynchronousTest.class.getPackage())
               .addPackage(PaymentProcessor.class.getPackage())
               .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
               .addAsWebInfResource("components-test.xml", "components.xml")
               .addAsResource("persistence.xml", "META-INF/persistence.xml")
               .addAsResource("import.sql")
               .addAsWebInfResource("jboss-deployment-structure.xml")
               .addAsResource("seam.properties")
               .addAsResource("seam.quartz.properties", "seam.quartz.properties")
               .addAsWebInfResource("web.xml")
               .addAsWebInfResource("pages.xml")
               .addAsResource("BaseData.xml")
               .addAsWebResource("index.html")
               .addAsWebResource("search.xhtml")
               .addAsLibraries(libs)
               .addAsLibraries(dbunitLibs);

    }
}
