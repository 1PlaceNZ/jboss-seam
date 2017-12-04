/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.example.remoting.helloworld.test.graphene;

import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;

/**
 * This class tests a functionality of remoting/helloworld example.
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class HelloworldFunctionalTest extends SeamGrapheneTest {

    protected static final int TRIES = 5;
    protected static final int INTERVAL = 2000;

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Override
    @Before
    public void beforeTest() throws MalformedURLException {
        Assume.assumeTrue(isRealBrowser());// due to alerts
        open(contextPath.toString() + getProperty("HELLOWORLD_URL"));
    }

    @Test
    @InSequence(1)
    public void simplePageContentTest() {
        assertTrue("Home page of Remoting/Helloworld Example expected",
                browser.getCurrentUrl().contains(getProperty("HELLOWORLD_URL")));
        assertTrue("Different page title expected", browser.getTitle().contains(getProperty("HELLOWORLD_TITLE")));
        assertTrue("Home page should contain Say Hello button", isElementPresent(getBy("SAYHELLO_BUTTON")));
    }

    @Test
    @InSequence(2)
    public void sayHelloButtonTest() {
        click(getBy("SAYHELLO_BUTTON"));
        Alert prompt = browser.switchTo().alert();
        prompt.sendKeys(getProperty("PROMPT_ANSWER"));
        prompt.accept();

        Alert alert = null;
        // the second alert does not appear immediately
        for (int i = 0; i < TRIES; i++) {
            try {
                alert = browser.switchTo().alert();
                break;
            } catch (NoAlertPresentException ex) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException ex1) {
                }
            }
        }
        assertTrue("An alert message should show up and should contain \"Hello,\" and name.",
                alert.getText().contains(getProperty("ALERT_MESSAGE")));
    }
}
