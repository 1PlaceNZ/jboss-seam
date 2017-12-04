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
package org.jboss.seam.example.numberguess.test.graphene;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.numberguess.graphene.CommonNumberguessFunctionalTest;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * 
 * @author Jozef Hartinger
 * @author Marek Schmidt
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class NumberguessFunctionalTest extends CommonNumberguessFunctionalTest
{

   @Override
   protected void enterGuess(int guess)
   {
      if (isElementPresent(getBy("GUESS_FIELD")))
      {
         // using input text field
         super.enterGuess(guess);
      }
      else
      {
         if (isElementPresent(getBy("GUESS_MENU_BUTTON")))
         {
            browser.findElement(getBy("GUESS_MENU_BUTTON")).click();
            waitGui(browser).until().element(getBy("GUESS_MENU")).is().not().visible();
            browser.findElement(getBy("GUESS_MENU_ITEM", String.valueOf(guess))).click();
         }
         else if (isElementPresent(getBy("GUESS_RADIO")))
         {
            // using radio buttons
            int min = Integer.parseInt(getText(getBy("GUESS_MIN_VALUE")));
            int radio = guess - min;
            browser.findElement(getBy("GUESS_RADIO_ITEM", String.valueOf(radio))).click();
         } else {
            fail("Unable to enter guess. No input found.");
         }
         clickAndWaitHttp(getBy("GUESS_SUBMIT"));
      }
   }
   
   @Test
   public void cheatingTest() {
      int number;
      
      clickAndWaitHttp(getBy("CHEAT_BUTTON"));
      clickAndWaitHttp(getBy("CHEAT_YES_BUTTON"));

      number = Integer.parseInt(getText(getBy("CHEAT_NUMBER")));
      
      clickAndWaitHttp(getBy("CHEAT_DONE_BUTTON"));
      enterGuess(number);
      assertTrue("User should win when cheating. Random number was " + number, isOnWinPage());
   }

}
