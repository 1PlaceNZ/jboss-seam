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
package org.jboss.seam.example.spring.test.graphene;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.booking.graphene.ChangePasswordTest;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests change password functionality.
 *
 * @author jbalunas
 * @author jharting
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class SpringChangePasswordTest extends ChangePasswordTest {

    /**
     * Verifies that changing password works well. If clean-up part of this
     * method fails it may affect other methods.
     */
    @Test
    @Override
    public void changePasswordTest() {
        String newPassword = "password";
        changePassword(newPassword, PASSWORD);
        assertTrue("Password change failed.", isTextOnPage(getProperty("PASSWORD_UPDATED_MESSAGE")));
        logout();
        assertTrue("Login failed.", login(USERNAME, newPassword));
        // cleanup - set default password
        changePassword(PASSWORD, newPassword);
        assertTrue("Password change failed.", isTextOnPage(getProperty("PASSWORD_UPDATED_MESSAGE")));
        logout();
        assertTrue("Login failed.", login(USERNAME, PASSWORD));
    }

    @Override
    public void usingDifferentPasswordsTest() {
    }

    @Test
    public void usingIncorrectOldPasswordTest() {
        changePassword("password", "foobar1");
        assertTrue("Password verification failed", isTextOnPage(getProperty("PASSWORD_REENTER_MESSAGE")));
    }

    @Override
    public void changePassword(String newPassword, String oldpassword) {
        clickAndWaitHttp(getBy("SETTINGS"));
        type(getBy("PASSWORD_PASSWORD"), newPassword);
        type(getBy("PASSWORD_VERIFY"), oldpassword);
        clickAndWaitHttp(getBy("PASSWORD_SUBMIT"));
    }
}
