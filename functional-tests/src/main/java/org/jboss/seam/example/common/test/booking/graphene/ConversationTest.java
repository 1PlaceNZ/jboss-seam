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

import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ConversationTest extends BookingFunctionalTestBase {

    private final String hotel1 = "Hotel Rouge";
    private final String hotel2 = "Doubletree";

    /**
     * Opens two windows and tries switching over workspaces to make sure
     * conversations work properly.
     */    
    
    @Before
    @Override
    public void beforeTest() throws MalformedURLException {
        Assume.assumeTrue(isRealBrowser());// need window handling
        super.beforeTest();
    }
    
    @Test
    public void testConversations() {
        
        // Open two windows
        openWindow(contextPath + getProperty("MAIN_PAGE"), "1");
        openWindow(contextPath + getProperty("MAIN_PAGE"), "2");
        
        // Start booking in window 1
        selectWindow("1");
        open(contextPath + getProperty("MAIN_PAGE"));
        
        if (!isLoggedIn()) {
            login();
        }
        enterSearchQuery(hotel1);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));
        clickAndWaitHttp(getBy("BOOKING_BOOK"));
        
        // Find hotel in window 2
        selectWindow("2");
        open(contextPath + getProperty("MAIN_PAGE"));
        if (!isLoggedIn()) {
            login();
        }
        enterSearchQuery(hotel2);
        clickAndWaitHttp(getBy("SEARCH_RESULT_TABLE_FIRST_ROW_LINK"));
        
        // Reload window 1 to check whether both workspaces are displayed
        selectWindow("1");
        assertEquals("#1 workspace not present in workspace table",
                getProperty("WORKSPACE_BOOKING_TEXT", hotel1), 
                getText(getBy("WORKSPACE_TABLE_LINK_BY_ID", 0)));
        assertEquals("#2 workspace not present in workspace table",
                getProperty("WORKSPACE_VIEW_TEXT", hotel2),
                getText(getBy("WORKSPACE_TABLE_LINK_BY_ID", 1)));
        
        // Switch window 1 to second workspace
        clickAndWaitHttp(getBy("WORKSPACE_TABLE_LINK_BY_ID", 1));
        
        // Switch window 1 back to first workspace
        clickAndWaitHttp(getBy("WORKSPACE_TABLE_LINK_BY_ID", 1));
        
        // End conversation in window 2
        selectWindow("2");
        clickAndWaitHttp(getBy("BOOKING_CANCEL"));
        
        // Second workspace should disappear
        selectWindow("1");
        assertEquals("Workspace failure.", 1, getXpathCount(getBy("WORKSPACE_TABLE_ROW_COUNT")));
    }
}
