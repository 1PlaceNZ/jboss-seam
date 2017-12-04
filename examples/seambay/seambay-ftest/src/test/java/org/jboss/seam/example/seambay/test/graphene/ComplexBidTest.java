package org.jboss.seam.example.seambay.test.graphene;

import java.net.URL;
import java.text.MessageFormat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.seam.example.common.test.DeploymentResolver;
import static org.jboss.seam.example.common.test.SeamGrapheneTest.getProperty;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

// Until Graphene handles two browsers, this test will be a mess
@RunAsClient
@RunWith(Arquillian.class)
public class ComplexBidTest {

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }

    @ArquillianResource
    protected URL contextPath;

    protected String defaultLogin = "demo";

    protected String defaultPassword = "demo";

    private WebDriver browser;

    private WebDriver secondBrowser;

    private WebDriver theBrowser;

    @Before
    public void beforeTest() {
        browser = new FirefoxDriver();
        secondBrowser = new FirefoxDriver();
        theBrowser = browser;
        open(contextPath.toString());
    }
    
    @After
    public void afterTest() {
        browser.quit();
        secondBrowser.quit();
    }

    @Test
    public void complexBidTest() {
        String firstBidderName = "honestjoe";
        String secondBidderName = "bidTester";
        String title = "Nikon D80 Digital Camera";

        if (isLoggedIn()) {
            logout();
        }

        // register new user in first browser
        clickAndWaitHttp(getBy("REGISTRATION"));
        submitRegistrationForm(secondBidderName, "password", "password", "Slovakia");
        assertTrue("Creating new user failed.", isLoggedIn());

        // place a bid for a camera
        search(title);
        clickAndWaitHttp(getBy("SEARCH_RESULTS_FIRST_ROW_LINK"));
        placeBid("2000");

        theBrowser = secondBrowser;
        open(contextPath + getProperty("HOME_PAGE"));

        login(defaultLogin, defaultPassword);
        search(title);
        clickAndWaitHttp(getBy("SEARCH_RESULTS_FIRST_ROW_LINK"));
        for (int i = 1100; i < 2000; i += 200) {
            placeBid(String.valueOf(i));
            assertTrue("'You have been outbid' page expected.", isElementPresent(getBy("BID_OUTBID")));
        }
        placeBid("2200");
        assertFalse("Outbid unexpectedly", isElementPresent(getBy("BID_OUTBID")));
        assertEquals("High bidder not recognized.", firstBidderName, getText(getBy("BID_HIGH_BIDDER")));

        theBrowser = browser;
        placeBid("2100");
        assertTrue("'You have been outbid' page expected.", isElementPresent(getBy("BID_OUTBID")));
        placeBid("2500");
        assertEquals("High bidder not recognized.", secondBidderName, getText(getBy("BID_HIGH_BIDDER")));
    }

    public void placeBid(String price) {
        if (isElementPresent(getBy("ITEM_NEW_BID_FIELD"))) {
            type(getBy("ITEM_NEW_BID_FIELD"), price, true);
            clickAndWaitHttp(getBy("ITEM_NEW_BID_SUBMIT"));
        } else if (isElementPresent(getBy("BID_INCREASE_FIELD"))) {
            type(getBy("BID_INCREASE_FIELD"), price, true);
            clickAndWaitHttp(getBy("BID_INCREASE_SUBMIT"));
        } else {
            fail("Unable to place a bid.");
        }
        clickAndWaitHttp(getBy("BID_CONFIRM"));
    }

    public void clickAndWaitHttp(By by) {
        theBrowser.findElement(by).click();
        sleep(5000);
    }

    public boolean isLoggedIn() {
        return isElementPresent(getBy("LOGOUT"));
    }

    public void logout() {
        clickAndWaitHttp(getBy("LOGOUT"));
    }

    public boolean isElementPresent(By by) {
        try {
            return theBrowser.findElement(by).isDisplayed();
        } catch(NoSuchElementException ex) {
            return false;
        }
    }

    public void submitRegistrationForm(String username, String password, String verify, String location) {
        assertTrue("Registration page expected.", theBrowser.getCurrentUrl().contains(getProperty("REGISTRATION_URL")));
        type(getBy("REGISTRATION_USERNAME"), username, true);
        type(getBy("REGISTRATION_PASSWORD"), password, true);
        type(getBy("REGISTRATION_VERIFY"), verify, true);
        type(getBy("REGISTRATION_LOCATION"), location, true);
        clickAndWaitHttp(getBy("REGISTRATION_SUBMIT"));
    }

    public int search(String keyword) {
        type(getBy("SEARCH_FIELD"), keyword, true);
        clickAndWaitHttp(getBy("SEARCH_SUBMIT"));
        return getXpathCount(getBy("SEARCH_RESULTS_COUNT"));
    }

    public void open(String url) {
        theBrowser.navigate().to(url);
        sleep(1000);// the Navigation.to doesn't seem to block as well as it should
    }

    public void type(By by, CharSequence text, boolean clear) {
        WebElement elem = theBrowser.findElement(by);
        if (clear) {
            elem.clear();
        }
        elem.sendKeys(text);
    }

    public String getText(By by) {
        return theBrowser.findElement(by).getText();
    }

    public int getXpathCount(By by) {
        return theBrowser.findElements(by).size();
    }

    public void login(String username, String password) {
        if (!isLoggedIn()) {
            clickAndWaitHttp(getBy("LOGIN"));
            submitLoginForm(username, password);
        }
    }

    public void submitLoginForm(String username, String password) {
        type(getBy("LOGIN_USERNAME"), username, true);
        type(getBy("LOGIN_PASSWORD"), password, true);
        clickAndWaitHttp(getBy("LOGIN_SUBMIT"));
    }

    public By getBy(String seleniumLocatorProperty, Object... args) {
        String seleniumLocator = getProperty(seleniumLocatorProperty);
        if (seleniumLocator == null) {
            seleniumLocator = seleniumLocatorProperty;
        }
        String locator = seleniumLocator.substring(seleniumLocator.indexOf("=") + 1);
        if (args.length != 0) {
            locator = MessageFormat.format(locator, args);
        }
        if (seleniumLocator.startsWith("id")) {
            return By.id(locator);
        } else if (seleniumLocator.startsWith("xpath")) {
            return By.xpath(locator);
        } else if (seleniumLocator.startsWith("css")) {
            return By.cssSelector(locator);
        } else if (seleniumLocator.startsWith("link")) {
            return By.linkText(locator);
        } else {
            return null;
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}