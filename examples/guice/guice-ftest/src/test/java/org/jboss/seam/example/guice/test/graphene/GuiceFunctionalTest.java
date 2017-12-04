/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, 2013 Red Hat Middleware LLC, and individual contributors
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
package org.jboss.seam.example.guice.test.graphene;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
* @author Martin Gencur
* @author Marek Schmidt
* 
*/
@RunAsClient
@RunWith(Arquillian.class)
public class GuiceFunctionalTest extends SeamGrapheneTest
{
   protected static final String BAR_URL = "/bar.seam";
   protected static final String JUICE_OF_THE_DAY = "Apple Juice* - 10 cents"; 
   protected static final String ANOTHER_JUICE = "Orange Juice - 12 cents";
   protected static final String GUICE_TITLE = "Juice Bar";
   
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
   }
   
   @Override
   @Before
   public void beforeTest() throws MalformedURLException {
       open(contextPath.toString() + BAR_URL);
   }

   @Test
   public void simplePageContentTest()
   {
      assertTrue("Home page of Guice Example expected", browser.getCurrentUrl().contains(BAR_URL));
      assertTrue("Different page title expected",browser.getTitle().contains(GUICE_TITLE));
      assertTrue("Juice of the day should contain its name and price", browser.getPageSource().contains(JUICE_OF_THE_DAY));
      assertTrue("Another juice should contain its name and price", browser.getPageSource().contains(ANOTHER_JUICE));
   }
}
