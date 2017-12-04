package org.jboss.seam.example.itext.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunAsClient
@RunWith(Arquillian.class)
public class ItextFunctionalTest extends SeamGrapheneTest {

    public static final String HOME_PAGE_TITLE = "Seam PDF";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    /**
     * Place holder - just verifies that example deploys
     */
    @Test
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", HOME_PAGE_TITLE, browser.getTitle());
    }
}
