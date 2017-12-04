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

package org.jboss.seam.example.common.test.numberguess.graphene;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class CommonNumberguessFunctionalTest extends SeamGrapheneTest
{
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
   }
   
   @Before
   public void setUp()
   {
      open(contextPath + getProperty("MAIN_PAGE"));
   }

   @Test
   public void smartTest()
   {

      int min;
      int max;
      int guess;
      int i = 0;

      while (browser.getCurrentUrl().contains(getProperty("GUESS_LOCATION")))
      {
         if (i > 9)
         {
            fail("Game should not be longer than 10 guesses");
         }
         min = Integer.parseInt(getText(getBy("GUESS_MIN_VALUE")));
         max = Integer.parseInt(getText(getBy("GUESS_MAX_VALUE")));
         guess = min + ((max - min) / 2);
         enterGuess(guess);
         i++;
      }
      assertTrue("Win page expected after playing smart.", isOnWinPage());
   }

   @Test
   public void linearTest()
   {
      int guess = 0;

      while (browser.getCurrentUrl().contains(getProperty("GUESS_LOCATION")))
      {
         enterGuess(++guess);
         assertTrue("Guess count exceeded.", guess <= 10);
      }
      if (guess < 10)
      {
         assertTrue("Player should not lose before 10th guess.", isOnWinPage());
      }
      else
      {
         assertTrue("After 10th guess player should lose or win.", isOnLosePage() || isOnWinPage());
      }

   }

   protected void enterGuess(int guess)
   {
      type(getBy("GUESS_FIELD"), String.valueOf(guess), true);
      clickAndWaitHttp(getBy("GUESS_SUBMIT"));
   }

   protected boolean isOnWinPage()
   {
      return browser.getCurrentUrl().contains(getProperty("WIN_LOCATION"));
   }

   protected boolean isOnLosePage()
   {
      return browser.getCurrentUrl().contains(getProperty("LOSE_LOCATION"));
   }

}
