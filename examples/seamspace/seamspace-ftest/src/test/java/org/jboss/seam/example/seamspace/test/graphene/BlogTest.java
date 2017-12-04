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
package org.jboss.seam.example.seamspace.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests blog functionality of SeamSpace application
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BlogTest extends SeamSpaceFunctionalTestBase {
    
    
    
    @Test
    public void createBlogTest() {
        String title = "What is Seam?";
        String text = "Seam is a powerful open source development platform for building rich Internet applications in Java. Seam integrates technologies such as Asynchronous JavaScript and XML (AJAX), JavaServer Faces (JSF), Java Persistence (JPA), Enterprise Java Beans (EJB 3.0) and Business Process Management (BPM) into a unified full-stack solution, complete with sophisticated tooling.";
        int blogCount = 0;

        clickAndWaitHttp(getBy("VIEW_BLOG_ENTRY"));
        blogCount = getXpathCount(getBy("BLOG_ENTRY_COUNT"));
        browser.navigate().back();
        clickAndWaitHttp(getBy("CREATE_BLOG_ENTRY"));
        type(getBy("NEW_BLOG_TITLE"), title);
        type(getBy("NEW_BLOG_TEXT"), text);
        clickAndWaitHttp(getBy("NEW_BLOG_SUBMIT"));
        assertEquals("Unexpected number of blog entries.", ++blogCount, getXpathCount(getBy("BLOG_ENTRY_COUNT")));
        String blogEntry = getProperty("BLOG_ENTRY_BY_TITLE", title);
        assertTrue("Blog entry not found. " + blogEntry, isElementPresent(getBy(blogEntry)));
        assertEquals("Blog entry text has been modified.", text, getText(getBy(blogEntry + getProperty("BLOG_ENTRY_TEXT"))));
    }
}
