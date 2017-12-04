package org.jboss.seam.example.booking.test;

import org.jboss.seam.example.booking.Booking;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class Deployments {
    public static WebArchive iceFacesDeployment() {
        
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "seam-icefaces.war")
                .addPackage(Booking.class.getPackage())
                .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
                .addAsResource("import.sql")
                .addAsResource("persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("components-test.xml","components.xml")
                .addAsWebInfResource("pages.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml")
                .addAsResource("seam.properties")
                .addAsWebInfResource("web.xml")
                .addAsLibraries(libs);
    }
}
