package org.jboss.seam.example.mail.test.graphene;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

@RunAsClient
@RunWith(Arquillian.class)
public class MailFunctionalTest extends SeamGrapheneTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    protected Wiser wiser;

    private Object[][] sendMethods = new Object[][]{
        {getBy("SEND_SIMPLE_BUTTON"), new String[]{"Content-Type: text/html; charset=", "Content-Disposition: inline", "<p>Dear " + getProperty("FIRSTNAME") + ",</p>"}},
        {getBy("SEND_PLAIN_BUTTON"), new String[]{"This is a plain text, email."}},
        {getBy("SEND_HTML_BUTTON"), new String[]{"Subject: Seam Mail", "Content-Type: multipart/mixed;", "Content-Type: multipart/alternative;", "Content-Type: text/plain; charset=", "This is the alternative text body for mail readers that don't support html", "Content-Type: text/html; charset=", "<p>This is an example <i>HTML</i> email sent by Seam.</p>"}},
        {getBy("SEND_ATTACHMENT_BUTTON"), new String[]{"Content-Type: multipart/mixed;", "Content-Type: application/octet-stream; name=jboss.jpg", "/9j/4AAQSkZJRgABA"/*jpeg start*/, "Content-Type: application/octet-stream; name=numbers.csv", "3,Three,treis,trois", "Content-Type: image/png; name=" + getProperty("FIRSTNAME") + "_" + getProperty("LASTNAME") + ".jpg", "iVBORw0KGgo" /*png start*/}},
        {getBy("SEND_ASYNCHRONOUS_BUTTON"), new String[]{"Content-Type: multipart/mixed;", "Content-Type: text/html; charset=", "Content-Disposition: inline", "<p>Dear " + getProperty("FIRSTNAME") + ",</p>"}},
        {getBy("SEND_TEMPLATE_BUTTON"), new String[]{"Subject: Templating with Seam Mail", "Content-Type: multipart/mixed;", "Content-Type: multipart/alternative;", "Content-Type: text/plain; charset=", "Sorry, your mail reader doesn't support html.", "Content-Type: text/html; charset=", "<p>Here's a dataTable</p><table>", "<td>Saturday</td>"}},
        {getBy("SEND_SERVLET_BUTTON"), new String[]{"Content-Type: multipart/mixed;", "Content-Disposition: inline", "Dear John Smith,", "This is a plain text, email."}}
    };

    /**
     * We restart SMTP after each Method, because Wiser doesn't have mechanism
     * to flush received e-mails.
     */
    @Before
    public void startSMTP() {
        wiser = new Wiser();
        wiser.setPort(3025);
        wiser.start();
    }

    @After
    public void stopSMTP() {
        wiser.stop();
    }

    /**
     * Place holder - just verifies that example deploys
     */
    @Test
    public void homePageLoadTest() {
        assertEquals("Unexpected page title.", getProperty("HOME_PAGE_TITLE"), browser.getTitle());
    }

    /**
     * Sends a mail and verifies it was delivered
     */
    public void mailTest(Object[] sendMethod) {
        // cycle sendMethods
        By buttonToClick = (By) sendMethod[0];
        String[] expectedMessageContents = (String[]) sendMethod[1];
        fillInInputs();
        sendEmail(buttonToClick);
        checkDelivered(expectedMessageContents);
    }

    @Test
    public void testSimple() {
        mailTest(sendMethods[0]);
    }

    @Test
    public void testPlain() {
        mailTest(sendMethods[1]);
    }

    @Test
    public void testHtml() {
        mailTest(sendMethods[2]);
    }

    @Test
    public void testAttachment() {
        mailTest(sendMethods[3]);
    }

    @Test
    public void testAsync() {
        Object[] sendMethod = sendMethods[4];
        By buttonToClick = (By) sendMethod[0];
        String[] expectedMessageContents = (String[]) sendMethod[1];
        fillInInputs();
        sendEmail(buttonToClick);
        // the async delay in MailExample.sendAsynchronous is 3000 ms, add 2000 ms as a buffer
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
        checkDelivered(expectedMessageContents);
    }

    @Test
    public void testTemplate() {
        mailTest(sendMethods[5]);
    }

    @Test
    public void testServlet() {
        Assume.assumeTrue(isRealBrowser());
        mailTest(sendMethods[6]);
    }

    /**
     * Fills in html text inputs.
     */
    private void fillInInputs() {
        type(getBy("FIRSTNAME_INPUT"), getProperty("FIRSTNAME"));
        type(getBy("LASTNAME_INPUT"), getProperty("LASTNAME"));
        type(getBy("ADDRESS_INPUT"), getProperty("ADDRESS"));
        type(getBy("SERVLET_NAME_INPUT"), getProperty("FIRSTNAME") + " " + getProperty("LASTNAME"));
        type(getBy("SERVLET_ADDRESS_INPUT"), getProperty("ADDRESS"));
    }

    /**
     * Sends an email by clicking on specified button. If the send method is
     * asynchronous, waits for the associated action to take place. It assures
     * that the email was sent by verifying appropriate message.
     *
     * @param buttonToClick
     */
    private void sendEmail(By buttonToClick) {
        clickAndWaitHttp(buttonToClick);
        if (buttonToClick.equals(getBy("SEND_ASYNCHRONOUS_BUTTON"))) {
            assertTrue(isTextInSource("Seam Email")); // asynchronous email send produces no message, so we just check that we didn't end up on a debug page
        } else {
            assertTrue("Expected message about successfuly sent mail. See also JBSEAM-3769.", isTextInSource("Email sent successfully"));
        }
    }

    /**
     * Checks that the expected email was delivered.
     *
     * @param expectedMessageContents
     */
    private void checkDelivered(String[] expectedMessageContents) {
        assertFalse("Expected a message", wiser.getMessages().isEmpty());
        WiserMessage message = wiser.getMessages().get(0); // although "send plain text" example sends 3 mails (To:, CC:, Bcc:) Wiser cannot distinquish between them so we just check the first mail.
        assertEquals(getProperty("ADDRESS"), message.getEnvelopeReceiver());
        assertTrue("Envelope sender (" + message.getEnvelopeSender() + ") doesn't match expected one (" + getProperty("ENVELOPE_SENDER") + ")", message.getEnvelopeSender().matches(getProperty("ENVELOPE_SENDER")));

        for (String expectedMessageContent : expectedMessageContents) {
            assertTrue("Didn't find expected text (" + expectedMessageContent + ") in the received email.", new String(message.getData()).contains(expectedMessageContent));
        }
    }
}
