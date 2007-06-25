package org.jboss.seam.exception;

import javax.faces.application.FacesMessage.Severity;

/**
 * Implements &lt;redirect/&gt; for pages.xml
 * 
 * @author Gavin King
 *
 */
public final class ConfigRedirectHandler extends RedirectHandler
{
   private final String id;
   private final Class clazz;
   private final boolean conversation;
   private final String message;
   private final Severity messageSeverity;

   public ConfigRedirectHandler(String id, Class clazz, boolean conversation, String message, Severity messageSeverity)
   {
      this.id = id;
      this.clazz = clazz;
      this.conversation = conversation;
      this.message = message;
      this.messageSeverity = messageSeverity;
   }

   @Override
   protected String getMessage(Exception e)
   {
      return message;
   }

   @Override
   protected String getViewId(Exception e)
   {
      return id;
   }

   @Override
   public boolean isHandler(Exception e)
   {
      return clazz.isInstance(e);
   }

   @Override
   protected boolean isEnd(Exception e)
   {
      return conversation;
   }

   @Override
   public Severity getMessageSeverity(Exception e)
   {
      return messageSeverity;
   }

}