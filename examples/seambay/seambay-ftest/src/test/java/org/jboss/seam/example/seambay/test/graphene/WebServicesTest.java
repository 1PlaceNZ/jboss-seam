package org.jboss.seam.example.seambay.test.graphene;

import com.google.common.base.Predicate;
import java.net.MalformedURLException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.graphene.wait.WebDriverWait;
import org.jboss.arquillian.graphene.wait.WebDriverWaitImpl;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.seam.example.common.test.SeamGrapheneTest;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;

/**
 * Tests Web Services of Seambay example. Uses WebDriver to test, browser is
 * chosen according to parameter passed
 *
 * @author kpiwko
 *
 */
@RunAsClient
@RunWith(Arquillian.class)
public class WebServicesTest extends SeamGrapheneTest {

    protected static final int WAIT_TIME = 1000;
    protected static final int WAIT_TIMEOUT = 10;//seconds
    protected static final int JS_TIMEOUT = 500;//ms
    protected static final By HERE_LINK = By.partialLinkText("here");
    protected static final String SERVICE_PAGE_URL = "test.seam";
    protected static final By INVOKE_SERVICE_BUTTON = By.xpath("//button[contains(@onclick,'sendRequest')]");
    protected static final By REQUEST_AREA = By.id("serviceRequest");
    protected static final By RESPONSE_AREA = By.id("serviceResponse");
    protected static final By SELECTED_SERVICE = By.id("selectedService");
    protected static final String LOGIN_LINK_TEXT = "Login";
    protected static final String LIST_CATEGORIES_LINK_TEXT = "List Categories";
    protected static final String CREATE_NEW_AUCTION_LINK_TEXT = "Create new auction";
    protected static final String UPDATE_AUCTION_DETAILS_LINK_TEXT = "Update auction details";
    protected static final String SET_AUCTION_DURATION_LINK_TEXT = "Set auction duration";
    protected static final String SET_STARTING_PRICE_LINK_TEXT = "Set starting price";
    protected static final String GET_AUCTION_DETAILS_LINK_TEXT = "Get the auction details";
    protected static final String CONFIRM_AUCTION_LINK_TEXT = "Confirm auction";
    protected static final String FIND_AUCTIONS_LINK_TEXT = "Find Auctions";
    protected static final String LOGOUT_LINK_TEXT = "Logout";

    /* login parameters */
    protected static final By LOGIN_INPUT_USERNAME = By.id("username");
    protected static final By LOGIN_INPUT_PASSWORD = By.id("password");

    /* create new auction parameters */
    protected static final By AUCTION_TITLE = By.id("title");
    protected static final By AUCTION_DESCRIPTION = By.id("description");
    protected static final By AUCTION_CATEGORY_ID = By.id("categoryId");

    /* parameters for other tests */
    protected static final By SEARCH_TERM = By.id("searchTerm");
    protected static final By AUCTION_DURATION = By.id("duration");
    protected static final By STARTING_PRICE = By.id("price");

    /* responses */
    protected static final String LIST_CATEGORIES_RESPONSE = "<ns2:listCategoriesResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"><return><categoryId>1</categoryId><name>Antiques</name></return><return><categoryId>2</categoryId><name>Art</name></return><return><categoryId>3</categoryId><name>Books</name></return><return><categoryId>4</categoryId><name>Cameras and Photography</name></return><return><categoryId>5</categoryId><name>Cars and Boats</name></return><return><categoryId>6</categoryId><name>Cell Phones</name></return><return><categoryId>7</categoryId><name>Clothing and Shoes</name></return><return><categoryId>8</categoryId><name>Computers</name></return><return><categoryId>9</categoryId><name>Music</name></return><return><categoryId>10</categoryId><name>Electronics</name></return><return><categoryId>11</categoryId><name>Home and Garden</name></return><return><categoryId>12</categoryId><name>Musical Instruments</name></return><return><categoryId>13</categoryId><name>Sporting goods</name></return><return><categoryId>14</categoryId><name>Toys</name></return><return><categoryId>15</categoryId><name>Video Games</name></return><return><categoryId>1001</categoryId><name>Furniture</name><parent><categoryId>1</categoryId><name>Antiques</name></parent></return><return><categoryId>1002</categoryId><name>Silverware</name><parent><categoryId>1</categoryId><name>Antiques</name></parent></return><return><categoryId>2001</categoryId><name>Paintings</name><parent><categoryId>2</categoryId><name>Art</name></parent></return><return><categoryId>2002</categoryId><name>Prints</name><parent><categoryId>2</categoryId><name>Art</name></parent></return><return><categoryId>2003</categoryId><name>Sculptures</name><parent><categoryId>2</categoryId><name>Art</name></parent></return><return><categoryId>3001</categoryId><name>Fiction</name><parent><categoryId>3</categoryId><name>Books</name></parent></return><return><categoryId>3002</categoryId><name>Non Fiction</name><parent><categoryId>3</categoryId><name>Books</name></parent></return><return><categoryId>3003</categoryId><name>Comic Books</name><parent><categoryId>3</categoryId><name>Books</name></parent></return><return><categoryId>3004</categoryId><name>Children</name><parent><categoryId>3</categoryId><name>Books</name></parent></return><return><categoryId>4001</categoryId><name>Digital Cameras</name><parent><categoryId>4</categoryId><name>Cameras and Photography</name></parent></return><return><categoryId>4002</categoryId><name>Memory Cards</name><parent><categoryId>4</categoryId><name>Cameras and Photography</name></parent></return><return><categoryId>4003</categoryId><name>Film Cameras</name><parent><categoryId>4</categoryId><name>Cameras and Photography</name></parent></return><return><categoryId>4004</categoryId><name>Video Cameras</name><parent><categoryId>4</categoryId><name>Cameras and Photography</name></parent></return><return><categoryId>5001</categoryId><name>Cars</name><parent><categoryId>5</categoryId><name>Cars and Boats</name></parent></return><return><categoryId>5002</categoryId><name>Motorcycles</name><parent><categoryId>5</categoryId><name>Cars and Boats</name></parent></return><return><categoryId>5003</categoryId><name>Car Parts</name><parent><categoryId>5</categoryId><name>Cars and Boats</name></parent></return><return><categoryId>6001</categoryId><name>Mobile Phones</name><parent><categoryId>6</categoryId><name>Cell Phones</name></parent></return><return><categoryId>6002</categoryId><name>Mobile Accessories</name><parent><categoryId>6</categoryId><name>Cell Phones</name></parent></return><return><categoryId>6003</categoryId><name>Prepaid cards</name><parent><categoryId>6</categoryId><name>Cell Phones</name></parent></return><return><categoryId>7001</categoryId><name>Women</name><parent><categoryId>7</categoryId><name>Clothing and Shoes</name></parent></return><return><categoryId>7002</categoryId><name>Men</name><parent><categoryId>7</categoryId><name>Clothing and Shoes</name></parent></return><return><categoryId>7003</categoryId><name>Girls</name><parent><categoryId>7</categoryId><name>Clothing and Shoes</name></parent></return><return><categoryId>7004</categoryId><name>Boys</name><parent><categoryId>7</categoryId><name>Clothing and Shoes</name></parent></return><return><categoryId>7005</categoryId><name>Babies</name><parent><categoryId>7</categoryId><name>Clothing and Shoes</name></parent></return><return><categoryId>8001</categoryId><name>Notebooks</name><parent><categoryId>8</categoryId><name>Computers</name></parent></return><return><categoryId>8002</categoryId><name>Desktop PCs</name><parent><categoryId>8</categoryId><name>Computers</name></parent></return><return><categoryId>8003</categoryId><name>Servers</name><parent><categoryId>8</categoryId><name>Computers</name></parent></return><return><categoryId>8004</categoryId><name>Hardware</name><parent><categoryId>8</categoryId><name>Computers</name></parent></return><return><categoryId>8005</categoryId><name>Software</name><parent><categoryId>8</categoryId><name>Computers</name></parent></return><return><categoryId>9001</categoryId><name>CDs</name><parent><categoryId>9</categoryId><name>Music</name></parent></return><return><categoryId>9002</categoryId><name>Records</name><parent><categoryId>9</categoryId><name>Music</name></parent></return><return><categoryId>10001</categoryId><name>Home Audio</name><parent><categoryId>10</categoryId><name>Electronics</name></parent></return><return><categoryId>10002</categoryId><name>MP3 Players</name><parent><categoryId>10</categoryId><name>Electronics</name></parent></return><return><categoryId>10003</categoryId><name>Television</name><parent><categoryId>10</categoryId><name>Electronics</name></parent></return><return><categoryId>10004</categoryId><name>Home theatre</name><parent><categoryId>10</categoryId><name>Electronics</name></parent></return><return><categoryId>11001</categoryId><name>Kitchen</name><parent><categoryId>11</categoryId><name>Home and Garden</name></parent></return><return><categoryId>11002</categoryId><name>Real Estate</name><parent><categoryId>11</categoryId><name>Home and Garden</name></parent></return><return><categoryId>11003</categoryId><name>Furniture</name><parent><categoryId>11</categoryId><name>Home and Garden</name></parent></return><return><categoryId>12001</categoryId><name>Guitars</name><parent><categoryId>12</categoryId><name>Musical Instruments</name></parent></return><return><categoryId>12002</categoryId><name>Pianos and Keyboards</name><parent><categoryId>12</categoryId><name>Musical Instruments</name></parent></return><return><categoryId>12003</categoryId><name>Percussion</name><parent><categoryId>12</categoryId><name>Musical Instruments</name></parent></return><return><categoryId>12004</categoryId><name>Orchestral</name><parent><categoryId>12</categoryId><name>Musical Instruments</name></parent></return><return><categoryId>13001</categoryId><name>Golf</name><parent><categoryId>13</categoryId><name>Sporting goods</name></parent></return><return><categoryId>13002</categoryId><name>Fishing</name><parent><categoryId>13</categoryId><name>Sporting goods</name></parent></return><return><categoryId>13003</categoryId><name>Tennis</name><parent><categoryId>13</categoryId><name>Sporting goods</name></parent></return><return><categoryId>14001</categoryId><name>Remote control</name><parent><categoryId>14</categoryId><name>Toys</name></parent></return><return><categoryId>14002</categoryId><name>Cars and trucks</name><parent><categoryId>14</categoryId><name>Toys</name></parent></return><return><categoryId>14003</categoryId><name>Dolls</name><parent><categoryId>14</categoryId><name>Toys</name></parent></return><return><categoryId>14004</categoryId><name>Educational</name><parent><categoryId>14</categoryId><name>Toys</name></parent></return><return><categoryId>15001</categoryId><name>PC</name><parent><categoryId>15</categoryId><name>Video Games</name></parent></return><return><categoryId>15002</categoryId><name>Nintendo Wii</name><parent><categoryId>15</categoryId><name>Video Games</name></parent></return><return><categoryId>15003</categoryId><name>Sony Playstation 3</name><parent><categoryId>15</categoryId><name>Video Games</name></parent></return><return><categoryId>15004</categoryId><name>XBox 360</name><parent><categoryId>15</categoryId><name>Video Games</name></parent></return></ns2:listCategoriesResponse>";
    protected static final String LOGIN_RIGHT_RESPONSE = "<return>true</return>";
    protected static final String CREATE_NEW_AUCTION_RESPONSE_JBOSS_56 = "<ns2:createAuctionResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String CREATE_NEW_AUCTION_RESPONSE_JBOSS_4 = "<createAuctionResponse xmlns=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String FIND_AUCTIONS_RESPONSE = "<description>You can buy an animal here</description>";
    protected static final String UPDATE_AUCTION_RESPONSE_JBOSS_56 = "<ns2:updateAuctionDetailsResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String UPDATE_AUCTION_RESPONSE_JBOSS_4 = "<updateAuctionDetailsResponse xmlns=\"http://seambay.example.seam.jboss.org";
    protected static final String SET_DURATION_RESPONSE_JBOSS_56 = "<ns2:setAuctionDurationResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String SET_DURATION_RESPONSE_JBOSS_4 = "<setAuctionDurationResponse xmlns=\"http://seambay.example.seam.jboss.org/";
    protected static final String SET_STARTING_PRICE_RESPONSE_JBOSS_56 = "<ns2:setAuctionPriceResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String SET_STARTING_PRICE_RESPONSE_JBOSS_4 = "<setAuctionPriceResponse xmlns=\"http://seambay.example.seam.jboss.org/";
    protected static final String AUCTION_DETAILS_PRICE_RESPONSE = "<ns2:getNewAuctionDetailsResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"><return><account><accountId>-1</accountId><feedbackPercent>0.0</feedbackPercent><feedbackScore>0</feedbackScore><location>Sydney, NSW, Australia</location>";
    protected static final String LOGOUT_RESPONSE = "<ns2:logoutResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"><return>true</return></ns2:logoutResponse>";
    protected static final String CONFIRMATION_RESPONSE_JBOSS_56 = "<ns2:confirmAuctionResponse xmlns:ns2=\"http://seambay.example.seam.jboss.org/\"";
    protected static final String CONFIRMATION_RESPONSE_JBOSS_4 = "<confirmAuctionResponse xmlns=\"http://seambay.example.seam.jboss.org/\"";

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return DeploymentResolver.createDeployment();
    }
    
    @Before
    public void beforeTest() throws MalformedURLException {
        // it seems HTMLUnit doesn't play well with the JS
        Assume.assumeTrue(isRealBrowser());
    }

    @Test
    @InSequence(1)
    public void testGoToWSPage() {
        open(contextPath.toString());
        clickAndWaitHttp(HERE_LINK);
        assertTrue("URL contains web services page", browser.getCurrentUrl().contains(SERVICE_PAGE_URL));
    }

    @Test
    @InSequence(2)
    public void testLoginService() {
        loginService();

        // the same as Selenium, must use getValue to retrieve text inside
        // <textarea></textarea>
        waitCustom().withMessage("Response area should contain \"true\"").until().element(RESPONSE_AREA).attribute("value").contains(LOGIN_RIGHT_RESPONSE);
    }

    @Test
    @InSequence(3)
    public void listCategoriesTest() {
        loginService();
        clickAndWaitForPage(LIST_CATEGORIES_LINK_TEXT);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain a list of categories.").until().element(RESPONSE_AREA).attribute("value").contains(LIST_CATEGORIES_RESPONSE);
    }

    @Test
    @InSequence(4)
    public void testCreateNewAuction() {
        loginService();
        createNewAuctionService();

        waitCustom().withMessage("Response area should contain information about creating the auction.").until(new Predicate() {
            public boolean apply(Object t) {
                return browser.findElement(RESPONSE_AREA).getAttribute("value").contains(CREATE_NEW_AUCTION_RESPONSE_JBOSS_56)
                        || browser.findElement(RESPONSE_AREA).getAttribute("value").contains(CREATE_NEW_AUCTION_RESPONSE_JBOSS_4);
            }
        });
    }

    @Test
    @InSequence(5)
    public void findAuctionsTest() {
        String searchTerm = "Animals";

        loginService();
        createNewAuctionService();
        confirmAuctionService();
        clickAndWaitForPage(FIND_AUCTIONS_LINK_TEXT);
        type(SEARCH_TERM, searchTerm);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain information about finding auction.").until().element(RESPONSE_AREA).attribute("value").contains(FIND_AUCTIONS_RESPONSE);
    }

    @Test
    @InSequence(6)
    public void updateAuctionTest() {
        String title = "Animals";
        String description = "Another description";
        String categoryId = "5";
        loginService();
        createNewAuctionService();

        clickAndWaitForPage(UPDATE_AUCTION_DETAILS_LINK_TEXT);
        type(AUCTION_TITLE, title);
        type(AUCTION_DESCRIPTION, description);
        type(AUCTION_CATEGORY_ID, categoryId);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain information about updating the auction.").until(new Predicate() {
            public boolean apply(Object t) {
                return browser.findElement(RESPONSE_AREA).getAttribute("value").contains(UPDATE_AUCTION_RESPONSE_JBOSS_56)
                        || browser.findElement(RESPONSE_AREA).getAttribute("value").contains(UPDATE_AUCTION_RESPONSE_JBOSS_4);
            }
        });
    }

    @Test
    @InSequence(7)
    public void setAuctionDurationTest() {
        String duration = "20";
        loginService();
        createNewAuctionService();

        clickAndWaitForPage(SET_AUCTION_DURATION_LINK_TEXT);
        type(AUCTION_DURATION, duration);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain information about setting duration.").until(new Predicate() {
            public boolean apply(Object t) {
                return browser.findElement(RESPONSE_AREA).getAttribute("value").contains(SET_DURATION_RESPONSE_JBOSS_56)
                        || browser.findElement(RESPONSE_AREA).getAttribute("value").contains(SET_DURATION_RESPONSE_JBOSS_4);
            }
        });

    }

    @Test
    @InSequence(8)
    public void setStartingPriceTest() {
        String price = "1000";
        loginService();
        createNewAuctionService();

        clickAndWaitForPage(SET_STARTING_PRICE_LINK_TEXT);
        type(STARTING_PRICE, price);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain information about setting starting price.").until(new Predicate() {
            public boolean apply(Object t) {
                return browser.findElement(RESPONSE_AREA).getAttribute("value").contains(SET_STARTING_PRICE_RESPONSE_JBOSS_56)
                        || browser.findElement(RESPONSE_AREA).getAttribute("value").contains(SET_STARTING_PRICE_RESPONSE_JBOSS_4);
            }
        });
    }

    @Test
    @InSequence(9)
    public void getAuctionDetailsTest() {
        loginService();
        createNewAuctionService();

        clickAndWaitForPage(GET_AUCTION_DETAILS_LINK_TEXT);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitUntilValueNotEmpty(RESPONSE_AREA);
        waitForCidUpdate();
        waitCustom().withMessage("Response area should contain auction details.").until().element(RESPONSE_AREA).attribute("value").contains(AUCTION_DETAILS_PRICE_RESPONSE);
    }

    @Test
    @InSequence(10)
    public void logOutTest() {
        loginService();
        clickAndWaitForPage(LOGOUT_LINK_TEXT);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);

        waitCustom().withMessage("Response area should contain logout confirmation.").until().element(RESPONSE_AREA).attribute("value").contains(LOGOUT_RESPONSE);
    }

    @Test
    @InSequence(11)
    public void confirmAuctionTest() {
        loginService();
        createNewAuctionService();
        confirmAuctionService();
        waitCustom().withMessage("Response area should contain information about confirmation.").until(new Predicate() {
            public boolean apply(Object t) {
                return browser.findElement(RESPONSE_AREA).getAttribute("value").contains(CONFIRMATION_RESPONSE_JBOSS_56)
                        || browser.findElement(RESPONSE_AREA).getAttribute("value").contains(CONFIRMATION_RESPONSE_JBOSS_4);
            }
        });
    }

    /**
     * Goes to web services page and logs user in
     */
    protected void loginService() {
        String username = "demo";
        String password = "demo";

        open(contextPath.toString() + getProperty("HOME_PAGE"));
        waitCustom().until().element(HERE_LINK).is().present();
        clickAndWaitHttp(HERE_LINK);

        clickAndWaitForPage(LOGIN_LINK_TEXT);

        type(LOGIN_INPUT_USERNAME, username);
        type(LOGIN_INPUT_PASSWORD, password);

        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
        waitCustom().until().element(RESPONSE_AREA).attribute("value").contains(LOGIN_RIGHT_RESPONSE);
    }

    protected void confirmAuctionService() {
        clickAndWaitForPage(CONFIRM_AUCTION_LINK_TEXT);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
    }

    protected void createNewAuctionService() {
        String title = "Animals";
        String description = "You can buy an animal here";
        String categoryId = "6";
        clickAndWaitForPage(CREATE_NEW_AUCTION_LINK_TEXT);
        type(AUCTION_TITLE, title);
        type(AUCTION_DESCRIPTION, description);
        type(AUCTION_CATEGORY_ID, categoryId);
        clickAndWaitAjax(INVOKE_SERVICE_BUTTON);
        waitForCidUpdate();
    }

    protected void clickAndWaitForPage(String linkText) {
        waitCustom().until().element(By.partialLinkText(linkText)).is().present();
        click(By.partialLinkText(linkText));
        waitCustom().until().element(SELECTED_SERVICE).text().contains(linkText);
    }

    protected void waitForCidUpdate() {
        waitUntilValueNotEmpty(RESPONSE_AREA);
        waitUntilValueNotEmpty(By.id("conversationId"));
        try {
            Thread.sleep(JS_TIMEOUT);
        } catch (InterruptedException ex) {
        }
    }

    public WebDriverWait waitCustom() {
        return new WebDriverWaitImpl<Void>(null, browser, WAIT_TIMEOUT);
    }

    protected void waitUntilValueNotEmpty(final By by) {
        waitCustom().until(new Predicate() {
            public boolean apply(Object t) {
                return !browser.findElement(by).getAttribute("value").isEmpty();
            }
        });
    }
}
