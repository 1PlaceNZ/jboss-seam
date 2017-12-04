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
package org.jboss.seam.example.dvd.test.graphene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.jboss.arquillian.graphene.Graphene.waitGui;

import java.text.MessageFormat;
import java.util.Date;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;


/**
 * This class tests the registration functionality of dvdstore example
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RegistrationTest extends GrapheneDvdTest {

    // suffix is needed to allow test to be run repeatedly
    private String suffix = Long.toString(new Date().getTime() % 10000000);

    @Override
    @Before
    public void setUp() {
        open(contextPath + getProperty("HOME_PAGE"));
        waitGui(browser).until().element(getBy("REGISTRATION")).is().present();
        clickAndWaitHttp(getBy("REGISTRATION"));
        assertTrue("Unable to load registration page.", browser.getCurrentUrl()
                .contains(getProperty("REGISTRATION_FIRST_PAGE_URL")));
    }

    /**
     * Tries to register user
     */
    @Test
    public void basicRegistrationTest() {
        Person person = new Person("Street 123", "012-3456-7890",
                "Visa", "City", "john@example.com", "John", "Doe",
                "password", "0123456789", "US", "john" + suffix, "password",
                "01234");

        fillFirstPage(person);
        assertTrue("Unable to load account page.", browser.getCurrentUrl()
                .contains(getProperty("REGISTRATION_SECOND_PAGE_URL")));
        fillSecondPage(person);
        assertTrue("Unable to load card page.", browser.getCurrentUrl().contains(
                getProperty("REGISTRATION_THIRD_PAGE_URL")));
        fillThirdPage(person);
        assertTrue("Unable to load confirmation page.", browser.getCurrentUrl()
                .contains(getProperty("REGISTRATION_CONFIRMATION_PAGE_URL")));
        assertTrue("Registration failed.", isTextOnPage(MessageFormat
                .format(getProperty("REGISTRATION_CONFIRMATION_MESSAGE"),
                        person.getUsername())));
        assertTrue("User should be logged in after succesful registration.",
                isLoggedIn());
    }

    /**
     * Tests whether validation of input fields works fine.
     */
    @Test
    public void firstPageInvalidValuesTest() {
        Person person = new Person("t", "t", "t");
        fillFirstPage(person);
        assertTrue("Navigation failed.", browser.getCurrentUrl().contains(
                getProperty("REGISTRATION_FIRST_PAGE_URL")));
        assertEquals("Error messages expected.", 2, getXpathCount(getBy("REGISTRATION_LENGTH_MESSAGE")));
    }

    /**
     * Tests password verification.
     */
    @Test
    public void verifyPasswordTest() {
        Person person = new Person("tester", "password", "password1");
        fillFirstPage(person);
        assertTrue("Navigation failed.", browser.getCurrentUrl().contains(
                getProperty("REGISTRATION_FIRST_PAGE_URL")));
        assertTrue("Password verify message expected.", isElementPresent(getBy("REGISTRATION_VERIFY_MESSAGE")));
    }

    /**
     * Tries to register user that already exists. Test assumes that user1 is
     * already registered.
     */
    @Test
    public void duplicateUserTest() {
        Person person = new Person("user1", "password", "password");
        fillFirstPage(person);
        assertTrue("Navigation failed.", browser.getCurrentUrl().contains(
                getProperty("REGISTRATION_FIRST_PAGE_URL")));
        assertTrue(
                "Duplicate user error message expected.",
                isElementPresent(getBy("REGISTRATION_DUPLICATE_USER_MESSAGE")));
    }

    private void fillFirstPage(Person person) {
        type(getBy("REGISTRATION_USERNAME"), person.getUsername(), true);
        type(getBy("REGISTRATION_PASSWORD"), person.getPassword(), true);
        type(getBy("REGISTRATION_VERIFY"), person.getVerify(), true);
        
        clickAndWaitHttp(getBy("REGISTRATION_FIRST_SUBMIT"));
    }

    private void fillSecondPage(Person person) {
        type(getBy("REGISTRATION_FIRST_NAME"), person.getUsername(), true);
        type(getBy("REGISTRATION_LAST_NAME"), person.getLastName(), true);
        type(getBy("REGISTRATION_ADDRESS"), person.getAddress(), true);
        type(getBy("REGISTRATION_ADDRESS2"), person.getAddress2(), true);
        type(getBy("REGISTRATION_CITY"), person.getCity(), true);
        type(getBy("REGISTRATION_STATE"), person.getState(), true);
        type(getBy("REGISTRATION_ZIP"), person.getZip(), true);
        type(getBy("REGISTRATION_EMAIL"), person.getEmail(), true);
        type(getBy("REGISTRATION_PHONE"), person.getPhone(), true);
        
        clickAndWaitHttp(getBy("REGISTRATION_SECOND_SUBMIT"));
    }

    private void fillThirdPage(Person person) {
        selectByText(getBy("REGISTRATION_CARD_TYPE_SELECT"), person.getCardType());
        type(getBy("REGISTRATION_CARD_NUMBER"), person.getCardNumber(), true);
        fillThirdPage();
    }

    private void fillThirdPage() {
        clickAndWaitHttp(getBy("REGISTRATION_THIRD_SUBMIT"));
    }
}
