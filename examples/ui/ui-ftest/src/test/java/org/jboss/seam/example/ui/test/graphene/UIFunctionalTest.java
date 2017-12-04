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
package org.jboss.seam.example.ui.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import static org.jboss.arquillian.graphene.Graphene.waitModel;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * This class tests functionality of UI example
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class UIFunctionalTest extends UIFunctionalTestBase {

    /**
     * Place holder - just verifies that example deploys
     */
    @Test
    @InSequence(1)
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", HOME_PAGE_TITLE, browser.getTitle());
    }

    @Test
    @InSequence(2)
    public void selectItemsTest() {
        // HTMLUnit has issues with By.xpath("//input[contains(@value,'Peter Muir')]");
        Assume.assumeTrue(isRealBrowser());
        
        String title = "Mr.";
        String name = "Martin Gencur";
        String continent = "Europe";
        String age = "24";
        String pet = "Dog (Needs lots of exercise)";
        String colour1 = "Green", colour2 = "Yellow";
        String book = "Pride and Prejudice by Jane Austin (British)";
        String film = "Blade Runner directed by Ridley Scott";
        clickAndWaitHttp(SELECT_ITEMS_LINK);
        selectByText(SELECT_ITEMS_TITLE, title);
        type(SELECT_ITEMS_NAME, name);
        selectByText(SELECT_ITEMS_CONTINENT, continent);
        check(SELECT_ITEMS_USER);
        check(SELECT_ITEMS_ADMIN);
        check(SELECT_ITEMS_MANAGER);
        check(SELECT_ITEMS_SUPERADMIN);
        selectByText(SELECT_ITEMS_AGE, age);
        selectByText(SELECT_ITEMS_PET, pet);
        selectByText(SELECT_ITEMS_COLOURS, colour1);
        selectByText(SELECT_ITEMS_COLOURS, colour2);
        selectByText(SELECT_ITEMS_BOOK, book);
        selectByText(SELECT_ITEMS_FILM, film);
        clickAndWaitHttp(SELECT_ITEMS_APPLY);
        check(SELECT_ITEMS_COUNTRY);
        clickAndWaitHttp(SELECT_ITEMS_APPLY);
        assertTrue("Page should contain \"Successfully updated\"", isTextInSource("Successfully updated"));
    }

    @Test
    @InSequence(3)
    public void fragmentTest() {
        clickAndWaitHttp(FRAGMENT_LINK);
        assertTrue("Page should contain \"fragment is rendered\"", isTextInSource("This fragment is rendered whilst"));
    }

    @Test
    @InSequence(4)
    public void formattedTextTest() {
        clickAndWaitHttp(FORMATTED_TEXT_LINK);
        assertTrue("Page should contain information about Pete Muir working all the time on Seam", isTextOnPage("works on Seam, of course"));
    }

    @Test
    @InSequence(5)
    public void buttonAndLinkTest() {
        clickAndWaitHttp(BUTTON_AND_SLINK_LINK);
        assertTrue("Page should contain \"A fragment to jump to\"", isTextInSource("A fragment to jump to"));
        click(JUMP_LINK);
        waitModel().until().element(JUMP_BUTTON).is().present();
        click(JUMP_BUTTON);
        waitModel().until().element(LINK_LINK).is().present();
        clickAndWaitHttp(LINK_LINK);
        clickAndWaitHttp(DO_ACTION_LINK);
        assertTrue("Page should contain \"A simple action was performed\"", isTextInSource("A simple action was performed"));
        clickAndWaitHttp(DO_ACTION_BUTTON);
        assertTrue("Page should contain \"A simple action was performed\"", isTextInSource("A simple action was performed"));
        assertTrue("Page should contain disabled link", isElementPresent(DISABLED_DO_ACTION_LINK));
        assertTrue("Page should contain disabled button", isElementPresent(DISABLED_DO_ACTION_BUTTON));
        clickAndWaitHttp(BEGIN_CONVERSATION_LINK);
        clickAndWaitHttp(END_CONVERSATION_BUTTON);
        assertFalse("Page shouldn't contain \"A simple action was performed\"", isTextInSource("A simple action was performed"));
        clickAndWaitHttp(ADD_PARAMETER_LINK);
        clickAndWaitHttp(ADD_PARAMETER_BUTTON);
        assertTrue("Page should contain \"Foo = bar\"", isTextInSource("Foo = bar"));
    }

    @Test
    @InSequence(6)
    public void cacheTest() {
        clickAndWaitHttp(CACHE_LINK);
        assertTrue("Page should contain some cached text", isTextInSource("Some cached text"));
    }

    @Test
    @InSequence(7)
    public void validateEqualityTest() {
        String name1 = "martin";
        String name2 = "peter";
        String age1 = "20";
        String age2 = "30";
        clickAndWaitHttp(VALIDATE_EQUALITY_LINK);

        type(NAME_INPUT, name1);
        type(NAME_VERIFICATION_INPUT, name1);
        clickAndWaitHttp(CHECK_NAME_BUTTON);
        assertTrue("Page should contain \"OK!\"" + " but contains:" + browser.findElement(By.tagName("body")).getText(), isTextInSource("OK!"));

        type(NAME_INPUT, name1);
        type(NAME_VERIFICATION_INPUT, name2);
        clickAndWaitHttp(CHECK_NAME_BUTTON);
        assertTrue("Page should contain \"Must be the same as name!\"", isTextInSource("Must be the same as name!"));

        type(MINIMUM_AGE_INPUT, age1);
        type(MAXIMUM_AGE_INPUT, age2);
        clickAndWaitHttp(CHECK_AGES_BUTTON);
        assertTrue("Page should contain \"OK!\"", isTextInSource("OK!"));
        type(MINIMUM_AGE_INPUT, age1);
        type(MAXIMUM_AGE_INPUT, age1);
        clickAndWaitHttp(CHECK_AGES_BUTTON);
        assertTrue("Page should contain \"Must be larger than minimum!\"", isTextInSource("Must be larger than minimum!"));
        type(MINIMUM_AGE_INPUT, age2);
        type(MAXIMUM_AGE_INPUT, age1);
        clickAndWaitHttp(CHECK_AGES_BUTTON);
        assertTrue("Page should contain \"Must be larger than minimum!\"", isTextInSource("Must be larger than minimum!"));
    }

    @Test
    @InSequence(8)
    public void validateEquality2Test() {
        String date1 = "2009-08-21";
        String date2 = "2009-08-25";
        clickAndWaitHttp(VALIDATE_EQUALITY2_LINK);

        type(DATE_INPUT, date1);
        type(DATE_VERIFICATION_INPUT, date1);
        clickAndWaitHttp(CHECK_DATE_BUTTON);
        assertTrue("Page should contain \"OK!\"", isTextInSource("OK!"));

        type(DATE_INPUT, date1);
        type(DATE_VERIFICATION_INPUT, date2);
        clickAndWaitHttp(CHECK_DATE_BUTTON);
        assertTrue("Page should contain \"Value does not equal that in 'date'\"", isTextInSource("Value does not equal"));
        //assertTrue("Page should contain information about Pete Muir working all the time on Seam", isTextOnPage("works on Seam, of course"));
    }

    @Test
    @InSequence(9)
    public void resourceDownloadTest() {
        String textToFind1 = "abc";
        String textToFind2 = "123";
        clickAndWaitHttp(RESOURCE_DOWNLOAD_LINK);

        assertTrue("File download failed: Restful with s:download \"Text\"", isDownloadWorking("resources.seam?id=1", textToFind1));
        assertTrue("File download failed: Restful with s:download \"Numbers\"", isDownloadWorking("resources.seam?id=2", textToFind2));
    }
}
