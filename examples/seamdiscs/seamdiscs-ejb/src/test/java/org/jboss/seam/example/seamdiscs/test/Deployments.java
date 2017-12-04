package org.jboss.seam.example.seamdiscs.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class Deployments {
    public static WebArchive seamdiscsDeployment() {
        File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

        File[] dbunitLibs = Maven.resolver().loadPomFromFile("pom.xml")
                .resolve("org.dbunit:dbunit:jar:2.2")
                .withoutTransitivity().asFile();

        return ShrinkWrap.create(WebArchive.class, "seam-seamdiscs.war")
                .addPackages(true, "org.jboss.seam.example.seamdiscs")
                .addPackages(true, "org.jboss.seam.trinidad")
                .addClasses(TestStrings.class)

                // already in EJB module
                .addAsResource("import.sql")
                .addAsResource("seam.properties")
                .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")

                // test specific persistence.xml
                .addAsResource("persistence.xml", "META-INF/persistence.xml")

                // manually copied from Web module
                .addAsWebInfResource("pages.xml")
                .addAsWebResource("login.page.xml")
                .addAsWebResource("artist.page.xml")
                .addAsWebResource("artists.page.xml")
                .addAsWebResource("disc.page.xml")
                .addAsWebResource("discs.page.xml")

                // manually copied from Web module, modified
                .addAsWebInfResource("web.xml") // only contains MockSeamListener definition
                .addAsWebInfResource("components-test.xml", "components.xml") // corrected ejb component jndi-name references from java:app/jboss-seam to java:app/seam-restbay

                // manually copied from EAR module
                .addAsWebInfResource("jboss-deployment-structure.xml")

                // 
                .addAsResource("org/jboss/seam/example/seamdiscs/test/BaseData.xml")

                .addAsLibraries(libs)
                .addAsLibraries(dbunitLibs);

    }
}
