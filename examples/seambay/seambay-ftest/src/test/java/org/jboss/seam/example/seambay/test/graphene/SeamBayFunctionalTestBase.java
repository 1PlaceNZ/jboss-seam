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
package org.jboss.seam.example.seambay.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;

/**
 * Main class for SeamBay example tests
 *
 * @author Jozef Hartinger
 *
 */
public class SeamBayFunctionalTestBase extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }
    protected String defaultLogin = "demo";
    protected String defaultPassword = "demo";

    public void login(String username, String password) {
        if (!isLoggedIn()) {
            clickAndWaitHttp(getBy("LOGIN"));
            submitLoginForm(username, password);
        }
    }

    public void login() {
        login(defaultLogin, defaultPassword);
    }

    public boolean isLoggedIn() {
        return isElementPresent(getBy("LOGOUT"));
    }

    public void logout() {
        clickAndWaitHttp(getBy("LOGOUT"));
    }

    public void submitRegistrationForm(String username, String password, String verify, String location) {
        assertTrue("Registration page expected.", browser.getCurrentUrl().contains(getProperty("REGISTRATION_URL")));
        type(getBy("REGISTRATION_USERNAME"), username);
        type(getBy("REGISTRATION_PASSWORD"), password);
        type(getBy("REGISTRATION_VERIFY"), verify);
        type(getBy("REGISTRATION_LOCATION"), location);
        clickAndWaitHttp(getBy("REGISTRATION_SUBMIT"));
    }

    public void submitLoginForm(String username, String password) {
        type(getBy("LOGIN_USERNAME"), username);
        type(getBy("LOGIN_PASSWORD"), password);
        clickAndWaitHttp(getBy("LOGIN_SUBMIT"));
    }

    public int search(String keyword) {
        type(getBy("SEARCH_FIELD"), keyword);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        return getXpathCount(getBy("SEARCH_RESULTS_COUNT"));
    }
    
    public void placeBid(String price) {
        if (isElementPresent(getBy("ITEM_NEW_BID_FIELD"))) {
            type(getBy("ITEM_NEW_BID_FIELD"), price);
            clickAndWaitHttp(getBy("ITEM_NEW_BID_SUBMIT"));
        } else if (isElementPresent(getBy("BID_INCREASE_FIELD"))) {
            type(getBy("BID_INCREASE_FIELD"), price);
            clickAndWaitHttp(getBy("BID_INCREASE_SUBMIT"));
        } else {
            fail("Unable to place a bid.");
        }
        clickAndWaitHttp(getBy("BID_CONFIRM"));
    }

}
