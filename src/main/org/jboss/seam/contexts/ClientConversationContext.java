/*
 * JBoss, Home of Professional Open Source �
 * 
 * Distributable under LGPL license. 
�* See terms of license at gnu.org. �
 */
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Manager;

/**
 * A conversation context is a logical context that lasts longer than 
 * a request but shorter than a login session
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 */
public class ClientConversationContext implements Context {

   private Map<String, Object> map;
   
   public ClientConversationContext()
   {
      map = (Map<String, Object>) getAttributeMap().remove( ScopeType.CONVERSATION.getPrefix() );
      if (map==null) map = new HashMap<String, Object>();
   }

   public ScopeType getType()
   {
      return ScopeType.CONVERSATION;
   }
   
	public Object get(String name) 
   {
      return map.get(name);
	}

	public void set(String name, Object value) 
   {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preSetVariable." + name);
		map.put(name, value);
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postSetVariable." + name);
	}

	public boolean isSet(String name) 
   {
		return get(name)!=null;
	}
   
	public void remove(String name) 
   {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preRemoveVariable." + name);
      map.remove(name);
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postRemoveVariable." + name);
	}

   public String[] getNames() {
      return map.keySet().toArray( new String[]{} );
   }
   
   @Override
   public String toString()
   {
      return "ClientConversationContext";
   }

   public Object get(Class clazz)
   {
      return get( Component.getComponentName(clazz) );
   }

   /**
    * Put the context variables in the faces view root.
    */
   public void flush()
   {
      if ( Manager.instance().isLongRunningConversation() )
      {
         getAttributeMap().put( ScopeType.CONVERSATION.getPrefix(), map );
      }
      else
      {
         getAttributeMap().remove( ScopeType.CONVERSATION.getPrefix() );
      }
   }

   private Map getAttributeMap()
   {
      return FacesContext.getCurrentInstance().getViewRoot().getAttributes();
   }

}
