//$Id: DroolsNumberGuessTest.java 6415 2007-10-07 22:27:57Z pmuir $
package org.jboss.seam.example.numberguess.test;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.numberguess.Game;
import org.jboss.seam.example.numberguess.Guess;
import org.jboss.seam.example.numberguess.RandomNumber;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.pageflow.Pageflow;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DroolsNumberGuessTest extends JUnitSeamTest
{
   
   private int guessedValue;
   
   
   @Deployment(name="DroolsNumberGuessTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
              .importCompileAndRuntimeDependencies()
              // force resolve jboss-seam, because it is provided-scoped in the pom, but we need it bundled in the WAR
              .resolve("org.jboss.seam:jboss-seam").withTransitivity().asFile();

      return ShrinkWrap.create(WebArchive.class, "seam-drools.war")
              .addPackage(Game.class.getPackage())
              .addAsWebInfResource("components-test.xml", "components.xml")
              .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
              .addAsWebInfResource("seam.properties", "classes/seam.properties")
              .addAsWebInfResource("web.xml", "web.xml")
              .addAsWebInfResource("pageflow.jpdl.xml", "classes/pageflow.jpdl.xml")
              .addAsWebInfResource("numberguess.drl", "classes/numberguess.drl")
              .addAsWebInfResource("numberguessforflow.drl", "classes/numberguessforflow.drl")
              .addAsWebInfResource("numberguessforflow.xls", "classes/numberguessforflow.xls")
              .addAsWebInfResource("numberguess.rf", "classes/numberguess.rf")
              .addAsWebInfResource("numberguess.xls", "classes/numberguess.xls")
              .addAsLibraries(libs);
   }
   
   @Test
   public void testNumberGuessWin() throws Exception
   {
      String id = new NonFacesRequest("/numberGuess.xhtml")
      {

         @Override
         protected void renderResponse() throws Exception {
            Game game = (Game) getInstance(Game.class);
            Guess guess = (Guess) getInstance(Guess.class);
            assert game.getBiggest()==100;
            assert game.getSmallest()==1;
            assert guess.getValue()==null;
            assert game.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
            .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      String id2 = new FacesRequest("/numberGuess.xhtml", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            Guess guess = (Guess) getInstance(Guess.class); 
            guessedValue = getRandomNumber() > 50 ? 25 : 75;
            guess.setValue(guessedValue);
         }

         @Override
         protected void invokeApplication() throws Exception {
            setOutcome("guess");
            //ng.guess();
         }
         
         @Override
         protected void afterRequest() {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/numberGuess.xhtml");
         }
         
      }.run();
      
      assert id2.equals(id);
      
      new NonFacesRequest("/numberGuess.xhtml", id)
      {
         
         @Override
         protected void renderResponse() throws Exception {
            Game game = (Game) getInstance(Game.class);
            Guess guess = (Guess) getInstance(Guess.class);
            assert ( guessedValue > getRandomNumber() && game.getBiggest()==guessedValue-1 ) 
                  || ( guessedValue < getRandomNumber() && game.getSmallest()==guessedValue+1 );
            assert guess.getValue().equals(guessedValue);
            assert game.getGuessCount()==1;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      id2 = new FacesRequest("/numberGuess.xhtml", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            Guess guess = (Guess) getInstance(Guess.class);
            guessedValue = getRandomNumber();
            guess.setValue(guessedValue);
         }

         @Override
         protected void invokeApplication() throws Exception {
             Guess guess = (Guess) getInstance(Guess.class);
            setOutcome("guess");
            assert guess.getValue().equals(getRandomNumber());
            assert Pageflow.instance().getProcessInstance().getRootToken()
            .getNode().getName().equals("displayGuess");
            //ng.guess();
         }
         
         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/win.xhtml");
         }
         
      }.run();
      
      assert id2.equals(id);
      
      new NonFacesRequest("/win.xhtml", id)
      {
         @Override
         protected void renderResponse() throws Exception {
            Game game = (Game) getInstance(Game.class);
            Guess guess = (Guess) getInstance(Guess.class);
            assert guess.getValue().equals(getRandomNumber());
            assert game.getGuessCount()==2;
            assert !Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("win");
         }
         
      }.run();

   }
   
   @Test
   public void testNumberGuessLose() throws Exception
   {
      String id = new NonFacesRequest("/numberGuess.xhtml")
      {

         @Override
         protected void renderResponse() throws Exception {
            Game game = (Game) getInstance(Game.class);
            Guess guess = (Guess) getInstance(Guess.class);
            assert game.getBiggest()==100;
            assert game.getSmallest()==1;
            assert guess.getValue()==null;
            assert game.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      for (int i=1; i<=9; i++)
      {
         
         final int count = i;

         new FacesRequest("/numberGuess.xhtml", id)
         {
   
            @Override
            protected void applyRequestValues() throws Exception {
               Guess guess = (Guess) getInstance(Guess.class);
               guessedValue = getRandomNumber() > 50 ? 25+count : 75-count;
               guess.setValue(guessedValue);
            }
   
            @Override
            protected void invokeApplication() throws Exception {
               setOutcome("guess");
               //ng.guess();
               //assert Pageflow.instance().getProcessInstance().getRootToken()
//                     .getNode().getName().equals("displayGuess");
            }
            
            @Override
            protected void afterRequest()
            {
               assert !isRenderResponseBegun();
               assert getViewId().equals("/numberGuess.xhtml");
            }
            
         }.run();
         
         new NonFacesRequest("/numberGuess.xhtml", id)
         {
   
            @Override
            protected void renderResponse() throws Exception {
               Game game = (Game) getInstance(Game.class);
               Guess guess = (Guess) getInstance(Guess.class);
               assert ( guessedValue > getRandomNumber() && game.getBiggest()==guessedValue-1 ) 
                     || ( guessedValue < getRandomNumber() && game.getSmallest()==guessedValue+1 );
               assert guess.getValue().equals(guessedValue);
               assert game.getGuessCount()==count;
               assert Manager.instance().isLongRunningConversation();
               assert Pageflow.instance().getProcessInstance().getRootToken()
                     .getNode().getName().equals("displayGuess");
            }
            
         }.run();
      
      }

      new FacesRequest("/numberGuess.xhtml", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            Guess guess = (Guess) getInstance(Guess.class);
            guessedValue = getRandomNumber() > 50 ? 49 : 51;
            guess.setValue(guessedValue);
         }

         @Override
         protected void invokeApplication() throws Exception {
            setOutcome("guess");
            //ng.guess();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }

         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/lose.xhtml");
         }
         
      }.run();

      new NonFacesRequest("/lose.xhtml", id)
      {

         @Override
         protected void renderResponse() throws Exception {
            Guess guess = (Guess) getInstance(Guess.class);
            Game game = (Game) getInstance(Game.class);
            assert ( guessedValue > getRandomNumber() && game.getBiggest()==guessedValue-1 ) 
                  || ( guessedValue < getRandomNumber() && game.getSmallest()==guessedValue+1 );
            assert guess.getValue().equals(guessedValue);
            assert game.getGuessCount()==10;
            assert !Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("lose");
         }
         
      }.run();

   }
   
   private Integer getRandomNumber()
   {
       return (Integer) getInstance(RandomNumber.class);
   }
   
}
