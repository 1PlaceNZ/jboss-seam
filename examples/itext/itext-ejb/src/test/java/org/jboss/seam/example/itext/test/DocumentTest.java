package org.jboss.seam.example.itext.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.document.ByteArrayDocumentData;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.document.DocumentStore;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.pdf.ui.UIDocument;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This is just a placeholder until I can find a way to actually test the UI
 * components.
 */
@RunWith(Arquillian.class)
public class DocumentTest extends JUnitSeamTest
{
   @Deployment
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      return Deployments.itextDeployment();
   }

   @Test
   public void documentStore() throws Exception
   {
      String conversationId = new FacesRequest("/whyseam.xhtml")
      {

         @Override
         protected void invokeApplication() throws Exception
         {
            Conversation.instance().begin();

            DocumentStore store = (DocumentStore) getValue("#{org.jboss.seam.document.documentStore}");
            String docId = store.newId();

            Contexts.getSessionContext().set("docId", docId);

            DocumentData documentData = new ByteArrayDocumentData("base", UIDocument.PDF, new byte[100]);
            store.saveData(docId, documentData);
         }

         @Override
         protected void renderResponse() throws Exception
         {
            String docId = (String) getValue("#{docId}");
            assert docId != null;

            DocumentStore store = (DocumentStore) getValue("#{org.jboss.seam.document.documentStore}");
            assert store.idIsValid(docId);

         }
      }.run();

      // different conversation
      new FacesRequest("/whyseam.xhtml")
      {
         @Override
         protected void renderResponse() throws Exception
         {
            String docId = (String) getValue("#{docId}");
            assert docId != null;

            DocumentStore store = (DocumentStore) getValue("#{org.jboss.seam.document.documentStore}");
            assert !store.idIsValid(docId);
         }
      }.run();

      new FacesRequest("/whyseam.xhtml", conversationId)
      {
         @Override
         protected void renderResponse() throws Exception
         {
            String docId = (String) getValue("#{docId}");
            assert docId != null;

            DocumentStore store = (DocumentStore) getValue("#{org.jboss.seam.document.documentStore}");
            assert store.idIsValid(docId);

            ByteArrayDocumentData data = (ByteArrayDocumentData) store.getDocumentData(docId);
            assert data.getDocumentType().equals(UIDocument.PDF);
            assert data.getData().length == 100;
         }
      }.run();
   }

}
