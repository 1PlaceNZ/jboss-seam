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
package org.jboss.seam.example.dvd.test.graphene;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;



/**
 * This class tests user authentication. Majority of other tests depends on
 * these methods.
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
@RunAsClient
@RunWith(Arquillian.class)
public class LoginTest extends GrapheneDvdTest {
   
    @Before
    @Override
    public void setUp() {
       // Do nothing
    }

    @Test
    public void basicLoginTest() {
        String username = "user1";
        String password = "password";
        assertTrue("Login failed.", login(username, password));
    }

    @Test
    public void invalidLoginTest() {
        String username = "nonExistingUser";
        String password = "invalidPassword";
        assertFalse("Logged in despite invalid credentials.", login(username,
                password));
    }

    @Test
    public void adminLoginTest() {
        String username = "manager";
        String password = "password";
        assertTrue("Login failed.", login(username, password));
        assertTrue("Navigation failed", browser.getCurrentUrl().contains(
                getProperty("ADMIN_URL")));
    }
}
