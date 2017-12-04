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
import static org.jboss.arquillian.graphene.Graphene.*;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests user management in SeamSpace application
 *
 * @author Jozef Hartinger
 *
 */

@RunAsClient
@RunWith(Arquillian.class)
public class UserTest extends SeamSpaceFunctionalTestBase {

    @Override
    @Before
    public void beforeTest() {
        Assume.assumeTrue(isRealBrowser());// due to alerts
        super.beforeTest();
        clickAndWaitHttp(getBy("SECURITY"));
        clickAndWaitHttp(getBy("MANAGE_USERS"));
    }

    @Test
    @InSequence(1)
    public void userCreatingTest() {
        String username = "jharting";
        String password = "topSecret";
        String[] roles = {"admin", "user"};
        createNewUser("Jozef", "Hartinger", username, password, password, roles, true);
        String userRow = getProperty("USER_TABLE_ROW_BY_NAME", username);
        // check user list
        assertTrue("User not found in userlist.", isElementPresent(getBy(userRow)));
        assertTrue("User not in admin role.", getText(getBy(userRow + getProperty("USER_TABLE_ROLES"))).contains("admin"));
        assertTrue("User not in user role.", getText(getBy(userRow + getProperty("USER_TABLE_ROLES"))).contains("user"));
        assertTrue("User not enabled.", isElementPresent(getBy(userRow + getProperty("USER_TABLE_CHECKBOX_CHECKED"))));
        // check new user can login
        clickAndWaitHttp(getBy("LOGOUT"));
        login(username, password);
        assertTrue("Unable to login with new user's credentials.", isLoggedIn());
    }

    @Test
    @InSequence(2)
    public void userEditingTest() {
        String username = "shadowman";
        String password = "password";
        String[] roles = {"admin", "user"};
        String userRow = getProperty("USER_TABLE_ROW_BY_NAME", username);
        clickAndWaitHttp(getBy(userRow + getProperty("USER_TABLE_EDIT")));
        fillUpdatableUserDetails(password, password, roles, true);
        clickAndWaitHttp(getBy("USER_SAVE"));
        assertTrue("User not in admin role.", getText(getBy(userRow + getProperty("USER_TABLE_ROLES"))).contains("admin"));
        assertTrue("User not in user role.", getText(getBy(userRow + getProperty("USER_TABLE_ROLES"))).contains("user"));
        clickAndWaitHttp(getBy("LOGOUT"));
        login(username, password);
        assertTrue("Unable to login with changed password", isLoggedIn());
    }

    @Test
    @InSequence(3)
    public void userDeletingTest() throws InterruptedException {
        String username = "mona";
        String userRow = getProperty("USER_TABLE_ROW_BY_NAME", username);
        assertTrue("User " + username + " not in user list.", isElementPresent(getBy(userRow)));
        String windowHandle = browser.getWindowHandle();
        click(getBy(userRow + getProperty("USER_TABLE_DELETE")));
        browser.switchTo().alert().accept();
        browser.switchTo().window(windowHandle);
        waitModel().until().element(getBy(userRow)).is().not().present();
        assertFalse("User " + username + " exists after deletion", isElementPresent(getBy(userRow)));
    }

    @Test
    @InSequence(4)
    public void cancelledUserDeletingTest() throws InterruptedException {
        String username = "demo";
        String userRow = getProperty("USER_TABLE_ROW_BY_NAME", username);
        assertTrue("User " + username + " not in user list.", isElementPresent(getBy(userRow)));
        click(getBy(userRow + getProperty("USER_TABLE_DELETE")));
        browser.switchTo().alert().dismiss();
        browser.navigate().refresh();
        assertTrue("User " + username + " missing in user list after cancelled deletion.", isElementPresent(getBy(userRow)));
    }

    @Test
    @InSequence(5)
    public void disablingUserAccountTest() {
        String username = "johny";
        String password = "password";
        String userRow = getProperty("USER_TABLE_ROW_BY_NAME", username);
        createNewUser("John", "Doe", username, password, password, new String[]{"user"}, false);
        assertTrue("User not found in userlist.", isElementPresent(getBy(userRow)));
        assertTrue("User account enabled.", isElementPresent(getBy(userRow + getProperty("USER_TABLE_CHECKBOX_UNCHECKED"))));
        clickAndWaitHttp(getBy("LOGOUT"));
        login(username, password);
        assertFalse("User logged in despite his account was disabled.", isLoggedIn());
    }

    private void createNewUser(String firstName, String lastName, String username, String password, String confirm, String[] roles, boolean enabled) {
        clickAndWaitHttp(getBy("CREATE_USER_BUTTON"));
        fillNewUserDetails(firstName, lastName, username, password, confirm, roles, enabled);
        clickAndWaitHttp(getBy("USER_SAVE"));
    }

    private void fillNewUserDetails(String firstName, String lastName, String username, String password, String confirm, String[] roles, boolean enabled) {
        type(getBy("USER_FIRSTNAME"), firstName);
        type(getBy("USER_LASTNAME"), lastName);
        type(getBy("USER_NAME"), username);
        fillUpdatableUserDetails(password, confirm, roles, enabled);
    }

    private void fillUpdatableUserDetails(String password, String confirm, String[] roles, boolean enabled) {
        type(getBy("USER_PASSWORD"), password);
        type(getBy("USER_CONFIRM"), confirm);
        for (String role : roles) {
            assertTrue("Unable to add user to role: " + role, isElementPresent(getBy("USER_ROLE_BY_NAME_CHECKBOX", role)));
            check(getBy("USER_ROLE_BY_NAME_CHECKBOX", role));
        }
        if (enabled) {
            check(getBy("USER_ENABLED"));
        }
    }
}
