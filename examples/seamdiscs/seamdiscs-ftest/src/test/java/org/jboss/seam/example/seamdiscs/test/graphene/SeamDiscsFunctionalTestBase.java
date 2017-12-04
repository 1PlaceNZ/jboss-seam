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
package org.jboss.seam.example.seamdiscs.test.graphene;

import com.thoughtworks.selenium.SeleniumException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

/**
 *
 * @author Ondrej Skutka
 *
 */
public abstract class SeamDiscsFunctionalTestBase extends SeamGrapheneTest {
    // TODO final

    public static By LOGIN = By.id("loginlink");
    public static By LOGOUT = By.id("logout");
    public static By LOGIN_USERNAME = By.id("login:username");
    public static By LOGIN_PASSWORD = By.id("login:password");
    public static By LOGIN_LOGIN = By.id("login:loginbutton");
    public static String HOME_URL = "home.seam";
//  public static By SECURITY = By.id("security");
//  
    public static By MANAGE_DISCS = By.id("manageDiscs");
    public static By CREATE_ARTIST_BUTTON = By.id("addArtist");
    public static By CREATE_BAND_BUTTON = By.id("addBand");
    public static By ARTIST_FILTER = By.xpath("//form[1]//input[@type=\"text\"]");
    public static By ARTIST_NAME = By.id("artist:name");
    public static By ARTIST_CREATE_DISC_BUTTON = By.id("artist:addDisc");
    public static By ARTIST_DESCRIPTION = By.id("artist:description");
    public static By ARTIST_PERSIST = By.id("artist:persist");
    public static By ARTIST_UPDATE = By.id("artist:update");
    public static By ARTIST_CANCEL = By.id("artist:cancel");
    public static By ARTIST_ADD_BAND_MEMBER = By.id("artist:addBandMember");
    public static String ARTIST_NTH_BAND_MEMBER = "xpath=//form[@id=\"artist\"]//tr[1]//tr[3]/td[2]//ul/li[{0}]//input[@type=\"text\"]";
    public static By ARTIST_LAST_BAND_MEMBER = By.xpath("//form[@id=\"artist\"]//tr[1]//tr[3]/td[2]//ul/li[last()]//input[@type=\"text\"]");
    public static String ARTIST_NTH_DISC = "xpath=//div[@id=\"artist:discs\"]/table/tbody/tr/td/table/tbody/tr[{0}]";
    public static String ARTIST_NTH_DISC_SHOW_DETAILS = ARTIST_NTH_DISC + "/td[1]/div/a[2]";
    public static String ARTIST_NTH_DISC_NAME = ARTIST_NTH_DISC + "/td[2]/input";
    public static String ARTIST_NTH_DISC_YEAR = ARTIST_NTH_DISC + "/td[3]//input";
    public static String ARTIST_NTH_DISC_DETAIL = ARTIST_NTH_DISC + "/td";
    public static By ARTISTS_FIRST_ARTIST_LINK = By.xpath("//span[@id=\"artists\"]//table/tbody/tr/td/table/tbody/tr[2]/td[2]/a");
    public static By ARTISTS_NEXT_PAGE_LINK = By.xpath("//span[@id=\"artists\"]//td[1]//td[2]//td[5]/a");
    public static String ARTIST_TABLE_ROW_BY_NAME = "xpath=//span[@id=\"artists\"]//tr[normalize-space(td/a/text())=\"{0}\"]";
    // these locators can only be used catenated with ARTIST_TABLE_ROW_BY_NAME
    public static String ARTIST_TABLE_ROW_LINK = ARTIST_TABLE_ROW_BY_NAME + "/td[2]/a";
    public static By MANAGE_ARTISTS = By.id("manageArtists");
    public static By CREATE_DISC_BUTTON = By.id("addDisc");
    public static By DISC_DETAIL_TITLE = By.xpath("//form[@id=\"disc\"]//tr//tr[2]/td[2]/input");
    public static By DISC_DETAIL_RELEASE_DATE = By.xpath("//form[@id=\"disc\"]//tr//tr[3]/td[2]//input");
    public static By DISC_DETAIL_ARTIST = By.xpath("//form[@id=\"disc\"]//tr//tr[4]//select");
    public static By DISC_DETAIL_DESCRIPTION = By.id("description");
    public static By DISC_DETAIL_UPDATE = By.id("update");
    public static By DISC_DETAIL_PERSIST = By.id("persist");
    public static By DISC_DETAIL_REMOVE = By.id("remove");
    public static By DISC_DETAIL_CANCEL = By.id("cancel");
    public static By DISCS_NEXT_PAGE_LINK = By.xpath("//table[@id=\"discs\"]//td[1]//td[2]//td[5]/a");
    public static String DISC_TABLE_ROW_BY_NAME = "xpath=//table[@id=\"discs\"]//tr/td/a[contains(text(), \"{0}\")]";
    // these locators can only be used catenated with DISC_TABLE_ROW_BY_NAME
    public static String DISC_TABLE_ROW_LINK = DISC_TABLE_ROW_BY_NAME + "/td[2]/a";
    public static String EMPTY_DISC_DESCRIPTION = "None known";
    public static String DEFAULT_USERNAME = "administrator";
    public static String DEFAULT_PASSWORD = "administrator";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Override
    @Before
    public void beforeTest() {
        open(contextPath + HOME_URL);
        if (!isLoggedIn()) {
            login();
        }
    }

    public void login() {
        login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public void login(String username, String password) {
        clickAndWaitHttp(LOGIN);
        type(LOGIN_USERNAME, username);
        type(LOGIN_PASSWORD, password);
        clickAndWaitHttp(LOGIN_LOGIN);
    }

    protected boolean isLoggedIn() {
        return !isElementPresent(LOGIN) && isElementPresent(LOGOUT);
    }

    /**
     * Checks whether specified disc contains expected data.
     */
    protected void checkDisc(int tableRow, String expectedDiscTitle, String expectedReleaseDate, String expectedDescription) {
        tableRow++; // first row is header
        String actualDiscTitle = getValue(getBy(ARTIST_NTH_DISC_NAME, tableRow));
        assertEquals(expectedDiscTitle, actualDiscTitle);

        String actualReleaseDate = "";
        if (isElementPresent(getBy(ARTIST_NTH_DISC_YEAR, tableRow))) {
            try {
                actualReleaseDate = getValue(getBy(ARTIST_NTH_DISC_YEAR, tableRow));
            } catch (SeleniumException ex) {
                // intentianally left blank
            }
            assertEquals(expectedReleaseDate, actualReleaseDate);
        }

        clickAndWaitHttp(getBy(ARTIST_NTH_DISC_SHOW_DETAILS, tableRow));
        String actualDescription = getText(getBy(ARTIST_NTH_DISC_DETAIL, tableRow + 1));
        assertEquals(expectedDescription, actualDescription);
        clickAndWaitHttp(getBy(ARTIST_NTH_DISC_SHOW_DETAILS, tableRow));
    }

    /**
     * Checks whether specified disc contains expected data.
     */
    protected void checkDiscDetail(String expectedDiscTitle, String expectedReleaseDate, String expectedDescription, String expectedArtist) {
        String actualDiscTitle = getValue(DISC_DETAIL_TITLE);
        assertEquals(expectedDiscTitle, actualDiscTitle);

        String actualReleaseDate = "";
        if (!getValue(DISC_DETAIL_RELEASE_DATE).isEmpty()) {
            actualReleaseDate = getValue(DISC_DETAIL_RELEASE_DATE);
            assertEquals(expectedReleaseDate, actualReleaseDate);
        }

        String actualDescription = getText(DISC_DETAIL_DESCRIPTION);
        assertEquals(expectedDescription, actualDescription);

        String actualArtist = getSelectedLabel(DISC_DETAIL_ARTIST);
        assertEquals(expectedArtist, actualArtist);
    }

    /**
     * Creates new disc. Expected to be on artist's edit page.
     *
     */
    protected void createDisc(String title, String year) {
        clickAndWaitHttp(ARTIST_CREATE_DISC_BUTTON);
        type(getBy(ARTIST_NTH_DISC_NAME, "last()"), title);
        type(getBy(ARTIST_NTH_DISC_YEAR, "last()"), year);
    }

    /**
     * Finds the specified artist in paginated artists page and clicks it.
     * Expected to be on artists page.
     *
     */
    protected void findAndClickArtist(String artistName) {
        // find the artist's page (it's paginated) and click it
        while (!isElementPresent(getBy(ARTIST_TABLE_ROW_BY_NAME, artistName))) { // click through pages
            assertTrue("Artist " + artistName + " not found.", isElementPresent(ARTISTS_NEXT_PAGE_LINK));
            click(ARTISTS_NEXT_PAGE_LINK); // ajax
            // possibly wait for change of table here
            sleep(3000);
        }

        clickAndWaitHttp(getBy(ARTIST_TABLE_ROW_LINK, artistName)); // click artist link
    }

    /**
     * Finds the specified disc in paginated discs page and clicks it. Expected
     * to be on discs page.
     *
     */
    protected void findAndClickDisc(String discName) {
        // find the disc page (it's paginated) and click it
        while (!isElementPresent(getBy(DISC_TABLE_ROW_BY_NAME, discName))) { // click through pages
            assertTrue("Disc " + discName + " not found.", isElementPresent(DISCS_NEXT_PAGE_LINK));
            clickAndWaitAjax(DISCS_NEXT_PAGE_LINK); // ajax
            sleep(3000);
        }

        clickAndWaitHttp(getBy(DISC_TABLE_ROW_LINK, discName)); // click disc link
    }

    protected void addBandMember(String artistName) {
        clickAndWaitHttp(ARTIST_ADD_BAND_MEMBER);
        type(ARTIST_LAST_BAND_MEMBER, artistName);
    }

    /**
     * Checks whether specified disc contains expected data.
     */
    protected void checkBandMember(int tableRow, String expectedBandMember) {
        String actualMemberName = getValue(getBy(ARTIST_NTH_BAND_MEMBER, tableRow));
        assertEquals(expectedBandMember, actualMemberName);
    }

    protected String getSelectedLabel(By by) {
        return new Select(browser.findElement(by)).getFirstSelectedOption().getText();
    }

    public String getValue(By by) {
        return browser.findElement(by).getAttribute("value");
    }
}
