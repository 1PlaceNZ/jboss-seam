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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunAsClient
@RunWith(Arquillian.class)
public class ContactCRUDTest extends ContactlistFunctionalTestBase {

    // used for creation test
    private Person john = new Person("John", "Doe", "0123456789", "0123456789",
            "Street", "City", "State", "01234", "US");
    private Person jane = new Person("Jane", "Doe", "0123456789", "0123456789",
            "Street", "City", "State", "01234", "US");
    // used for edit test
    private Person jozef = new Person("Jozef", "Hartinger", "0123456789",
            "0123456789", "Cervinkova 99", "Brno", "Czech Republic", "01234",
            "CZ");

    @Before
    @Override
    public void beforeTest() {
        open(contextPath + getProperty("START_PAGE"));
    }
    
    @Test
    public void testCreateContact() {
        clickAndWaitHttp(getBy("CREATE_CONTACT_PAGE"));

        fillCreateContactForm(john);
        clickAndWaitHttp(getBy("CREATE_CONTACT_SUBMIT"));

        clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

        search(john);
        assertTrue("Creating new contact failed.", searchResultPresent(john));
    }

    @Test
    public void testCreationCanceling() {
        clickAndWaitHttp(getBy("CREATE_CONTACT_PAGE"));

        fillCreateContactForm(jane);
        clickAndWaitHttp(getBy("CREATE_CONTACT_CANCEL"));
       
        clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

        type(getBy("SEARCH_FIRST_NAME_FIELD"), jane.getFirstName());
        type(getBy("SEARCH_LAST_NAME_FIELD"), jane.getLastName());
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));

        assertFalse("New contact created despite cancel.", 
                searchResultPresent(jane));
    }

    @Test
    public void testEditContact() {
        String firstName = "Shane";
        String lastName = "Bryzak";
        // find contact
        
        search(firstName, lastName);
        assertTrue("Contact not found. Application is in unexpected state.",
                searchResultPresent(firstName, lastName));
        clickAndWaitHttp(getBy("SEARCH_RESULT_FIRST_ROW_LINK"));

        clickAndWaitHttp(getBy("EDIT_CONTACT_LINK"));

        // update form fields
        fillCreateContactForm(jozef);
        clickAndWaitHttp(getBy("UPDATE_CONTACT_SUBMIT"));

        // make sure new values are present
        clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

        search(jozef);
        assertTrue("Contact update failed. New values missing", 
                searchResultPresent(jozef));
        // make sure old values are not present
        clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

        search(firstName, lastName);
        assertFalse("Contact update failed. Old values still present", 
                searchResultPresent(firstName, lastName));
    }

    @Test
    public void testRemoveContact() {
        String firstName = "Norman";
        String lastName = "Richards";
        // find contact
        search(firstName, lastName);
        assertTrue("Contact not found. Application is in unexpected state.",
                searchResultPresent(firstName, lastName));
        clickAndWaitHttp(getBy("SEARCH_RESULT_FIRST_ROW_LINK"));

        // remove contact
        clickAndWaitHttp(getBy("REMOVE_CONTACT_LINK"));

        // assert contact is removed
        clickAndWaitHttp(getBy("SEARCH_CONTACT_PAGE"));

        search(firstName, lastName);
        assertFalse("Contact present despite it should be removed.", 
                searchResultPresent(firstName, lastName));
    }
}
