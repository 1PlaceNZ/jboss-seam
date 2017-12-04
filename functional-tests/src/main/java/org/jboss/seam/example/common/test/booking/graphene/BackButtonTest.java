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

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This tests verifies that the example can cleanly handle backbuttoning in
 * various situations
 *
 * @author jbalunas
 * @author jharting
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BackButtonTest extends BookingFunctionalTestBase {

    String hotelName = "Mar";

    @Before
    public void assumeNotHTMLUnit() {
        Assume.assumeTrue(isRealBrowser());// back button behaviour in htmlunit is not desirable
    }

    /**
     * Tries whether backbuttoning after logout is cleanly handled.
     */
    @Test
    public void backbuttoningAfterLogoutTest() {
        // don't search at this point, avoid POST request to enable back buttoning in Firefox
        logout();
        browser.navigate().back();
        clickAndWaitHttp(getBy("SETTINGS"));
        // should redirect to home when handling ViewExpiredException and due to not being logged in
        assertTrue(browser.getCurrentUrl().contains(getProperty("HOME_PAGE")));
        assertEquals("Backbuttoning failed.", getProperty("PAGE_TITLE"),
                browser.getTitle());
        assertTrue("Login message not present", isTextOnPage(getProperty("NOT_LOGGED_IN_MESSAGE")));
        assertFalse("User is logged in after logout and backbuttoning", isLoggedIn());
    }

    /**
     * Tries whether backbuttoning after logout is cleanly handled. Using ajax
     * functionality after logout.
     */
    @Test
    public void backbuttoningAfterLogoutWithAjaxTest() {
        // don't search at this point, avoid POST request to enable back buttoning in Firefox
        logout();
        browser.navigate().back();
        click(getBy("SEARCH_SUBMIT"));
        sleep(5000);
        // should redirect to home when handling ViewExpiredException and due to not being logged in
        assertTrue(browser.getCurrentUrl().contains(getProperty("HOME_PAGE")));
        assertEquals("Backbuttoning failed.", getProperty("PAGE_TITLE"),
                browser.getTitle());
        assertTrue("Login message not present", isTextOnPage(getProperty("NOT_LOGGED_IN_MESSAGE")) || isTextOnPage("Session expired, please log in again"));
        assertFalse("User is logged in after logout and backbuttoning", isLoggedIn());
    }

    /**
     * Verifies that backbuttoning after ending conversation is handled cleanly.
     */
    @Test
    public void backbuttoningAfterConversationEndTest() {
        // start booking
        enterSearchQuery(hotelName);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));
        clickAndWaitHttp(getBy("BOOKING_BOOK"));

        // cancel booking
        clickAndWaitHttp(getBy("HOTEL_CANCEL"));

        browser.navigate().back();
        // conditional refresh for firefox, which doesn't reload page after backbuttoning, while HTMLUnit does
        if (!isTextInSource(getProperty("CONVERSATION_TIMEOUT_MESSAGE"))) {
            browser.navigate().refresh();
        }
        assertTrue("Conversation failure.", isTextInSource(getProperty("CONVERSATION_TIMEOUT_MESSAGE")));
    }
}
