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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * This class tests shopping cart
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ShoppingCartTest extends GrapheneDvdTest {
   
    private NumberFormat nf = NumberFormat.getNumberInstance();

    @Test
    public void simpleCartTest() {
        String[] dvds = new String[] { "Top Gun", "Pulp Fiction",
                "Forrest Gump" };
        for (String dvd : dvds) {
            addDVDToCart(dvd);
        }
        clickAndWaitHttp(getBy("CART"));
        for (String dvd : dvds) {
            assertTrue("Expected item not present in cart.", isElementPresent(getBy("CART_TABLE_ROW_BY_NAME", dvd)));
        }
    }

    @Test
    public void testCartCostCalculation() throws ParseException {
        String[] dvds = new String[] { "Top Gun", "Pulp Fiction", "Top Gun" };
        BigDecimal expectedSum = BigDecimal.ZERO;
        for (String dvd : dvds) {
            addDVDToCart(dvd);
        }
        clickAndWaitHttp(getBy("CART"));
        int items = getXpathCount(getBy("CART_TABLE_ITEM"));
        assertNotSame("Cart should not be empty.", 0, items);
        
        for (int i = 0; i < items; i++) {
            BigDecimal quantity = parseBalance(getValue(getBy("CART_TABLE_QUANTITY_BY_ID", i)));
            BigDecimal price = parseBalance(getText(getBy("CART_TABLE_PRICE_BY_ID", i)));
            BigDecimal priceForCurrentRow = price.multiply(quantity);
            expectedSum = expectedSum.add(priceForCurrentRow);
        }
        BigDecimal actualSum = parseBalance(getText(getBy("CART_SUBTOTAL")));
        assertEquals("Price sum in cart is incorrect.", 0, expectedSum
                .compareTo(actualSum));
    }

    @Test
    public void testRemovingCartItem() {
        String dvd = "Top Gun";
        addDVDToCart(dvd);
        clickAndWaitHttp(getBy("CART"));
        assertTrue("DVD is not in the cart.", isElementPresent(getBy("CART_TABLE_ROW_BY_NAME", dvd)));
        check(getBy("CART_TABLE_CHECKBOX_BY_NAME", dvd));
        
        clickAndWaitHttp(getBy("CART_TABLE_UPDATE_BUTTON"));
        
        assertFalse("Cart item was not removed.", isElementPresent(getBy("CART_TABLE_ROW_BY_NAME", dvd)));
    }

    /**
     * This method tries purchasing more copies of The Bourne Identity than are
     * available in stock
     */
    @Test
    public void testExceedingAvailableItemLimit() {
        String dvd = "The Bourne Identity";
        String amount = "300";
        addDVDToCart(dvd);
        clickAndWaitHttp(getBy("CART"));
        type(getBy("CART_TABLE_FIRST_ROW_QUANTITY"), amount, true);
        clickAndWaitHttp(getBy("CART_TABLE_UPDATE_BUTTON"));
        clickAndWaitHttp(getBy("CART_PURCHASE_BUTTON"));
        clickAndWaitHttp(getBy("CART_CONFIRM_BUTTON"));
        assertTrue("Message not displayed.", isElementPresent(getBy("CART_NOT_ENOUGH_COPIES_LEFT", dvd)));
        assertTrue(
                "Order should not be completed.",
                isElementPresent(getBy("CART_UNABLE_TO_COMPLETE_ORDER_MESSAGE")));
    }

    private void addDVDToCart(String dvdName) {
        assertTrue("User should be logged in.", isLoggedIn());
        clickAndWaitHttp(getBy("SHOP"));
        type(getBy("SEARCH_FIELD"), dvdName, true);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        
        check(getBy("SEARCH_RESULT_FIRST_ROW_CHECKBOX"));
        clickAndWaitHttp(getBy("SEARCH_RESULT_UPDATE_BUTTON"));
    }
    
    private String getValue(By selector) {
       return browser.findElement(selector).getAttribute("value");
    }
    
    private BigDecimal parseBalance(String text) throws ParseException {
       String number = text.replaceAll("\\$", "").trim();
       return BigDecimal.valueOf(nf.parse(number).doubleValue());
    }
}
