package org.jboss.seam.example.rss.test.xml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.seam.example.common.test.DeploymentResolver;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RunAsClient
@RunWith(Arquillian.class)
public class SeamXMLRSSTest
{
   private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
   private static XPathFactory xpf = XPathFactory.newInstance();
   protected DocumentBuilder db;
   protected XPath xp;
   
   public static final String HOME_PAGE = "rss.seam";
   public static final String HOME_PAGE_TITLE = "Title Feed";
   public static final String TITLE_XPATH = "/feed/title";

   public static final String ATOM_NS_URI = "http://www.w3.org/2005/Atom";

   private Document doc;
   
   @ArquillianResource
   protected URL contextPath;
   
   @Deployment(testable = false)
   public static Archive<?> createDeployment() {
       return DeploymentResolver.createDeployment();
   }

   @Before
   public void setDocument() throws IOException, SAXException, ParserConfigurationException
   {
      dbf.setNamespaceAware(false);
      db = dbf.newDocumentBuilder();
      xp = xpf.newXPath();
      
      doc = db.parse(contextPath + HOME_PAGE);
   }

   /**
    * Verifies that example deploys and has title
    * 
    * @throws XPathExpressionException If XPath expression cannot be compiled or
    *            executed
    */
   @Test
   public void testRSSTitle() throws XPathExpressionException
   {
      NodeList list = (NodeList) xp.compile(TITLE_XPATH).evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
      assertEquals("There is only on title", 1, list.getLength());
      
      Element element = (Element)list.item(0);
      assertEquals("Document title should be '" + HOME_PAGE_TITLE + "'", HOME_PAGE_TITLE, element.getTextContent());
   }
}
