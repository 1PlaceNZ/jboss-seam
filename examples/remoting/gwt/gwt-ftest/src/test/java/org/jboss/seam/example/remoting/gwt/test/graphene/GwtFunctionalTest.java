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

package org.jboss.seam.example.remoting.gwt.test.graphene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.jboss.arquillian.graphene.Graphene.waitModel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Predicate;

@RunAsClient
@RunWith(Arquillian.class)
public class GwtFunctionalTest extends SeamGrapheneTest
{
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
   }
   
   protected static final String GWT_URL = "HelloWorld.html";
   protected static final String GWT_TITLE = "Wrapper HTML for HelloWorld";
   protected static final String ASK_BUTTON = "xpath=(//button)[1]";
   protected static final String TEXT_FIELD = "xpath=(//input)[1]";
   
   protected static final String ENTER_TEXT_WITHOUT = "Text without question mark at the end";
   protected static final String ENTER_TEXT_WITH = "Text WITH question mark at the end?";
   
   protected static final String MESSAGE_WITHOUT = "A question has to end with a \'?\'";
   protected static final String MESSAGE_WITH = "42. Its the real question that you seek now.";
 
   @Before
   public void setUp()
   {
      open(contextPath + GWT_URL);
   }

   @Test
   public void simplePageContentTest()
   {      
      assertTrue("Home page of Remoting/Gwt Example expected", browser.getCurrentUrl().contains(GWT_URL));
      assertTrue("Different page title expected ale je:"+ browser.getTitle(),browser.getTitle().contains(GWT_TITLE));
      assertTrue("Home page should contain Text field", isElementPresent(getBy(TEXT_FIELD)));
      assertTrue("Home page should contain Ask button", isElementPresent(getBy(ASK_BUTTON)));
   }
   
   @Test
   public void withoutQuestionMarkTest(){
      type(getBy(TEXT_FIELD), ENTER_TEXT_WITHOUT, true);
      click(getBy(ASK_BUTTON));
      Alert alert = waitForAlertPresent();
      try {
         String result = alert.getText();
         assertEquals("An alert message should show up and should contain message \"" + MESSAGE_WITHOUT + "\"", MESSAGE_WITHOUT, result);
      }
      finally {
         alert.dismiss();
      }
   } 
   
   @Test
   public void withQuestionMarkTest(){
      type(getBy(TEXT_FIELD), ENTER_TEXT_WITH, true);
      click(getBy(ASK_BUTTON));
      Alert alert = waitForAlertPresent();
      try {
         String result = alert.getText();
         assertEquals("An alert message should show up and should contain message \"" + MESSAGE_WITH + "\"", MESSAGE_WITH, result);
      }
      finally {
         alert.dismiss();
      }
   }    
   
   public Alert waitForAlertPresent() {
      waitModel(browser).until(new Predicate<WebDriver>() {
         public boolean apply(WebDriver browser) {
            try {
               browser.switchTo().alert();
               return true;
            }
            catch(NoAlertPresentException x) {
               return false;
            }
         }
     });
      
      return browser.switchTo().alert();
   }
}
