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

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This class tests search functionality of the example
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SearchTest extends GrapheneDvdTest {

    /**
     * This test does simple search for dvd.
     */
    //@Test(groups = { "search" }, dependsOnGroups = { "login.basic" })
    @Test
    public void testSearch() {
        String searchString = "top gun";
        clickAndWaitHttp(getBy("SHOP"));

        assertTrue("Navigation failed.", browser.getCurrentUrl().contains(
                getProperty("SHOP_URL")));
        type(getBy("SEARCH_FIELD"), searchString, true);
        
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        
        //exact number of matches depends on search algorithm,
        //so we only check that at least something was found:
        assertTrue("Unexpected number of results. One result expected.", getXpathCount(getBy("SEARCH_RESULT_ITEM")) > 0);
        
        clickAndWaitHttp(getBy("SEARCH_RESULT_FIRST_ROW_LINK"));
       
        assertTrue("Navigation failure.", browser.getCurrentUrl().contains(
                getProperty("DVD_URL")));
    }

    /**
     * This test does simple search in two windows verifying they do not affect
     * each other
    * @throws InterruptedException 
     */
    //@Test(dependsOnMethods = { "testSearch" }, dependsOnGroups = { "login.basic" })
    @Test
    public void testMultipleWindowSearch() throws InterruptedException {
        String searchString1 = "Forrest Gump";
        String searchString2 = "The Shawshank Redemption";

        assertTrue("User should be logged in by now.", isLoggedIn());
        clickAndWaitHttp(getBy("SHOP"));
        type(getBy("SEARCH_FIELD"), searchString1, true);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        assertEquals("Unexpected search result in first window.",
                searchString1, getText(getBy("SEARCH_RESULT_FIRST_ROW_LINK")));
        
        String firstWindowHandle = browser.getWindowHandle();
        // search for dvd in second window
        openWindow(contextPath + getProperty("HOME_PAGE"), "1");
        selectWindow("1");
        Thread.sleep(10000); // ugly but turned out to be the most browser-compatible solution
        assertTrue("User should be logged in by now.", isLoggedIn());
        clickAndWaitHttp(getBy("SHOP"));
        type(getBy("SEARCH_FIELD"), searchString2);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        assertEquals("Unexpected search result in second window.",
                searchString2, getText(getBy("SEARCH_RESULT_FIRST_ROW_LINK")));
        selectWindow(firstWindowHandle);
        browser.navigate().refresh();
        assertEquals("Unexpected search result in first window after refresh.",
                searchString1, getText(getBy("SEARCH_RESULT_FIRST_ROW_LINK")));

    }
}
