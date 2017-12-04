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
package org.jboss.seam.example.seambay.test.graphene;

import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RegistrationTest extends SeamBayFunctionalTestBase {

    @Override
    @Before
    public void beforeTest() throws MalformedURLException {
        super.beforeTest();
        if (isLoggedIn()) {
            logout();
        }
        clickAndWaitHttp(getBy("REGISTRATION"));
    }

    @Test
    public void testRegistration() {
        submitRegistrationForm("tester", "password", "password", "location");
        assertTrue("Registration failed.", isLoggedIn());
    }

    /**
     * This test verifies that application will not crash after submitting empty
     * registration form
     */
    @Test
    public void testEmptyRegistration() {
        submitRegistrationForm("", "", "", "");
        assertFalse("Registration resulted in debug page.", browser.getCurrentUrl().contains(getProperty("DEBUG_PAGE")));
    }

    @Test
    public void testPasswordConfirmation() {
        submitRegistrationForm("tester1", "password", "differentPassword", "location");
        assertTrue("Registration page expected.",
                browser.getCurrentUrl().contains(getProperty("REGISTRATION_PAGE")));
        assertTrue("Error message not displayed.", isElementPresent(getBy("REGISTRATION_CONFIRM_MESSAGE")));
    }
}
