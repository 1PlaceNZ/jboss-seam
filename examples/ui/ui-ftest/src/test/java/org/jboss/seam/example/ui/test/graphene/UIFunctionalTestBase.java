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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.openqa.selenium.By;

/**
 * Base class for UI functional tests
 *
 * @author Martin Gencur
 *
 */
public class UIFunctionalTestBase extends SeamGrapheneTest {

    public static final String HOME_PAGE = "index.seam";
    public static final String HOME_PAGE_TITLE = "UI Example:";
    public static final By SELECT_ITEMS_LINK = By.xpath("//a[contains(@href,\"selectItems\")]");
    public static final By FRAGMENT_LINK = By.xpath("//a[contains(@href,\"fragment\")]");
    public static final By FORMATTED_TEXT_LINK = By.xpath("//a[contains(@href,\"formattedText\")]");
    public static final By BUTTON_AND_SLINK_LINK = By.xpath("//a[contains(@href,\"linkAndButton\")]");
    public static final By CACHE_LINK = By.xpath("//a[contains(@href,\"cache\")]");
    public static final By VALIDATE_EQUALITY_LINK = By.xpath("//a[contains(@href,\"equalityValidator\")]");
    public static final By VALIDATE_EQUALITY2_LINK = By.xpath("//a[contains(@href,\"equalityValidatorWConvert\")]");
    public static final By RESOURCE_DOWNLOAD_LINK = By.xpath("//a[contains(@href,\"resource\")]");
    public static final By SELECT_ITEMS_TITLE = By.xpath("//select[option[contains(@value,'MR')]]");
    public static final By SELECT_ITEMS_NAME = By.xpath("//input[contains(@value,'Peter Muir')]");
    public static final By SELECT_ITEMS_CONTINENT = By.xpath("//select[option[contains(text(),'Europe')]]");
    public static final By SELECT_ITEMS_USER = By.xpath("//input[@type='checkbox'][@value='USER']");
    public static final By SELECT_ITEMS_ADMIN = By.xpath("//input[@type='checkbox'][@value='ADMIN']");
    public static final By SELECT_ITEMS_MANAGER = By.xpath("//input[@type='checkbox'][@value='MANAGER']");
    public static final By SELECT_ITEMS_SUPERADMIN = By.xpath("//input[@type='checkbox'][@value='SUPERADMIN']");
    public static final By SELECT_ITEMS_AGE = By.xpath("//select[option[contains(text(),'24')]]");
    public static final By SELECT_ITEMS_PET = By.xpath("//select[option[contains(@value,'Dog')]]");
    public static final By SELECT_ITEMS_COLOURS = By.xpath("//select[option[contains(text(),'Green')]]");
    public static final By SELECT_ITEMS_BOOK = By.xpath("//select[option[contains(text(),'Pride and Prejudice by Jane Austin (British)')]]");
    public static final By SELECT_ITEMS_FILM = By.xpath("//select[option[contains(text(),'Blade Runner directed by Ridley Scott')]]");
    public static final By SELECT_ITEMS_APPLY = By.xpath("//input[@type='submit'][@value='Apply']");
    public static final By SELECT_ITEMS_COUNTRY = By.xpath("//input[@type='radio'][@value='18']");
    public static final By JUMP_LINK = By.xpath("//a[contains(text(),'Jump')]");
    public static final By JUMP_BUTTON = By.xpath("//input[@type='button'][@value='Jump']");
    public static final By LINK_LINK = By.xpath("//a[contains(text(),'Link')]");
    public static final By DO_ACTION_LINK = By.xpath("//a[contains(text(),'Do action')]");
    public static final By DO_ACTION_BUTTON = By.xpath("//input[@type='button'][@value='Do action']");
// DISABLED_DO_ACTION_LINK=xpath=//a[contains(text(),'Do action')][not(@href)]
// DISABLED_DO_ACTION_BUTTON=xpath=//input[@type='button'][@value='Do action'][@disabled='disabled']
    public static final By DISABLED_DO_ACTION_LINK = By.xpath("//tr[contains(td[1]/text(), 'Disabled')]/td[2]/a[contains(text(),'Do action')][string-length(@href)=0] ");
    public static final By DISABLED_DO_ACTION_BUTTON = By.xpath("//tr[contains(td[1]/text(), 'Disabled')]/td[3]/input[@value='Do action'][@type='button']");
    public static final By BEGIN_CONVERSATION_LINK = By.xpath("//a[contains(text(),'Begin conversation')]");
    public static final By END_CONVERSATION_BUTTON = By.xpath("//input[@type='button'][@value='End conversation']");
    public static final By ADD_PARAMETER_LINK = By.xpath("//a[contains(text(),'Add a page parameter')]");
    public static final By ADD_PARAMETER_BUTTON = By.xpath("//input[@type='button'][@value='Add a page parameter']");
    public static final By NAME_INPUT = By.xpath("//input[@type='text'][contains(@name,'name')][not(contains(@name,'nameVerification'))]");
    public static final By NAME_VERIFICATION_INPUT = By.xpath("//input[@type='text'][contains(@name,'nameVerification')]");
    public static final By CHECK_NAME_BUTTON = By.xpath("//input[@type='submit'][@value='Check name']");
    public static final By MINIMUM_AGE_INPUT = By.xpath("//input[@type='text'][contains(@name,'min')][not(contains(@name,'minVerification'))]");
    public static final By MAXIMUM_AGE_INPUT = By.xpath("//input[@type='text'][contains(@name,'minVerification')]");
    public static final By CHECK_AGES_BUTTON = By.xpath("//input[@type='submit'][@value='Check ages']");
    public static final By DATE_INPUT = By.xpath("//input[contains(@name,'date')][not(contains(@name,'dateVerification'))]");
    public static final By DATE_VERIFICATION_INPUT = By.xpath("//input[contains(@name,'dateVerification')]");
    public static final By CHECK_DATE_BUTTON = By.xpath("//input[@type='submit'][@value='Check date']");

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Before
    @Override
    public void beforeTest() throws MalformedURLException {
        super.beforeTest();
        open(contextPath + HOME_PAGE);
    }

    protected boolean isDownloadWorking(String pathToFile, String textToFind) {
        try {
            URL downloadUrl = new URL(contextPath + pathToFile);
            BufferedReader r = new BufferedReader(new InputStreamReader(downloadUrl.openStream()));
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = r.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString().contains(textToFind);
        } catch (IOException e) {
            return false;
        }
    }
}
