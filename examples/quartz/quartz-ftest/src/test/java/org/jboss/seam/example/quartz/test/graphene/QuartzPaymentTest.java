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
package org.jboss.seam.example.quartz.test.graphene;

import java.math.BigDecimal;
import java.text.ParseException;
import org.jboss.seam.example.common.test.seampay.graphene.SeampayFunctionalTest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 *
 * @author jharting
 *
 */
public class QuartzPaymentTest extends SeampayFunctionalTest {

    /**
     * Submits new payment using CRON and verifies that the balance is
     * subtracted correctly
     *
     * @throws ParseException
     * @throws InterruptedException
     */
    @Test
    public void testCRON() throws ParseException, InterruptedException {
        int account = 3;
        BigDecimal amount = new BigDecimal(10);
        String to = "foo";
        By accountLinkLocator = getBy("ACCOUNT_TABLE_LINK", account);
        By accountBalanceLocator = getBy("ACCOUNT_TABLE_BALANCE", account);
        // send every 20 seconds
        String cronExpression = "0/20 * * * * ?";

        clickAndWaitHttp(accountLinkLocator);

        // submit new cron job
        type(getBy("PAYMENT_TO_FIELD"), to);
        type(getBy("PAYMENT_AMOUNT_FIELD"), amount.toString());
        type(getBy("PAYMENT_CRON_FIELD"), cronExpression);
        clickAndWaitHttp(getBy("PAYMENT_CRON_SUBMIT"));

        assertTrue("Scheduled payment not confirmed.", isTextInSource(getProperty("PAYMENT_CONFIRMATION_MESSAGE", to)));
        assertEquals("Invalid count of payments.", 1, getXpathCount(getBy("PAYMENTS_COUNT")));
        // wait
        Thread.sleep(5000);
        // get balance
        clickAndWaitHttp(accountLinkLocator);

        BigDecimal firstBalance = BigDecimal.valueOf(parseBalance(getText(accountBalanceLocator)));
        // wait 20 seconds
        Thread.sleep(20000);
        // get balance after 20 seconds
        clickAndWaitHttp(accountLinkLocator);

        BigDecimal secondBalance = BigDecimal.valueOf(parseBalance(getText(accountBalanceLocator)));
        // wait 20 seconds
        Thread.sleep(20000);
        // get balance after 40 seconds
        clickAndWaitHttp(accountLinkLocator);

        BigDecimal thirdBalance = BigDecimal.valueOf(parseBalance(getText(accountBalanceLocator)));

        BigDecimal expectedSecondBalance = firstBalance.subtract(amount);
        BigDecimal expectedThirdBalance = firstBalance.subtract(amount).subtract(amount);
        assertEquals("Incorrect balance after 20 seconds.", expectedSecondBalance, secondBalance);
        assertEquals("Incorrect balance after 40 seconds.", expectedThirdBalance, thirdBalance);
    }
}
