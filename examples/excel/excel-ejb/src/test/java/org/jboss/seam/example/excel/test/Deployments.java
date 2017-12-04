package org.jboss.seam.example.excel.test;

import java.io.File;

import org.jboss.seam.example.excel.ExcelTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

public class Deployments
{
   public static WebArchive excelDeployment() {
      
      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies()
            // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
            .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();

      return ShrinkWrap.create(WebArchive.class, "seam-excel.war")
              .addPackage(ExcelTest.class.getPackage())
              .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
              .addAsWebInfResource("persistence.xml", "classes/META-INF/persistence.xml")
              .addAsWebInfResource(new StringAsset("org.jboss.seam.mock.MockFacesContextFactory"), "classes/META-INF/services/javax.faces.context.FacesContextFactory")
              .addAsWebInfResource("components-test.xml", "components.xml")
              .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
              .addAsWebInfResource("seam.properties", "classes/seam.properties")
              .addAsWebInfResource("web.xml", "web.xml")
              .addAsWebResource("simple.xhtml")
              .addAsLibraries(libs);
   }

}
