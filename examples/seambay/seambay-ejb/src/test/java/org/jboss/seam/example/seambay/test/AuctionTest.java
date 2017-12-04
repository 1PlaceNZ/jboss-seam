package org.jboss.seam.example.seambay.test;

import java.io.File;
import java.util.List;

import javax.faces.model.DataModel;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.seambay.Auction;
import org.jboss.seam.example.seambay.Category;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class AuctionTest extends JUnitSeamTest
{
   @Deployment(name="AuctionTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
                .resolve("org.jboss.seam:jboss-seam")
                .withTransitivity().asFile();

      WebArchive war = ShrinkWrap.create(WebArchive.class, "seam-seambay.war")
                .addPackages(true, "org.jboss.seam.example.seambay")

                .addAsWebInfResource("org/jboss/seam/example/seambay/soap-handlers.xml", "classes/org/jboss/seam/example/seambay/soap-handlers.xml")

                // already in EJB module
                .addAsResource("import.sql")
                .addAsResource("seam.properties")

                .addAsWebInfResource("META-INF/ejb-jar.xml", "ejb-jar.xml")
                .addAsWebInfResource("META-INF/security-rules.drl", "security-rules.drl")

                // Test specific persistence.xml
                .addAsResource("persistence.xml", "META-INF/persistence.xml")

                // manually copied from Web module
                .addAsWebInfResource("pages.xml")

                // manually copied from Web module, modified
                .addAsWebInfResource("web.xml") // only contains MockSeamListener definition
                .addAsWebInfResource("components-test.xml", "components.xml") // corrected ejb component jndi-name references from java:app/jboss-seam to java:app/seam-blog

                // manually copied from EAR module
                .addAsWebInfResource("jboss-deployment-structure.xml")
                .addAsLibraries(libs);

      return war;
   }
   
   @Test
   public void testCreateAuction() throws Exception
   {
      new FacesRequest() 
      {        
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);            
         }
      }.run();  
      
      String cid = new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
         }
         
         @Override
         protected void renderResponse()
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            assert auction != null;
         }
      }.run();
            
      new FacesRequest("/sell.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.title}", "A Widget");
         }
      }.run();
      
      
      new FacesRequest("/sell2.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            List<Category> categories = (List<Category>) getValue("#{allCategories}");
            
            setValue("#{auctionAction.auction.category}", categories.get(0));
         }
      }.run();      
      
      new FacesRequest("/sell3.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.duration}", 3);
            setValue("#{auctionAction.auction.startingPrice}", 100.0);
         }
         
      }.run();
      
      new FacesRequest("/sell5.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.description}", "foo");
         }         
      }.run();      
      
      new FacesRequest("/preview.xhtml", cid)
      {
         @Override 
         protected void invokeApplication() throws Exception
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            invokeAction("#{auctionAction.confirm}");
            assert auction.getStatus() == Auction.STATUS_LIVE;
         }         
      }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{identity.logout}");
            assert getValue("#{identity.loggedIn}").equals(false);
         }         
      }.run();
   }
 
   @Test
   public void testBidding() throws Exception
   {
      new FacesRequest() 
      {        
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);
         }
      }.run();
            
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
            setValue("#{auctionAction.auction.title}", "BidTestZZZ");
            setValue("#{auctionAction.auction.startingPrice}", 1);         
            setValue("#{auctionAction.auction.description}", "bar");
            setValue("#{auctionAction.categoryId}", 1001);
            
            Auction auction = (Auction) getValue("#{auctionAction.auction}"); 

            assert auction.getStatus() == Auction.STATUS_UNLISTED;
            
            invokeAction("#{auctionAction.confirm}");
            
            assert auction.getStatus() == Auction.STATUS_LIVE;            
            assert auction.getHighBid() == null;
         }
      }.run();      
      
      new FacesRequest()
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionSearch.searchTerm}", "BidTestZZZ");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            assert auctions.getRowCount() == 1;
            Auction auction = ((Auction) auctions.getRowData()); 
            assert auction.getTitle().equals("BidTestZZZ");
            assert auction.getHighBid() == null;
         }
         
      }.run();
         
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getEventContext().set("auction", getValue("#{auctionSearch.auctions[0]}"));
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("required");
            Contexts.getEventContext().set("bidAmount", "5.00");
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("confirm");
            
            assert invokeAction("#{bidAction.confirmBid}").equals("success");
         }
      }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            Auction auction = ((Auction) auctions.getRowData());
            assert auction.getHighBid() != null;
         }
      }.run();
      
      
   }
   
}
