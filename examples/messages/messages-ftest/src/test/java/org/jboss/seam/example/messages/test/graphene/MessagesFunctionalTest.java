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
package org.jboss.seam.example.messages.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for messages example
 *
 * @author Jozef Hartinger
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class MessagesFunctionalTest extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }
    private Object[][] messages = {
        {0, "Greetings Earthling", "This is another example of a message."},
        {1, "Hello World", "This is an example of a message."}};

    @Test
    @InSequence(1)
    public void readMessageTest() {
        for (Object[] message : messages) {
            int i = (Integer) message[0];
            String title = (String) message[1];
            String text = (String) message[2];

            clickAndWaitHttp(getBy("MESSAGES_LINK", i));
            assertEquals("Unexpected message title displayed.", title, getText(getBy("MESSAGE_TITLE")));
            assertEquals("Unexpected message text displayed.", text, getText(getBy("MESSAGE_TEXT")));
            assertTrue("Checkbox should be checked after message is read.", browser.findElement(getBy("MESSAGES_CHECKBOX", i)).isSelected());
        }
    }

    @Test
    @InSequence(2)
    public void deleteMessageTest() {
        for (Object[] message : messages) {
            int i = (Integer) message[0];
            String title = (String) message[1];
            String text = (String) message[2];

            int messageCount = getXpathCount(getBy("MESSAGES_COUNT"));
            // delete first message in a table
            clickAndWaitHttp(getBy("MESSAGES_DELETE", 0));
            assertEquals("Unexpected count of messages.", --messageCount, getXpathCount(getBy("MESSAGES_COUNT")));
            assertFalse("Message title still present.", isTextInSource(title));
        }
    }
}
