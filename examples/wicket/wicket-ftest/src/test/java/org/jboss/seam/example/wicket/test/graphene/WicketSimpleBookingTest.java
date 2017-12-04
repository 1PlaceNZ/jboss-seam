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
package org.jboss.seam.example.wicket.test.graphene;

import static org.junit.Assert.fail;

import org.jboss.seam.example.common.test.booking.graphene.SimpleBookingTest;


/**
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
public class WicketSimpleBookingTest extends SimpleBookingTest {

    @Override
    protected void populateBookingFields(int bed, int smoking,
            String creditCard, String creditCardName) {
        super.populateBookingFields(bed, smoking, creditCard, creditCardName);
        selectByIndex(getBy("HOTEL_CREDIT_CARD_EXPIRY_MONTH"), 1);
        selectByIndex(getBy("HOTEL_CREDIT_CARD_EXPIRY_YEAR"), 1);
    }
    
    @Override
    protected int bookHotel(String hotelName, int bed, int smoking,
          String creditCard, String creditCardName) {
      if (!isLoggedIn())
          fail();
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
      String message = getText(getBy("ORDER_CONFIRMATION_NUMBER", hotelName));
      
      int confirmationNumber = Integer.parseInt(message);
      return confirmationNumber;
  }
    
}
