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
package org.jboss.seam.example.openid.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests basic functionality of Seam OpenId example.
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class OpenIdFunctionalTest extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    /**
     * Place holder - just verifies that example deploys
     */
    @Test
    @InSequence(1)
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", getProperty("HOME_PAGE_TITLE"), browser.getTitle());
    }

    /**
     * Method verifies login and logout operations.
     */
    @Test
    @InSequence(2)
    public void openIdLoginLogoutTest() {
        deleteCookies();
        type(getBy("LOGIN_INPUT"), getProperty("OPENID_ACCOUNT"));
        clickAndWaitHttp(getBy("LOGIN_LINK"));
        type(getBy("PASSWORD_INPUT"), getProperty("OPENID_PASSWORD"));
        clickAndWaitHttp(getBy("SIGNIN_BUTTON"));
        if (isElementPresent(getBy("CONTINUE_BUTTON"))) {
            clickAndWaitHttp(getBy("CONTINUE_BUTTON"));
        }

        assertTrue("Page should contain information about successful login", isTextInSource("OpenID login successful..."));
        clickAndWaitHttp(getBy("LOGOUT_BUTTON"));
        assertTrue("Page should contain input field which means that user is not logged in anymore", isElementPresent(getBy("LOGIN_INPUT")));
    }

    private void deleteCookies() {
        browser.manage().deleteCookieNamed("session_id");
        browser.manage().deleteCookieNamed("secure_session_id");
    }
}
