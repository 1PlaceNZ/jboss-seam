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
package org.jboss.seam.example.remoting.chatroom.test.graphene;

import com.google.common.base.Predicate;
import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import static org.jboss.seam.example.common.test.SeamGrapheneTest.getProperty;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * This class tests functionality of remoting/chatroom example. The test opens
 * two browsers and tests communication between users.
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class ChatroomFunctionalTest extends SeamGrapheneTest {

    public static long timeout = 2000;

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    private WebDriver driver;

    private WebDriver driver2;
    
    @Before
    @Override
    public void beforeTest() throws MalformedURLException {
        driver = new FirefoxDriver();
        driver2 = new FirefoxDriver();
        
        driver.navigate().to(contextPath + getProperty("HOME_PAGE"));
        driver2.navigate().to(contextPath + getProperty("HOME_PAGE"));
    }
    
    @After
    public void afterTest() throws MalformedURLException {
        driver.quit();
        driver2.quit();
    }
    
//    Graphene-WebDriver can't do multiple browsers ARQGRA-72
//    @Drone
//    @Secon
//    protected WebDriver browser2;
    @Test // place holder - should be replaced by better tests as soon as JBSEAM-3944 is resolved
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", getProperty("HOME_PAGE_TITLE"), driver.getTitle());
    }

    @Test
    public void connectAndChatTest() {

        /*connect user to chat*/
        connect();
        /*verify that user is connected and is seen by other users*/
        verifyConnecting();
        /*exchange several messages*/
        chat();
        /*disconnect user from chat*/
        disconnect();
        /*verify that user is disconnected and is not in a list of users anymore*/
        verifyDisconnecting();
    }

    public void connect() {
        driver.findElement(getBy("NAME_INPUT")).sendKeys(getProperty("NAME1"));
        driver.findElement(getBy("CONNECT_BUTTON")).click();
    }

    public void verifyConnecting() {
        waitForElement(driver, "MARTIN_LISTED");
        
        driver2.findElement(getBy("NAME_INPUT")).sendKeys(getProperty("NAME2"));
        driver2.findElement(getBy("CONNECT_BUTTON")).click();
        waitForElement(driver2, "JOZEF_LISTED");
        waitForElement(driver2, "MARTIN_LISTED");
        
        waitForText(driver, "CHAT_WINDOW", "JOZEF_CONNECTED");
        waitForElement(driver, "JOZEF_LISTED");
    }

    public void disconnect() {
        driver2.findElement(getBy("DISCONNECT_BUTTON")).click();
    }

    public void verifyDisconnecting() {
        waitForElementNotPresent(driver2, "JOZEF_LISTED");
        waitForElement(driver2, "DISCONNECT_BUTTON_DISABLED");

        waitForText(driver, "CHAT_WINDOW", "JOZEF_DISCONNECTED");
        waitForElementNotPresent(driver, "JOZEF_LISTED");
        driver.findElement(getBy("DISCONNECT_BUTTON")).click();
        waitForElementNotPresent(driver,"MARTIN_LISTED");
        waitForElement(driver, "DISCONNECT_BUTTON_DISABLED");
    }

    public void chat() {
        /*first user is sending a message*/
        driver.findElement(getBy("MESSAGE_INPUT")).sendKeys(getProperty("MESSAGE_FROM_MARTIN"));
        driver.findElement(getBy("MESSAGE_INPUT")).sendKeys(Keys.RETURN);
        waitForText(driver, "CHAT_WINDOW", "MARTIN_GT");
        waitForText(driver, "CHAT_WINDOW", "MESSAGE_FROM_MARTIN");

        waitForText(driver2, "CHAT_WINDOW", "MARTIN_GT");
        waitForText(driver2, "CHAT_WINDOW", "MESSAGE_FROM_MARTIN");

        /*second user is sending a message*/
        driver2.findElement(getBy("MESSAGE_INPUT")).sendKeys(getProperty("MESSAGE_FROM_JOZEF"));
        driver2.findElement(getBy("MESSAGE_INPUT")).sendKeys(Keys.RETURN);
        waitForText(driver2, "CHAT_WINDOW", "JOZEF_GT");
        waitForText(driver2, "CHAT_WINDOW", "MESSAGE_FROM_JOZEF");

        waitForText(driver, "CHAT_WINDOW", "JOZEF_GT");
        waitForText(driver, "CHAT_WINDOW", "MESSAGE_FROM_JOZEF");
    }

    private void waitForElement(WebDriver browser, final String propertyName, final Object... args) {
        new WebDriverWait(browser, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver input) {
                return input.findElement(getBy(propertyName, args)).isDisplayed();
            }
            
        });
    }

    private void waitForText(WebDriver browser, final String elementPropertyName, final String textPropertyName, final Object... args) {
         new WebDriverWait(browser, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver input) {
                return input.findElement(getBy(elementPropertyName, args)).getText().contains(getProperty(textPropertyName));
            }
            
        });
    }

    private void waitForElementNotPresent(WebDriver browser, final String propertyName, final Object... args) {
        new WebDriverWait(browser, timeout).until(new Predicate<WebDriver>() {

            @Override
            public boolean apply(WebDriver input) {
                try {
                    input.findElement(getBy(propertyName, args));
                    return false;
                } catch(NotFoundException nfe) {
                    return true;
                }
            }
            
        });
    }
}
