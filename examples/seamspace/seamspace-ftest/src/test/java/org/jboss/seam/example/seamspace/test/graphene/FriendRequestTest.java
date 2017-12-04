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
import org.jboss.arquillian.junit.Arquillian;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests "Send a friend request" at seamspace example. This option is
 * available when user opens somebody's profile.
 *
 * @author Martin Gencur
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class FriendRequestTest extends SeamSpaceFunctionalTestBase {

    @Test
    public void sendFriendRequestText() {
        clickAndWaitHttp(getBy("DUKE_IMAGE"));
        clickAndWaitHttp(getBy("FRIEND_REQUEST_LINK"));
        type(getBy("MESSAGE_AREA"), getProperty("MESSAGE_TEXT"));
        clickAndWaitHttp(getBy("REQUEST_SEND_BUTTON"));
        assertTrue("Friend request sent page expected", isTextInSource(getProperty("REQUEST_SENT_MESSAGE")));
    }
}
