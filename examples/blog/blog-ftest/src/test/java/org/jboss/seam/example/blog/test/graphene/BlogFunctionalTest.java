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
package org.jboss.seam.example.blog.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Blog functional test
 *
 * @author Jozef Hartinger
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BlogFunctionalTest extends SeamGrapheneTest {

    protected String password = "tokyo";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Test
    @InSequence(1)
    public void simpleEntryTest() {

        String id = "simpleBlogEntry";
        String title = "Simple blog entry";
        String excerpt = "This is an excerpt";
        String body = "This is a simple blog entry posted for testing purposes.";

        enterNewEntry(id, title, excerpt, body);

        open(contextPath + "entry/" + id);

        assertFalse("Entry not found.", isElementPresent(getBy("ENTRY_404")));
        assertEquals("Unexpected entry title found.", title, getText(getBy("ENTRY_TITLE")));
        assertEquals("Unexpected entry body found.", body, getText(getBy("ENTRY_BODY")));
    }

    @Test
    @InSequence(2)
    public void simpleEntrySearchTest() {

        String id = "searchTestEntry";
        String title = "Search Test Entry";
        String excerpt = "";
        String searchString = "9e107d9d372bb6826bd81d3542a419d6";
        String body = "This is a simple blog entry used for testing search functionality. " + searchString;

        enterNewEntry(id, title, excerpt, body);

        type(getBy("SEARCH_FIELD"), searchString);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        assertEquals("Unexpected search result.", 1, getXpathCount(getBy("SEARCH_RESULT")));

    }

    private void enterNewEntry(String id, String title, String excerpt, String body) {
        clickAndWaitHttp(getBy("NEW_POST"));

        if (browser.getCurrentUrl().contains(getProperty("LOGIN_URL"))) {
            login();
        }
        fillNewEntryForm(id, title, excerpt, body);
    }

    private void fillNewEntryForm(String id, String title, String excerpt, String body) {
        type(getBy("NEW_ENTRY_ID"), id);
        type(getBy("NEW_ENTRY_TITLE"), title);
        type(getBy("NEW_ENTRY_EXCERPT"), excerpt);
        type(getBy("NEW_ENTRY_BODY"), body);
        clickAndWaitHttp(getBy("NEW_ENTRY_SUBMIT"));
    }

    private void login() {
        type(getBy("LOGIN_PASSWORD"), password);
        clickAndWaitHttp(getBy("LOGIN_SUBMIT"));
    }
}
