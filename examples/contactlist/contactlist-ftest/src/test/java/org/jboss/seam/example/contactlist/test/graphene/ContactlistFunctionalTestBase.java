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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;

public class ContactlistFunctionalTestBase extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
    }

    public boolean searchResultPresent(String firstName, String lastName) {
        return isElementPresent(getBy("SEARCH_RESULT_FIRST_ROW_LINK"))
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_LINK")).equals(firstName + " " + lastName);
    }

    public boolean searchResultPresent(Person person) {
        return searchResultPresent(person.getFirstName(), person.getLastName())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_CELL_PHONE")).equals(person.getCellPhone())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_HOME_PHONE")).equals(person.getHomePhone())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_ADDRESS")).equals(person.getAddress())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_CITY")).equals(person.getCity())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_STATE")).equals(person.getState())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_ZIP")).equals(person.getZip())
                && getText(getBy("SEARCH_RESULT_FIRST_ROW_COUNTRY")).equals(person.getCountry());
    }

    public void fillCreateContactForm(Person person) {
        type(getBy("FIRST_NAME_FIELD"), person.getFirstName());
        type(getBy("LAST_NAME_FIELD"), person.getLastName());
        type(getBy("CELL_PHONE_FIELD"), person.getCellPhone());
        type(getBy("HOME_PHONE_FIELD"), person.getHomePhone());
        type(getBy("ADDRESS_FIELD"), person.getAddress());
        type(getBy("CITY_FIELD"), person.getCity());
        type(getBy("STATE_FIELD"), person.getState());
        type(getBy("ZIP_FIELD"), person.getZip());
        type(getBy("COUNTRY_FIELD"), person.getCountry());
    }

    public void search(String firstName, String lastName) {
        type(getBy("SEARCH_FIRST_NAME_FIELD"), firstName);
        type(getBy("SEARCH_LAST_NAME_FIELD"), lastName);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));

    }

    public void search(Person person) {
        search(person.getFirstName(), person.getLastName());
    }
}
