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
package org.jboss.seam.example.nestedbooking.test.graphene;

import org.jboss.seam.example.common.test.booking.graphene.SimpleBookingTest;

/**
 * This class alters behaviour of SimpleBookingTest to match nestedbooking example
 *
 * @author jharting
 */

public class NestedSimpleBookingTest extends SimpleBookingTest {

    @Override
    protected int bookHotel(String hotelName) {
        return bookHotel(hotelName, 0, CREDIT_CARD, CREDIT_CARD_NAME);
    }

    protected int bookHotel(String hotelName, int room, String creditCard, String creditCardName) {
        if (!isLoggedIn()) {
            return -1;
        }

        if (!isElementPresent(getBy("SEARCH_SUBMIT"))) {
            open(contextPath + getProperty("MAIN_PAGE"));
        }

        enterSearchQuery(hotelName);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));
        
        // hotel page
        clickAndWaitHttp(getBy("BOOKING_BOOK"));

        // book page
        clickAndWaitHttp(getBy("BOOKING_SELECT_ROOM"));
        
        // room select page
        clickAndWaitHttp(getBy("ROOM_LINK", room));
        
        // payment page
        type(getBy("PAYMENT_CREDIT_CARD"), creditCard);
        type(getBy("PAYMENT_CREDIT_CARD_NAME"), creditCardName);
        clickAndWaitHttp(getBy("PAYMENT_PROCEED"));
        
        // confirm page
        clickAndWaitHttp(getBy("CONFIRM_CONFIRM"));
        
        // main page
        String message = getText(getBy("HOTEL_MESSAGE"));
        if (message.matches(getProperty("BOOKING_CONFIRMATION_MESSAGE", EXPECTED_NAME, hotelName))) {
            String[] messageParts = message.split(" ");
            int confirmationNumber = Integer.parseInt(messageParts[messageParts.length - 1]);
            return confirmationNumber;
        } else {
            return -1;
        }
    }
    
    @Override
    protected void populateBookingFields() {}
}
