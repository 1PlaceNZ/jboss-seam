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
package org.jboss.seam.example.registration.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests registration form functionality in registration example.
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RegistrationFunctionalTest extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Override
    @Before
    public void beforeTest() {
        open(contextPath + getProperty("REGISTRATION_URL"));
    }

    @Test
    @InSequence(1)
    public void simpleRegistrationTest() {
        String username = "johny";
        String name = "John Doe";
        String password = "secretPassword";

        submitRegistrationForm(username, name, password);
        assertTrue("After-registration page expected.", browser.getCurrentUrl().contains(getProperty("REGISTERED_URL")));
        assertTrue("Welcome message should contain username.", isTextInSource(username));
        assertTrue("Welcome message should contain name.", isTextInSource(name));
    }

    @Test
    @InSequence(2)
    public void duplicateUsernameTest() {
        String username = "janeDoe";
        String name = "Jane Doe";
        String password = "secretPassword";
        submitRegistrationForm(username, name, password);
        browser.navigate().back();
        submitRegistrationForm(username, name, password);
        assertTrue("Registration page expected.", browser.getCurrentUrl().contains(getProperty("REGISTRATION_URL")));
        assertTrue("Error message did not appear.", isTextInSource("User " + username + " already exists"));
    }

    @Test
    @InSequence(3)
    public void emptyValuesTest() {
        submitRegistrationForm("", "", "");
        assertTrue("Registration page expected.", browser.getCurrentUrl().contains(getProperty("REGISTRATION_URL")));
        assertEquals("Unexpected number of error messages.", 3, getXpathCount(getBy("REGISTRATION_MESSAGE_COUNT")));
    }

    protected void submitRegistrationForm(String username, String name, String password) {
        type(getBy("REGISTRATION_USERNAME"), username);
        type(getBy("REGISTRATION_NAME"), name);
        type(getBy("REGISTRATION_PASSWORD"), password);
        clickAndWaitHttp(getBy("REGISTRATION_SUBMIT"));
    }
}
