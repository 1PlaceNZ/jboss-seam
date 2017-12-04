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
package org.jboss.seam.example.contactlist.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunAsClient
@RunWith(Arquillian.class)
public class CommentTest extends ContactlistFunctionalTestBase {

	@Test
	public void testComment() {
		String firstName = "Gavin";
		String lastName = "King";
		String message = "founder of the Hibernate open source object/relational mapping project";
		// find contact
		open(contextPath + getProperty("START_PAGE"));
        
		search(firstName, lastName);
		assertTrue("Contact not found. Application is in unexpected state.",
		searchResultPresent(firstName, lastName));
		clickAndWaitHttp(getBy("SEARCH_RESULT_FIRST_ROW_LINK"));

		// submit comment
		type(getBy("COMMENT_TEXTAREA"), message);
		clickAndWaitHttp(getBy("COMMENT_SUBMIT"));

		// assert comment is stored
		clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

		search(firstName, lastName);
		clickAndWaitHttp(getBy("SEARCH_RESULT_FIRST_ROW_LINK"));

		assertTrue("Comment is not stored.", isTextInSource(message));
	}
}
