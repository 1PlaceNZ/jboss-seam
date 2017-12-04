//$Id: MessageListTest.java 2383 2006-10-26 18:53:00Z gavin $
package org.jboss.seam.example.messages.test;
import javax.faces.model.DataModel;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MessageListTest extends JUnitSeamTest
{
	@Deployment(name="MessageListTest")
	@OverProtocol("Servlet 3.0") 
	public static Archive<?> createDeployment()
	{

      return Deployments.messagesDeployment();
   }
	
   @Test
   public void testMessageList() throws Exception 
   {
      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(1);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.select}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            assert getValue("#{message.title}").equals("Hello World");
            assert getValue("#{message.read}").equals(true);
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(0);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.delete}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

   }
   
}
