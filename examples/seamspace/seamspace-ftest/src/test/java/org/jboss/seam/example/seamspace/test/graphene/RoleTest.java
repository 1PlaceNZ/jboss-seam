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
import org.openqa.selenium.Alert;

/**
 * This class tests role management in SeamSpace application
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class RoleTest extends SeamSpaceFunctionalTestBase {

    @Override
    @Before
    public void beforeTest() {
        Assume.assumeTrue(isRealBrowser());// due to alerts
        super.beforeTest();
        clickAndWaitHttp(getBy("SECURITY"));
        clickAndWaitHttp(getBy("MANAGE_ROLES"));
    }

    @Test
    @InSequence(1)
    public void roleCreatingTest() {
        String roleName = "tester";
        String[] roles = {"user", "admin"};
        createNewRole(roleName, roles);
        // check that new role is added to role list
        String roleRow = getProperty("ROLE_TABLE_ROW_BY_NAME", roleName);
        assertTrue("New role not found in role list.", isElementPresent(getBy(roleRow)));
        String roleTableRoles = getText(getBy(roleRow + getProperty("ROLE_TABLE_ROLES")));
        assertTrue("New role is not member of " + roles[0] + " role.", roleTableRoles.contains(roles[0]));
        assertTrue("New role is not member of " + roles[1] + " role.", roleTableRoles.contains(roles[1]));
        // check that new role is available to users
        clickAndWaitHttp(getBy("SECURITY"));
        clickAndWaitHttp(getBy("MANAGE_USERS"));
        clickAndWaitHttp(getBy("CREATE_USER_BUTTON"));
        assertTrue("New role is not available when creating new user.", isElementPresent(getBy("USER_ROLE_BY_NAME_CHECKBOX", roleName)));
    }

    @Test
    @InSequence(2)
    public void roleEditingTest() {
        String oldRoleName = "QA";
        String[] oldRoles = {"user"};
        String newRoleName = "QE";
        String[] newRoles = {"user", "admin"};

        createNewRole(oldRoleName, oldRoles);
        String oldRoleRow = getProperty("ROLE_TABLE_ROW_BY_NAME", oldRoleName);
        assertTrue("New role not found.", isElementPresent(getBy(oldRoleRow)));
        assertFalse("New role should not be member of admin role.", getText(getBy(oldRoleRow + getProperty("ROLE_TABLE_ROLES"))).contains("admin"));
        clickAndWaitHttp(getBy(oldRoleRow + getProperty("ROLE_TABLE_EDIT")));
        fillRoleDetails(newRoleName, newRoles);
        clickAndWaitHttp(getBy("ROLE_SAVE"));
        String newRoleRow = getProperty("ROLE_TABLE_ROW_BY_NAME", newRoleName);
        assertFalse("Old role still present.", isElementPresent(getBy(oldRoleRow)));
        assertTrue("Updated role not found in role table.", isElementPresent(getBy(newRoleRow)));
        String updatedRoleTableRoles = getText(getBy(newRoleRow + getProperty("ROLE_TABLE_ROLES")));
        assertTrue("New role is not member of " + newRoles[0] + " role.", updatedRoleTableRoles.contains(newRoles[0]));
        assertTrue("New role is not member of " + newRoles[1] + " role.", updatedRoleTableRoles.contains(newRoles[1]));
    }

    @Test
    @InSequence(3)
    public void roleDeletingTest() {
        String roleName = "commiter";
        String[] roles = {"user"};

        createNewRole(roleName, roles);
        String roleRow = getProperty("ROLE_TABLE_ROW_BY_NAME", roleName);
        assertTrue("New role not found.", isElementPresent(getBy(roleRow)));
        click(getBy(roleRow + getProperty("ROLE_TABLE_DELETE")));
        sleep(2000);
        Alert confirm = browser.switchTo().alert();
        assertTrue("Expected role deletion confirmation.", confirm.getText().contains(getProperty("ROLE_TABLE_DELETE_CONFIRMATION")));
        confirm.accept();
        sleep(2000);
        waitModel().until().element(getBy(roleRow)).is().not().present();
        assertFalse("Removed role still present.", isElementPresent(getBy(roleRow)));
    }

    public void createNewRole(String name, String[] roles) {
        clickAndWaitHttp(getBy("CREATE_ROLE_BUTTON"));
        fillRoleDetails(name, roles);
        clickAndWaitHttp(getBy("ROLE_SAVE"));
    }

    public void fillRoleDetails(String name, String[] roles) {
        type(getBy("ROLE_NAME"), name);
        for (String role : roles) {
            assertTrue("Role not available: " + role, isElementPresent(getBy("ROLE_MEMBER_OF_BY_NAME_CHECKBOX", role)));
            check(getBy("ROLE_MEMBER_OF_BY_NAME_CHECKBOX", role));
        }
    }
}
