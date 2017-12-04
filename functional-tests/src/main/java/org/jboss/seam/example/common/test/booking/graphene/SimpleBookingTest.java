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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.JavascriptExecutor;

/**
 * This class tests booking functionality of the example.
 *
 * @author jbalunas
 * @author jharting
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SimpleBookingTest extends BookingFunctionalTestBase {

    protected final String EXPECTED_NAME = "Demo User";
    protected final String CREDIT_CARD = "0123456789012345";
    protected final String CREDIT_CARD_NAME = "visa";

    /**
     * Tries searching for non existing hotel.
     */
    @Test
    public void invalidSearchStringTest() {
        enterSearchQuery("NonExistingHotel");
        assertTrue("Search failed.", isElementPresent(getBy("NO_HOTELS_FOUND")));
    }

    /**
     * Simply books hotel.
     */
    @Test
    public void simpleBookingTest() {
        String hotelName = "W Hotel";
        int confirmationNumber;
        confirmationNumber = bookHotel(hotelName);
        assertTrue("Booking with confirmation number " + confirmationNumber + " not found.",
                isElementPresent(getBy("BOOKING_TABLE_ITEM", confirmationNumber, hotelName)));
    }

    /**
     * Tries booking hotel with incorrect dates.
     */
    @Test
    public void invalidDatesTest() {
        String hotelName = "W Hotel";
        enterSearchQuery(hotelName);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));

        // hotel page
        clickAndWaitHttp(getBy("BOOKING_BOOK"));

        // booking page
        String checkOut = browser.findElement(getBy("HOTEL_CHECKOUT_DATE_FIELD")).getAttribute("value");
        populateBookingFields();

        // switch check in and check out date
        setTextInputValue(getBy("HOTEL_CHECKIN_DATE_FIELD"), checkOut);
        clickAndWaitHttp(getBy("HOTEL_PROCEED"));
        assertTrue("Date verification #1 failed.", isTextInSource(getProperty("BOOKING_INVALID_DATE_MESSAGE1")));
        assertTrue("Check-out date error message expected.", isElementPresent(getBy("HOTEL_CHECKOUT_DATE_MESSAGE")));
        // set check in to past
        setTextInputValue(getBy("HOTEL_CHECKIN_DATE_FIELD"), "01/01/1970");
        clickAndWaitHttp(getBy("HOTEL_PROCEED"));
        assertTrue("Date verification #2 failed.", isTextInSource(getProperty("BOOKING_INVALID_DATE_MESSAGE2")));
        assertTrue("Checkin-date error message expected.", isElementPresent(getBy("HOTEL_CHECKIN_DATE_MESSAGE")));
    }

    /**
     * This test verifies that user gets right confirmation number when
     * canceling order. https://jira.jboss.org/jira/browse/JBSEAM-3288
     */
    @Test
    public void testJBSEAM3288() {
        String[] hotelNames = new String[]{"Doubletree", "Hotel Rouge", "Conrad Miami"};
        int[] confirmationNumbers = new int[3];
        // make 3 bookings
        for (int i = 0; i < 3; i++) {
            int confirmationNumber = bookHotel(hotelNames[i]);
            confirmationNumbers[i] = confirmationNumber;
        }
        // assert that there bookings are listed in hotel booking list
        for (int i = 0; i < 3; i++) {
            assertTrue("Expected booking #" + i + " not present", isElementPresent(
                    getBy("BOOKING_TABLE_ITEM", confirmationNumbers[i], hotelNames[i])));
        }
        // cancel all the reservations
        for (int i = 2; i >= 0; i--) {
            clickAndWaitHttp(getBy("BOOKING_TABLE_ITEM_LINK", confirmationNumbers[i], hotelNames[i]));
            assertTrue("Booking canceling failed", isTextInSource(
                    getProperty("BOOKING_CANCELLED_MESSAGE", confirmationNumbers[i])));
        }

    }

    protected int bookHotel(String hotelName, int bed, int smoking, String creditCard, String creditCardName) {
        if (!isLoggedIn()) {
            fail();
        }
        if (!isElementPresent(getBy("SEARCH_SUBMIT"))) {
            open(contextPath + getProperty("MAIN_PAGE"));
        }
        enterSearchQuery(hotelName);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));
        
        // booking page
        clickAndWaitHttp(getBy("BOOKING_BOOK"));
        
        // hotel page
        populateBookingFields(bed, smoking, creditCard, creditCardName);
        clickAndWaitHttp(getBy("HOTEL_PROCEED"));
        
        // confirm page
        clickAndWaitHttp(getBy("HOTEL_CONFIRM"));
        
        // main page
        String message = getText(getBy("HOTEL_MESSAGE"));
        assertTrue("Booking failed. Confirmation message does not match.", message.matches(
                getProperty("BOOKING_CONFIRMATION_MESSAGE", EXPECTED_NAME, hotelName)));
        String[] messageParts = message.split(" ");
        int confirmationNumber = Integer.parseInt(messageParts[messageParts.length - 1]);
        return confirmationNumber;
    }

    protected int bookHotel(String hotelName) {
        return bookHotel(hotelName, 2, 0, CREDIT_CARD, CREDIT_CARD_NAME);
    }

    protected void populateBookingFields(int bed, int smoking, String creditCard, String creditCardName) {
        selectByValue(getBy("HOTEL_BED_FIELD"), bed);
        if (smoking == 1) {
            click(getBy("HOTEL_SMOKING_1"));
        } else {
            click(getBy("HOTEL_SMOKING_2"));
        }
        type(getBy("HOTEL_CREDIT_CARD"), creditCard);
        type(getBy("HOTEL_CREDIT_CARD_NAME"), creditCardName);
    }

    protected void populateBookingFields() {
        populateBookingFields(2, 0, CREDIT_CARD, CREDIT_CARD_NAME);
    }
}
