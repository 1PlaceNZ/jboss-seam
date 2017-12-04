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
package org.jboss.seam.example.common.test.booking.graphene;

import java.util.Date;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests registration
 *
 * @author jbalunas
 * @author jharting
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RegistrationTest extends BookingFunctionalTestBase {

    private final String LONG_TEXT = "testertestertest";
    private final String SHORT_TEXT = "tes";
    // private final static int USER_COUNT = 3;
    private String suffix;

    public RegistrationTest() {
        Date date = new Date();
        // suffix is needed to allow tests to be run repeatedly
        suffix = Long.toString(date.getTime() % 10000000);
    }

    @Before
    @Override
    public void setUp() {
        // do not login
    }

    @After
    @Override
    public void tearDown() {
        // do not logout
    }

    @Test
    public void testVerify() {
        register("tester", "tester", "password", "password1");
        //assertTrue("Error message expected.", isElementPresent(getBy("REGISTRATION_VERIFY_MESSAGE")));
        assertTrue("Password verification failed.", isTextInSource(getProperty("REGISTRATION_REENTER_MESSAGE")));
    }

    @Test
    public void testLongText() {
        register(LONG_TEXT, "tester", "password", "password");
        assertTrue("Username validation failed.", isTextInSource(getProperty("REGISTRATION_LENGTH_MESSAGE")));
    }

    @Test
    public void testShortText() {
        register(SHORT_TEXT, "tester", "password", "password");
        assertTrue("Username validation failed.", isTextInSource(getProperty("REGISTRATION_LENGTH_MESSAGE")));
    }

    @Test
    public void testDuplicateUser() {
        String username = "tester" + suffix;
        register(username, "tester", "password", "password");
        assertTrue("Navigation after succesful registration failed.", browser.getCurrentUrl().contains(getProperty("HOME_PAGE")));
        //assertTrue("Registration failed.", isTextOnPage(getProperty("REGISTRATION_SUCCESSFUL_MESSAGE", username)));
        register(username, "tester", "password", "password");
        assertTrue("Registered 2 users with the same username.", isTextInSource(
                getProperty("REGISTRATION_USER_EXISTS_MESSAGE", username)));
    }

    @Test
    public void standardRegistrationTest() {
        String username = "john" + suffix;
        String name = "John Doe";
        String password = "password";
        register(username, name, password, password);
        assertTrue("Navigation after succesful registration failed.", browser.getCurrentUrl().contains(getProperty("HOME_PAGE")));
        //assertTrue("Registration failed.", isTextOnPage(getProperty("REGISTRATION_SUCCESSFUL_MESSAGE", username)));
        // try logging in to verify registration
        assertTrue("Login failed.", login(username, password));
        logout();
    }

    private void register(String username, String name, String password, String verify) {
        open(contextPath + getProperty("HOME_PAGE"));
        assertEquals("Unable to load home page.", getProperty("PAGE_TITLE"), browser.getTitle());
        clickAndWaitHttp(getBy("REGISTRATION"));
        type(getBy("REGISTRATION_USERNAME"), username);
        type(getBy("REGISTRATION_NAME"), name);
        type(getBy("REGISTRATION_PASSWORD"), password);
        type(getBy("REGISTRATION_VERIFY"), verify);
        clickAndWaitHttp(getBy("REGISTRATION_SUBMIT"));
    }
}
