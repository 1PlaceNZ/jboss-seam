package org.jboss.seam.example.jpa.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;
import org.jboss.seam.example.jpa.Booking;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments {

    public static WebArchive jpaDeployment() {

        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve().withTransitivity().asFile();

        // Get File reference to src/main/webapp/WEB-INF, to avoid duplication of resources for the test archive
        File webInf = new File(new File("pom.xml").getParentFile(), "src/main/webapp/WEB-INF");

        return ShrinkWrap.create(WebArchive.class, "jpa-web.war")
                .addPackage(Booking.class.getPackage())
                
                // copy classpath resources to WAR classpath
                .addAsResource("import.sql")
                .addAsResource("seam.properties")
                .addAsResource("META-INF/persistence.xml")
                
                // copy files from src/main/webapp to test-archive's WEB-INF
                .addAsWebInfResource(new File(webInf, "components.xml"))
                .addAsWebInfResource(new File(webInf, "jboss-deployment-structure.xml"))
                .addAsWebInfResource(new File(webInf, "pages.xml"))
                
                // modified web.xml, defines only MockSeamListener
                .addAsWebInfResource("web.xml")
                .addAsLibraries(libs);
    }
}
