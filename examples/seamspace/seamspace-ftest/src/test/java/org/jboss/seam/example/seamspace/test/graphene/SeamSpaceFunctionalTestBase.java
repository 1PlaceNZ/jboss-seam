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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Jozef Hartinger
 *
 */
public abstract class SeamSpaceFunctionalTestBase extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @Override
    @Before
    public void beforeTest() {
        open(contextPath + getProperty("HOME_URL"));
        if (!isLoggedIn()) {
            login();
        }
    }

    @After
    public void tearDown() {
        if (isLoggedIn()) {
            clickAndWaitHttp(getBy("LOGOUT"));
        }
    }

    public void login() {
        login(getProperty("DEFAULT_USERNAME"), getProperty("DEFAULT_PASSWORD"));
    }

    public void login(String username, String password) {
        clickAndWaitHttp(getBy("LOGIN"));
        type(getBy("LOGIN_USERNAME"), username);
        type(getBy("LOGIN_PASSWORD"), password);
        clickAndWaitHttp(getBy("LOGIN_LOGIN"));
    }

    protected boolean isLoggedIn() {
        return !isElementPresent(getBy("LOGIN")) && isElementPresent(getBy("LOGOUT"));
    }
}
