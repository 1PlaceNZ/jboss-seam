/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

/**
 * Provides BaseSeamTest functionality for TestNG integration tests.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Mike Youngstrom
 * @author Pete Muir
 */
public class SeamTest extends BaseSeamTest
{
   
   @BeforeMethod
   @Override
   public void begin()
   {
      super.begin();
   }

   @AfterMethod
   @Override
   public void end()
   {
      super.end();
   }

   @Override
   @Deprecated
   public void init() throws Exception
   {
      super.init();
   }

   @Override
   @Deprecated
   public void cleanup() throws Exception
   {
      super.cleanup();
   }
   
   @Override
   @BeforeSuite
   public void startSeam() throws Exception
   {
      super.startSeam();
   }
   
   @Override
   @AfterSuite
   public void stopSeam() throws Exception
   {
      super.stopSeam();
   }
   
   @Override
   @BeforeClass
   public void setupClass() throws Exception
   {
      super.setupClass();
   }
   
   @Override
   @AfterClass
   protected void cleanupClass() throws Exception
   {
      super.cleanupClass();
   }

   /**
    * A pass through to BaseSeamTest.FacesRequest. 
    * 
    * Deprecated, use BaseSeamTest.FacesRequest instead
    */
   public class FacesRequest extends BaseSeamTest.FacesRequest
   {

      public FacesRequest()
      {
         super();
      }

      public FacesRequest(String viewId, String conversationId)
      {
         super(viewId, conversationId);
      }

      public FacesRequest(String viewId)
      {
         super(viewId);
      }

   }

   /**
    * A pass through to BaseSeamTest.NonFacesRequest.
    * 
    * Deprecated, use BaseSeamTest.NonFacesRequest instead
    */
   public class NonFacesRequest extends BaseSeamTest.NonFacesRequest
   {

      public NonFacesRequest()
      {
         super();
      }

      public NonFacesRequest(String viewId, String conversationId)
      {
         super(viewId, conversationId);
      }

      public NonFacesRequest(String viewId)
      {
         super(viewId);
      }

   }

   /**
    * A pass through to BaseSeamTest.Request.
    * 
    * Deprecated, use BaseSeamTest.Request instead
    */
   public abstract class Request extends BaseSeamTest.Request
   {

      public Request()
      {
         super();
      }

      public Request(String conversationId)
      {
         super(conversationId);
      }

   }

   /**
    * @deprecated Use BaseSeamTest.FacesRequest or BaseSeamTest.NonFacesRequest instead
    */
   public abstract class Script extends BaseSeamTest.Script
   {

      public Script()
      {
         super();
      }

      public Script(String conversationId)
      {
         super(conversationId);
      }
   }
}
