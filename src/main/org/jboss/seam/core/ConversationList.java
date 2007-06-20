package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.web.Session;

/**
 * @author Gavin King
 */
@Scope(ScopeType.PAGE)
@Name("org.jboss.seam.core.conversationList")
@Install(precedence=BUILT_IN)
@BypassInterceptors
public class ConversationList implements Serializable 
{
   
   private static final long serialVersionUID = -1515889862229134356L;
   private List<ConversationEntry> conversationEntryList;
   
   @Create
   public void createConversationEntryList()
   {
      ConversationEntries conversationEntries = ConversationEntries.getInstance();
      if (conversationEntries==null)
      {
         conversationEntryList = Collections.EMPTY_LIST;
      }
      else
      {
         Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
         orderedEntries.addAll( conversationEntries.getConversationEntries() );
         conversationEntryList = new ArrayList<ConversationEntry>( conversationEntries.size() );
         for ( ConversationEntry entry: orderedEntries )
         {
            if ( entry.isDisplayable() && !Session.instance().isInvalid() )
            {
               conversationEntryList.add(entry);
            }
         }
      }
   }
   
   @Unwrap
   public List<ConversationEntry> getConversationEntryList()
   {
      return conversationEntryList;
   }
}
