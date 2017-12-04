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
package org.jboss.seam.example.common.test.seampay.graphene;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 *
 * @author jharting
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SeampayFunctionalTest extends SeamGrapheneTest {
    
    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }
    
    protected NumberFormat nf = NumberFormat.getNumberInstance();
    
    @Before
    @Override
    public void beforeTest() {
        open(contextPath + getProperty("MAIN_PAGE"));
    }

    /**
     * Submits new payment and asserts that remaining account balance is
     * correct.
     *
     * @throws ParseException
     */
    @Test
    public void payOnceTest() throws ParseException {
        int account = 0;
        BigDecimal amount = new BigDecimal(10);
        String to = "foo";
        
        clickAndWaitHttp(getBy("ACCOUNT_TABLE_LINK", account));
        BigDecimal expectedBalance = BigDecimal.valueOf(parseBalance(getText(getBy("ACCOUNT_TABLE_BALANCE", account))));
        submitPayment(to, amount.toString(), getBy("PAYMENT_ONLY_ONCE_RADIO"));
        assertTrue("Scheduled payment not confirmed.", isTextInSource(getProperty("PAYMENT_CONFIRMATION_MESSAGE", to)));
        assertEquals("Invalid count of payments.", 1, getXpathCount(getBy("PAYMENTS_COUNT")));
        clickAndWaitHttp(getBy("ACCOUNT_TABLE_LINK", account));
        assertEquals("No money were subtracted from account", expectedBalance.subtract(amount), BigDecimal.valueOf(parseBalance(getText(getBy("ACCOUNT_TABLE_BALANCE", account)))));
    }

    /**
     * Submits new payment with one minute interval and verifies the balance
     * after 60 seconds
     *
     * @throws ParseException
     * @throws InterruptedException
     */
    @Test
    public void payEveryMinuteTest() throws ParseException, InterruptedException {
        int account = 1;
        BigDecimal amount = new BigDecimal(10);
        String to = "foo";
        
        clickAndWaitHttp(getBy("ACCOUNT_TABLE_LINK", account));
        // create new payment
        submitPayment(to, amount.toString(), getBy("PAYMENT_EVERY_MINUTE_RADIO"));
        assertTrue("Scheduled payment not confirmed.", isTextInSource(getProperty("PAYMENT_CONFIRMATION_MESSAGE", to)));
        assertEquals("Invalid count of payments.", 1, getXpathCount(getBy("PAYMENTS_COUNT")));
        // wait
        Thread.sleep(5000);
        // get first balance
        clickAndWaitHttp(getBy("ACCOUNT_TABLE_LINK", account));
        BigDecimal firstBalance = BigDecimal.valueOf(parseBalance(getText(getBy("ACCOUNT_TABLE_BALANCE", account))));
        // wait 60 seconds
        Thread.sleep(60000);
        // get second balance
        clickAndWaitHttp(getBy("ACCOUNT_TABLE_LINK", account));
        BigDecimal secondBalance = BigDecimal.valueOf(parseBalance(getText(getBy("ACCOUNT_TABLE_BALANCE", account))));
        BigDecimal expectedSecondBalance = firstBalance.subtract(amount);
        assertEquals("No money were subtracted from account after a minute", expectedSecondBalance, secondBalance);
    }
    
    protected void submitPayment(String to, String amount, By radio) {
        type(getBy("PAYMENT_TO_FIELD"), to);
        type(getBy("PAYMENT_AMOUNT_FIELD"), amount.toString());
        click(radio);
        clickAndWaitHttp(getBy("PAYMENT_SUBMIT"));
    }
    
    protected Double parseBalance(String text) throws ParseException {
        // dirty but can hardly be parsed nicer
        String number = text.replaceAll("\\$", new String()).replaceAll(" ", new String()).trim();
        return nf.parse(number).doubleValue();
    }
}
