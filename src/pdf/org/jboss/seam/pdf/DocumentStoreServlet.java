package org.jboss.seam.pdf;

import java.io.IOException;

import javax.faces.event.PhaseId;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.ServletRequestSessionMap;
import org.jboss.seam.util.Parameters;

public class DocumentStoreServlet 
    extends HttpServlet 
{
    private static final long serialVersionUID = 5196002741557182072L;

    @Override
    protected void doGet(final HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, 
               IOException 
    {
        Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
        Lifecycle.setServletRequest(request);
        Lifecycle.beginRequest( getServletContext(), request );
        Manager.instance().restoreConversation(request.getParameterMap());
        Lifecycle.resumeConversation(request);
        Manager.instance().handleConversationPropagation(request.getParameterMap());
        try 
        {
           doWork(request, response);
           //TODO: conversation timeout
           Manager.instance().endRequest( new ServletRequestSessionMap(request) );
           Lifecycle.endRequest(request);
        } 
        catch (Exception e) 
        {
           Lifecycle.endRequest();           
           throw new ServletException(e);
        } 
        finally 
        {
           Lifecycle.setServletRequest(null);
           Lifecycle.setPhaseId(null);
        }
    
    }    
    
    
    private void doWork(HttpServletRequest request, HttpServletResponse response) 
        throws IOException 
    {
        String contentId = (String)
        Parameters.convertMultiValueRequestParameter(Parameters.getRequestParameters(),
                "docId",
                String.class);        
                
        DocumentStore store = DocumentStore.instance();
        
        if (store.idIsValid(contentId)) 
        {
            DocumentData documentData = store.getDocumentData(contentId);
            
            byte[] data = documentData.getData();       

            response.setContentType(documentData.getDocType().getMimeType());
            response.setHeader("Content-Disposition", 
                    "inline; filename=\"" + documentData.getFileName() + "\"");

            if (data != null) {
                response.getOutputStream().write(data);
            }
        } 
        else 
        {
             String error = store.getErrorPage();             
             if (error != null) 
             {      
                 if (error.startsWith("/")) 
                 {
                     error = request.getContextPath() + error;
                 }
                 response.sendRedirect(error);
             } 
             else 
             {
                 response.sendError(404);
             }
        }
    }
}
