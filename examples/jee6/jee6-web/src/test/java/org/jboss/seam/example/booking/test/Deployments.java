package org.jboss.seam.example.booking.test;

import java.io.File;
import org.jboss.seam.example.booking.Booking;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {

    public static WebArchive bookingDeployment() {
        
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        // Get File reference to src/main/webapp/WEB-INF, to avoid duplication of resources for the test archive
        File webInf = new File(new File("pom.xml").getParentFile(), "src/main/webapp/WEB-INF");

        return ShrinkWrap.create(WebArchive.class, "jee6-web.war")
                .addPackage(Booking.class.getPackage())
                
                // copy classpath resources to WAR classpath
                .addAsWebInfResource("import.sql", "classes/import.sql")
                .addAsWebInfResource("seam.properties", "classes/seam.properties")
                .addAsWebInfResource("persistence.xml", "classes/META-INF/persistence.xml")
                .addAsWebInfResource("components-test.xml", "components.xml")
                
                // copy files from src/main/webapp to test-archive's WEB-INF
                .addAsWebInfResource(new File(webInf, "jboss-deployment-structure.xml"))
                .addAsWebInfResource(new File(webInf, "pages.xml"))
                .addAsWebInfResource(new File(webInf, "ejb-jar.xml"))
                .addAsWebInfResource(new File(webInf, "web.xml"))
                
                .addAsLibraries(libs);
    }
}
