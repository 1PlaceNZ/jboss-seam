package org.jboss.seam.example.nestedbooking.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;
import org.jboss.seam.example.booking.Booking;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {
   public static WebArchive nestedBookingDeployment() {
        
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "seam-nestedbooking.war")
                .addPackage(Booking.class.getPackage())
                
                // from main and test resources
                .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
                .addAsResource("import.sql")
                .addAsResource("seam.properties")
                .addAsWebInfResource("web.xml")
                
                // manually copied from EAR, persistence modified
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml")
                
                // manually copied from WAR
                .addAsWebInfResource("components-test.xml", "components.xml")
                .addAsLibraries(libs);
    }
}
