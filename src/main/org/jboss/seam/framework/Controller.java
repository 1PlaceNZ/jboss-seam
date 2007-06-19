package org.jboss.seam.framework;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.ClassValidator;
import org.jboss.seam.Component;
import org.jboss.seam.Model;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.core.ServletSession;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.HttpError;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.faces.Validation;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transaction;

/**
 * Base class for controller objects. Provides various
 * helper methods that help slightly reduce the code
 * required to create a Seam component that acts as
 * a controller.
 * 
 * @author Gavin King
 *
 */
public abstract class Controller implements Serializable
{

   @Logger Log log;

   protected Context getApplicationContext()
   {
      return Contexts.getApplicationContext();
   }

   protected Context getBusinessProcessContext()
   {
      return Contexts.getBusinessProcessContext();
   }

   protected Context getConversationContext()
   {
      return Contexts.getConversationContext();
   }

   protected Context getEventContext()
   {
      return Contexts.getEventContext();
   }

   protected Events getEvents()
   {
      return Events.instance();
   }
   
   protected Conversation getConversation()
   {
      return Conversation.instance();
   }

   protected FacesMessages getFacesMessages()
   {
      return FacesMessages.instance();
   }
   
   protected Identity getIdentity()
   {
      return Identity.instance();
   }
   
   protected Cookie getCookie(String name)
   {
      return (Cookie) FacesContext.getCurrentInstance().getExternalContext()
                                  .getRequestCookieMap().get(name);
   }
   
   protected void addCookie(Cookie cookie)
   {
      ( (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                                          .getResponse() ).addCookie(cookie);
   }
   
   protected void addFacesMessage(String messageTemplate, Object... params)
   {
      getFacesMessages().add(messageTemplate, params);
   }
   
   protected void addFacesMessageFromResourceBundle(String key, Object... params)
   {
      getFacesMessages().addFromResourceBundle(key, params);
   }
   
   protected String render(String path)
   {
      return Renderer.instance().render(path);
   }

   protected void sendHttpError(int code)
   {
      HttpError.instance().send(code);
   }
   
   protected void sendHttpError(int code, String message)
   {
      HttpError.instance().send(code, message);
   }

   protected Log getLog()
   {
      return log;
   }

   protected Map<String, String> getMessages()
   {
      return Messages.instance();
   }

   protected Context getMethodContext()
   {
      return Contexts.getMethodContext();
   }

   protected Context getPageContext()
   {
      return Contexts.getPageContext();
   }

   protected Redirect getRedirect()
   {
      return Redirect.instance();
   }

   protected Context getSessionContext()
   {
      return Contexts.getSessionContext();
   }
   
   protected FacesContext getFacesContext()
   {
      return FacesContext.getCurrentInstance();
   }

   protected boolean validationSucceeded()
   {
      return Validation.instance().isSucceeded();
   }

   protected boolean validationFailed()
   {
      return Validation.instance().isFailed();
   }

   protected void failValidation()
   {
      Validation.instance().fail();
   }
   
   protected String interpolate(String string, Object... params) 
   {
      return Interpolator.instance().interpolate(string, params);
   }

   protected <T> ClassValidator<T> getValidator(Class<T> modelClass)
   {
      return Model.forClass(modelClass).getValidator();
   } 

   protected void debug(Object object, Object... params)
   {
      log.debug(object, params);
   }

   protected void debug(Object object, Throwable t, Object... params)
   {
      log.debug(object, t, params);
   }

   protected void error(Object object, Object... params)
   {
      log.error(object, params);
   }

   protected void error(Object object, Throwable t, Object... params)
   {
      log.error(object, t, params);
   }

   protected void fatal(Object object, Object... params)
   {
      log.fatal(object, params);
   }

   protected void fatal(Object object, Throwable t, Object... params)
   {
      log.fatal(object, t, params);
   }

   protected void info(Object object, Object... params)
   {
      log.info(object, params);
   }

   protected void info(Object object, Throwable t, Object... params)
   {
      log.info(object, t, params);
   }

   protected void trace(Object object, Object... params)
   {
      log.trace(object, params);
   }

   protected void trace(Object object, Throwable t, Object... params)
   {
      log.trace(object, t, params);
   }

   protected void warn(Object object, Object... params)
   {
      log.warn(object, params);
   }

   protected void warn(Object object, Throwable t, Object... params)
   {
      log.warn(object, t, params);
   }

   protected void raiseAsynchronousEvent(String type, Object... parameters)
   {
      getEvents().raiseAsynchronousEvent(type, parameters);
   }

   protected void raiseEvent(String type, Object... parameters)
   {
      getEvents().raiseEvent(type, parameters);
   }

   protected void raiseTransactionSuccessEvent(String type, Object... parameters)
   {
      getEvents().raiseTransactionSuccessEvent(type, parameters);
   }
   
   protected Object getComponentInstance(String name)
   {
      return Component.getInstance(name);
   }

   protected Object getComponentInstance(Class clazz)
   {
      return Component.getInstance(clazz);
   }
   
   protected void invalidateSession()
   {
      ServletSession.instance().invalidate();
   }

   protected boolean isTransactionMarkedRollback()
   {
      try
      {
         return Transaction.instance().isMarkedRollback();
      }
      catch (Exception e)
      {
         return false;
      }
   }

}
