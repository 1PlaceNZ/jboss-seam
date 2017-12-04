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

package org.jboss.seam.example.todo.test.graphene;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunAsClient
@RunWith(Arquillian.class)
public class TodoFunctionalTest extends SeamGrapheneTest
{
   public static final String LOGIN_URL = "/login.seam";
   public static final String TODO_URL = "/todo.seam";
   public static final String LOGIN_USERNAME = "id=login:username";
   public static final String LOGIN_SUBMIT = "id=login:submit";
   public static final String NEW_ITEM_DESCRIPTION = "id=new:description";
   public static final String NEW_ITEM_CREATE = "id=new:create";
   
   public static final String NO_ITEMS_FOUND = "id=list:noItems";
   public static final String FIRST_ITEM_DESCRIPTION = "id=list:items:0:description";
   public static final String FIRST_ITEM_PRIORITY = "id=list:items:0:priority";
   public static final String FIRST_ITEM_DONE = "id=list:items:0:done";
   public static final String NTH_ITEM_DESCRIPTION = "id=list:items:{0}:description";
   public static final String NTH_ITEM_PRIORITY = "id=list:items:{0}:priority";
   public static final String NTH_ITEM_DONE = "id=list:items:{0}:done";
   public static final String ITEMS_COUNT = "xpath=//table[@id=\"list:items\"]/tbody/tr";
   public static final String ITEMS_UPDATE = "id=list:update";
   
   
   public static final String DEFAULT_USERNAME = "tester";
   
   private static boolean prepared = false;
   
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
   }
   
   public void prepareTestFixture() {
      String[] fixture = {"selenium test for todo example", "buy milk", "clean the bathroom"};
      assertTrue("Item list should be empty", isElementPresent(getBy(NO_ITEMS_FOUND)));
      for (String item : fixture) {
         type(getBy(NEW_ITEM_DESCRIPTION), item);
         clickAndWaitHttp(getBy(NEW_ITEM_CREATE));
      }
      assertEquals("Unexpected count of items.", fixture.length, getXpathCount(getBy(ITEMS_COUNT)));
   }
   
   @Before
   public void setUp() {
      open(contextPath + LOGIN_URL);
      type(getBy(LOGIN_USERNAME), DEFAULT_USERNAME, true);
      clickAndWaitHttp(getBy(LOGIN_SUBMIT));
      assertTrue("Navigation failure. Todo page expected.", browser.getCurrentUrl().contains(TODO_URL));
      
      if (!prepared) {
         prepareTestFixture();
         prepared = true;
      }
   }
   
   @Test
   public void getEntryDoneTest() {
      String description = browser.findElement(getBy(FIRST_ITEM_DESCRIPTION)).getAttribute("value");
      int itemCount = getXpathCount(getBy(ITEMS_COUNT));
      clickAndWaitHttp(getBy(FIRST_ITEM_DONE));
      assertFalse("Item should disappear from item list when done.", isTextOnPage(description));
      assertEquals("Unexpected count of items.", --itemCount, getXpathCount(getBy(ITEMS_COUNT)));
   }

   /**
    * This test sets high priority to first item and verifies that the item is be moved to the bottom and the priority number is kept.
    */
   @Test
   public void priorityTest() {
      String description = browser.findElement(getBy(FIRST_ITEM_DESCRIPTION)).getAttribute("value");
      String priority = "10";
      int itemCount = getXpathCount(getBy(ITEMS_COUNT));
      int lastItemRowId = itemCount - 1;
      type(getBy(FIRST_ITEM_PRIORITY), priority, true);
      clickAndWaitHttp(getBy(ITEMS_UPDATE));
      
      String nthItemDescription = browser.findElement(getBy(NTH_ITEM_DESCRIPTION, lastItemRowId)).getAttribute("value");
      assertEquals("Message should move to the end of item list after priority change.", description, nthItemDescription);
      
      String nthItemPriority = browser.findElement(getBy(NTH_ITEM_PRIORITY, lastItemRowId)).getAttribute("value");
      
      assertEquals("Unexpected priority.", priority, nthItemPriority);
   }
}
