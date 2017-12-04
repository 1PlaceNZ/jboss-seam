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

import java.io.IOException;
import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

/**
 * This class tests functionality of UI example
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class UIHTMLUnitTest extends UIFunctionalTestBase  {
    
    public static final By FILE_UPLOAD_FILE = By.xpath("//input[@type='file']");
    public static final By FILE_UPLOAD_UPDATE = By.xpath("//input[@type='submit'][@value='Update']");
    public static final By FILE_UPLOAD_RESPONSE = By.xpath("//ul/li[contains(text(),'Successfully updated')]");
    public static final By FILE_UPLOAD_LINK = By.xpath("//a[contains(@href,'fileUpload')]");
    public static final String IMAGE_TO_UPLOAD = "photo.jpg";
    public static final By IMAGE = By.xpath("//img");
    public static final By GRAPHIC_IMAGE_LINK = By.xpath("//a[contains(@href,'graphicImage')]");
    
    @Before
    public void beforeTest() throws MalformedURLException {
        Assume.assumeTrue(!isRealBrowser());// need htmlunit for the upload
        browser.navigate().to(contextPath + HOME_PAGE);
    }
    
    @Test
    @InSequence(1)
    public void fileUploadTest() throws IOException {

        Graphene.guardHttp(browser.findElement(FILE_UPLOAD_LINK)).click();
        ((JavascriptExecutor) browser).executeScript("document.getElementById(arguments[0]).value = arguments[1]", browser.findElement(FILE_UPLOAD_FILE).getAttribute("id"), IMAGE_TO_UPLOAD);

        Graphene.guardHttp(browser.findElement(FILE_UPLOAD_UPDATE)).click();
        assertTrue("Page should contain \"Successfully updated\"", browser.findElement(FILE_UPLOAD_RESPONSE).isDisplayed());
    }

    @Test
    @InSequence(2)
    public void graphicImageTest() throws IOException {
        Graphene.guardHttp(browser.findElement(GRAPHIC_IMAGE_LINK)).click();
        assertTrue("Page should contain image of Pete Muir", browser.findElement(IMAGE).isDisplayed());
    }
}
