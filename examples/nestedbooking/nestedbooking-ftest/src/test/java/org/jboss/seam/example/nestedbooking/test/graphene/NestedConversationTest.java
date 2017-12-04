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

import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.booking.graphene.BookingFunctionalTestBase;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests only nested conversations
 *
 * @author mgencur
 */

@RunAsClient
@RunWith(Arquillian.class)
public class NestedConversationTest extends BookingFunctionalTestBase {
    
    protected final String CREDIT_CARD = "0123456789012345";
    protected final String CREDIT_CARD_NAME = "visa";

    @Before
    @Override
    public void beforeTest() throws MalformedURLException {
        Assume.assumeTrue(isRealBrowser());// need window handling
        super.beforeTest();
    }
    
    @Test
    public void nestedConversationTest() {

        openWindow(contextPath + getProperty("MAIN_PAGE"), "1");
        
        selectWindow("1");
        if (!isLoggedIn()) {
            login();
        }
        enterSearchQuery("W Hotel");
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_SECOND_ROW_LINK"));

        //open the url in a second window
        String url = browser.getCurrentUrl();
        openWindow(url, "2");

        //go next to confirm button in browser 1
        selectWindow("1");
        clickAndWaitHttp(getBy("BOOKING_BOOK"));
        clickAndWaitHttp(getBy("SELECT_ROOM_BUTTON"));
        clickAndWaitHttp(getBy("SELECT_WONDERFUL_ROOM"));
        type(getBy("PAYMENT_CREDIT_CARD"), CREDIT_CARD);
        type(getBy("PAYMENT_CREDIT_CARD_NAME"), CREDIT_CARD_NAME);
        clickAndWaitHttp(getBy("PAYMENT_PROCEED"));

        //go next to confirm button in browser 2
        selectWindow("2");
        clickAndWaitHttp(getBy("BOOKING_BOOK"));
        clickAndWaitHttp(getBy("SELECT_ROOM_BUTTON"));
        clickAndWaitHttp(getBy("SELECT_FANTASTIC_ROOM"));
        type(getBy("PAYMENT_CREDIT_CARD"), CREDIT_CARD);
        type(getBy("PAYMENT_CREDIT_CARD_NAME"), CREDIT_CARD_NAME);
        clickAndWaitHttp(getBy("PAYMENT_PROCEED"));

        //confirm in browser 1 (WONDERFUL room should be selected)
        selectWindow("1");
        clickAndWaitHttp(getBy("CONFIRM_CONFIRM"));
        assertTrue(isTextOnPage("$450.00") && isTextOnPage("Wonderful Room"));
        assertFalse(isTextOnPage("$1,000.00") || isTextOnPage("Fantastic Suite"));
        logout();
    }

}
