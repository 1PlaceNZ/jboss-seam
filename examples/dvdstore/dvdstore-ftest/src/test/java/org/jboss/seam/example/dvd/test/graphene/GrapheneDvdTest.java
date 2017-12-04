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

import static org.junit.Assert.fail;
import static org.jboss.arquillian.graphene.Graphene.waitGui;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;


/**
 * 
 * @author jbalunas
 * @author jharting
 * 
 */
public abstract class GrapheneDvdTest extends SeamGrapheneTest {

    protected final String DEFAULT_USERNAME = "user1";
    protected final String DEFAULT_PASSWORD = "password";
    
    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Before
    public void setUp() {
        login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public boolean login(String username, String password) {
        open(contextPath + getProperty("HOME_PAGE"));
        waitGui(browser).until().element(getBy("LOGIN_SUBMIT")).is().present();
        if (isLoggedIn()) {
            fail("User already logged in.");
        }
        
        type(getBy("LOGIN_USERNAME"), username, true);
        type(getBy("LOGIN_PASSWORD"), password, true);
        clickAndWaitHttp(getBy("LOGIN_SUBMIT"));
        return isLoggedIn();
    }

    @After
    public void tearDown() {
        logout();
    }

    public void logout() {
        if (isLoggedIn()) {
            clickAndWaitHttp(getBy("LOGOUT"));
        }
    }

    public boolean isLoggedIn() {
        return isElementPresent(getBy("LOGOUT"));
    }

    
}
