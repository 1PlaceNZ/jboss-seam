/*
�* JBoss, Home of Professional Open Source
�*
�* Distributable under LGPL license.
�* See terms of license at gnu.org.
�*/
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.pages.ConversationIdParameter;
import org.jboss.seam.pages.Page;
import org.jboss.seam.util.Id;

/**
 * The Seam conversation manager.
 *
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.manager")
@Install(precedence=BUILT_IN)
@Intercept(NEVER)
public class Manager
{
   private static final LogProvider log = Logging.getLogProvider(Manager.class);

   //The id of the current conversation
   private String currentConversationId;
   private List<String> currentConversationIdStack;

   //Is the current conversation "long-running"?
   private boolean isLongRunningConversation;
   
   //private boolean updateModelValuesCalled;

   private boolean controllingRedirect;
   
   private boolean destroyBeforeRedirect;
   
   private int conversationTimeout = 600000; //10 mins
   private int concurrentRequestTimeout = 1000; //one second
   
   private String conversationIdParameter = "conversationId";
   private String parentConversationIdParameter = "parentConversationId";

   // DONT BREAK, icefaces uses this
   public String getCurrentConversationId()
   {
      return currentConversationId;
   }

   /**
    * Only public for the unit tests!
    * @param id
    */
   public void setCurrentConversationId(String id)
   {
      currentConversationId = id;
      currentConversationEntry = null;
   }
   
   /**
    * Change the id of the current conversation.
    * 
    * @param id the new conversation id
    */
   public void updateCurrentConversationId(String id)
   {
      if ( ConversationEntries.instance().getConversationIds().contains(id) )
      {
         throw new IllegalStateException("Conversation id is already in use: " + id);
      }
      
      String[] names = Contexts.getConversationContext().getNames();
      Object[] values = new Object[names.length];
      for (int i=0; i<names.length; i++)
      {
         values[i] = Contexts.getConversationContext().get(names[i]);
         Contexts.getConversationContext().remove(names[i]);
      }
      Contexts.getConversationContext().flush();
      
      ConversationEntry ce = ConversationEntries.instance().updateConversationId(currentConversationId, id);
      setCurrentConversationId(id);
      if (ce!=null)
      {
         setCurrentConversationIdStack( ce.getConversationIdStack() );
         //TODO: what about child conversations?!
      }
      
      for (int i=0; i<names.length; i++)
      {
         Contexts.getConversationContext().set(names[i], values[i]);
      }
   }

   private static void touchConversationStack(List<String> stack)
   {
      if ( stack!=null )
      {
         //iterate in reverse order, so that current conversation 
         //sits at top of conversation lists
         ListIterator<String> iter = stack.listIterator( stack.size() );
         while ( iter.hasPrevious() )
         {
            String conversationId = iter.previous();
            ConversationEntry conversationEntry = ConversationEntries.instance().getConversationEntry(conversationId);
            if (conversationEntry!=null)
            {
               conversationEntry.touch();
            }
         }
      }
   }
   
   private static void endNestedConversations(String id)
   {
      for ( ConversationEntry ce: ConversationEntries.instance().getConversationEntries() )
      {
         if ( ce.getConversationIdStack().contains(id) )
         {
            ce.end();
         }
      }
   }
   
   /**
    * Get the name of the component that started the current
    * conversation.
    * 
    * @deprecated
    */
   public Object getCurrentConversationInitiator()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if (ce!=null)
      {
         return ce.getInitiatorComponentName();
      }
      else
      {
         return null;
      }
   }

   public List<String> getCurrentConversationIdStack()
   {
      return currentConversationIdStack;
   }

   public void setCurrentConversationIdStack(List<String> stack)
   {
      currentConversationIdStack = stack;
   }

   private List<String> createCurrentConversationIdStack(String id)
   {
      currentConversationIdStack = new ArrayList<String>();
      currentConversationIdStack.add(id);
      return currentConversationIdStack;
   }

   public String getCurrentConversationDescription()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getDescription();
   }

   public Integer getCurrentConversationTimeout()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getTimeout();
   }

   public String getCurrentConversationViewId()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if ( ce==null ) return null;
      return ce.getViewId();
   }
   
   public String getParentConversationViewId()
   {
      ConversationEntry conversationEntry = ConversationEntries.instance().getConversationEntry(getParentConversationId());
      return conversationEntry==null ? null : conversationEntry.getViewId();
   }
   
   public String getParentConversationId()
   {
      return currentConversationIdStack==null || currentConversationIdStack.size()<2 ?
            null : currentConversationIdStack.get(1);
   }

   public String getRootConversationId()
   {
      return currentConversationIdStack==null || currentConversationIdStack.size()<1 ?
            null : currentConversationIdStack.get( currentConversationIdStack.size()-1 );
   }

   // DONT BREAK, icefaces uses this
   public boolean isLongRunningConversation()
   {
      return isLongRunningConversation;
   }

   public boolean isLongRunningOrNestedConversation()
   {
      return isLongRunningConversation() || isNestedConversation();
   }

   public boolean isReallyLongRunningConversation()
   {
      return isLongRunningConversation() && 
            !getCurrentConversationEntry().isRemoveAfterRedirect() &&
            !ServletSession.instance().isInvalid();
   }
   
   public boolean isNestedConversation()
   {
      return currentConversationIdStack!=null && 
            currentConversationIdStack.size()>1;
   }

   public void setLongRunningConversation(boolean isLongRunningConversation)
   {
      this.isLongRunningConversation = isLongRunningConversation;
   }

   public static Manager instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("No active event context");
      }
      Manager instance = (Manager) Component.getInstance(Manager.class, ScopeType.EVENT);
      if (instance==null)
      {
         throw new IllegalStateException("No Manager could be created, make sure the Component exists in application scope");
      }
      return instance;
   }

   /**
    * Clean up timed-out conversations
    */
   public void conversationTimeout(Map<String, Object> session)
   {
      long currentTime = System.currentTimeMillis();
      ConversationEntries conversationEntries = ConversationEntries.getInstance();
      if (conversationEntries!=null)
      {
         List<ConversationEntry> entries = new ArrayList<ConversationEntry>( conversationEntries.getConversationEntries() );
         for (ConversationEntry conversationEntry: entries)
         {
            boolean locked = conversationEntry.lockNoWait(); //we had better not wait for it, or we would be waiting for ALL other requests
            try
            {
               long delta = currentTime - conversationEntry.getLastRequestTime();
               if ( delta > conversationEntry.getTimeout() )
               {
                  if ( locked )
                  { 
                     if ( log.isDebugEnabled() )
                     {
                        log.debug("conversation timeout for conversation: " + conversationEntry.getId());
                     }
                  }
                  else
                  {
                     //if we could not acquire the lock, someone has left a garbage lock lying around
                     //the reason garbage locks can exist is that we don't require a servlet filter to
                     //exist - but if we do use SeamExceptionFilter, it will clean up garbage and this
                     //case should never occur
                     
                     //NOTE: this is slightly broken - in theory there is a window where a new request 
                     //      could have come in and got the lock just before us but called touch() just 
                     //      after we check the timeout - but in practice this would be extremely rare, 
                     //      and that request will get an IllegalMonitorStateException when it tries to 
                     //      unlock() the CE
                     log.info("destroying conversation with garbage lock: " + conversationEntry.getId());
                  }
                  destroyConversation( conversationEntry.getId(), session );
               }
            }
            finally
            {
               if (locked) conversationEntry.unlock();
            }
         }
      }
   }

   /**
    * Clean up all state associated with a conversation
    */
   private void destroyConversation(String conversationId, Map<String, Object> session)
   {
      Lifecycle.destroyConversationContext(session, conversationId);
      ConversationEntries.instance().removeConversationEntry(conversationId);
   }

   /**
    * Touch the conversation stack, destroy ended conversations, 
    * and timeout inactive conversations.
    */
   public void endRequest(Map<String, Object> session)
   {
      if ( isLongRunningConversation() )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("Storing conversation state: " + getCurrentConversationId());
         }
         touchConversationStack( getCurrentConversationIdStack() );
      }
      else
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("Discarding conversation state: " + getCurrentConversationId());
         }
         //now safe to remove the entry
         removeCurrentConversationAndDestroyNestedContexts(session);
      }

      if ( !Init.instance().isClientSideConversations() ) 
      {
         // difficult question: is it really safe to do this here?
         // right now we do have to do it after committing the Seam
         // transaction because we can't close EMs inside a txn
         // (this might be a bug in HEM)
         Manager.instance().conversationTimeout(session);
      }
   }
   
   public void unlockConversation()
   {
      ConversationEntry ce = getCurrentConversationEntry();
      if (ce!=null) 
      {
         if ( ce.isLockedByCurrentThread() )
         {
            ce.unlock();
         }
      }
      else if ( isNestedConversation() )
      {
         ConversationEntries.instance().getConversationEntry( getParentConversationId() ).unlock();
      }
   }

   private void removeCurrentConversationAndDestroyNestedContexts(Map<String, Object> session) 
   {
      ConversationEntries conversationEntries = ConversationEntries.getInstance();
      if (conversationEntries!=null)
      {
         conversationEntries.removeConversationEntry( getCurrentConversationId() );
         destroyNestedConversationContexts( session, getCurrentConversationId() );
      }
   }

   private void destroyNestedConversationContexts(Map<String, Object> session, String conversationId) 
   {
      List<ConversationEntry> entries = new ArrayList<ConversationEntry>( ConversationEntries.instance().getConversationEntries() );
      for  ( ConversationEntry ce: entries )
      {
         if ( ce.getConversationIdStack().contains(conversationId) )
         {
            String entryConversationId = ce.getId();
            log.debug("destroying nested conversation: " + entryConversationId);
            destroyConversation(entryConversationId, session);
         }
      }
   }

   private String getPropagationFromRequestParameter(Map parameters)
   {
      Object type = parameters.get("conversationPropagation");
      if (type==null)
      {
         return null;
      }
      else if (type instanceof String)
      {
         return (String) type;
      }
      else
      {
         return ( (String[]) type )[0];
      }
   }
   
   /**
    * Initialize the request conversation context, taking
    * into account conversation propagation style, and
    * any conversation id passed as a request parameter
    * or in the PAGE context.
    * 
    * @param parameters the request parameters
    * @return false if the conversation id referred to a 
    *         long-running conversation that was not found
    */
   public boolean restoreConversation(Map parameters)
   {
      String storedConversationId = null;
      String storedParentConversationId = null;
      boolean validateLongRunningConversation = false;
      
      //First, try to get the conversation id from the request parameter defined for the page
      String viewId = Pages.getCurrentViewId();
      if ( viewId!=null )
      {
         Page page = Pages.instance().getPage(viewId);
         storedConversationId = page.getConversationIdParameter().getRequestConversationId(parameters);
         //TODO: how about the parent conversation id?
      }
      
      //Next, try to get the conversation id from the globally defined request parameters
      if ( isMissing(storedConversationId) )
      {
         storedConversationId = getRequestParameterValue(parameters, conversationIdParameter);
      }
      if ( isMissing(storedParentConversationId) )
      {
         storedParentConversationId = getRequestParameterValue(parameters, parentConversationIdParameter);
      }
            
      if ( Contexts.isPageContextActive() && isMissing(storedConversationId) ) 
      {
         //checkPageContext is a workaround for a bug in MySQL server-side state saving
         
         //if it is not passed as a request parameter,
         //try to get it from the page context
         org.jboss.seam.core.FacesPage page = org.jboss.seam.core.FacesPage.instance();
         storedConversationId = page.getConversationId();
         storedParentConversationId = null;
         validateLongRunningConversation = page.isConversationLongRunning();
      }

      else
      {
         log.debug("Found conversation id in request parameter: " + storedConversationId);
      }

      String propagation = getPropagationFromRequestParameter(parameters);
      if ( "none".equals(propagation) )
      {
         storedConversationId = null;
         storedParentConversationId = null;
         validateLongRunningConversation = false;
      }
      else if ( "end".equals(propagation) )
      {
         validateLongRunningConversation = false;
      }
      
      return restoreConversation(storedConversationId, storedParentConversationId) 
               || !validateLongRunningConversation;
      
   }
   
   /**
    * Look for a conversation propagation style in the request
    * parameters and begin, nest or join the conversation,
    * as necessary.
    * 
    * @param parameters the request parameters
    */
   public void handleConversationPropagation(Map parameters)
   {
      
      String propagation = getPropagationFromRequestParameter(parameters);
      
      if ( propagation!=null && propagation.startsWith("begin") )
      {
         if ( isLongRunningConversation )
         {
            throw new IllegalStateException("long-running conversation already active");
         }
         beginConversation(null);
         if (propagation.length()>6)
         {
            Pageflow.instance().begin( propagation.substring(6) );
         }
      }
      else if ( propagation!=null && propagation.startsWith("join") )
      {
         if ( !isLongRunningConversation )
         {
            beginConversation(null);
            if (propagation.length()>5)
            {
               Pageflow.instance().begin( propagation.substring(5) );
            }
         }
      }
      else if ( propagation!=null && propagation.startsWith("nest") )
      {
         beginNestedConversation(null);
         if (propagation.length()>5)
         {
            Pageflow.instance().begin( propagation.substring(5) );
         }
      }
      else if ( "end".equals(propagation) )
      {
         endConversation(false);
      }

   }
   
   /**
    * Initialize the request conversation context, given the 
    * conversation id. If no conversation entry is found, or
    * conversationId is null, initialize a new temporary
    * conversation context.
    * 
    * @return true if the conversation with the given id was found
    */
   public boolean restoreConversation(String conversationId)
   {
      return restoreConversation(conversationId, null);
   }

   /**
    * Initialize the request conversation context, given the 
    * conversation id and optionally a parent conversation id.
    * If no conversation entry is found for the first id, try
    * the parent, and if that also fails, initialize a new 
    * temporary conversation context.
    */
   private boolean restoreConversation(String conversationId, String parentConversationId) 
   {
      ConversationEntry ce = null;
      if (conversationId!=null)
      {
         ConversationEntries entries = ConversationEntries.instance();
         ce = entries.getConversationEntry(conversationId);
         if (ce==null)
         {
            ce = entries.getConversationEntry(parentConversationId);
         }
      }
      
      return restoreAndLockConversation(ce);
   }

   private boolean restoreAndLockConversation(ConversationEntry ce)
   {
      if ( ce!=null && ce.lock() )
      {
         // do this asap, since there is a window where conversationTimeout() might  
         // try to destroy the conversation, even if he cannot obtain the lock!
         touchConversationStack( ce.getConversationIdStack() );

         //we found an id and obtained the lock, so restore the long-running conversation
         log.debug("Restoring conversation with id: " + ce.getId());
         setLongRunningConversation(true);
         setCurrentConversationId( ce.getId() );
         setCurrentConversationIdStack( ce.getConversationIdStack() );

         boolean removeAfterRedirect = ce.isRemoveAfterRedirect() && !(
               Init.instance().isDebug() &&
               (FacesContext.getCurrentInstance() != null) &&
               "/debug.xhtml".equals( Pages.getCurrentViewId() )
            );
         
         if (removeAfterRedirect)
         {
            setLongRunningConversation(false);
            ce.setRemoveAfterRedirect(false);
         }
         
         return true;

      }
      else
      {
         //there was no id in either place, so there is no
         //long-running conversation to restore
         log.debug("No stored conversation, or concurrent call to the stored conversation");
         initializeTemporaryConversation();
         return false;
      }
   }

   /**
    * Retrieve the conversation id from the request parameters.
    * 
    * @param parameters the request parameters
    * @return the conversation id
    */
   public static String getRequestParameterValue(Map parameters, String parameterName) {
      Object object = parameters.get(parameterName);
      if (object==null)
      {
         return null;
      }
      else
      {
         if ( object instanceof String )
         {
            //when it comes from JSF it is (usually?) a plain string
            return (String) object;
         }
         else
         {
            //in a servlet it is a string array
            String[] values = (String[]) object;
            if (values.length!=1)
            {
               throw new IllegalArgumentException("expected exactly one value for conversationId request parameter");
            }
            return values[0];
         }
      }
   }

   private boolean isMissing(String storedConversationId) 
   {
      return storedConversationId==null || "".equals(storedConversationId);
   }
   
   /**
    * Initialize a new temporary conversation context,
    * and assign it a conversation id.
    */
   public void initializeTemporaryConversation()
   {
      String id = generateInitialConversationId();
      setCurrentConversationId(id);
      createCurrentConversationIdStack(id);
      setLongRunningConversation(false);
   }

   private String generateInitialConversationId()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String viewId = Pages.getViewId(facesContext);
      if ( viewId!=null )
      {
         return Pages.instance().getPage(viewId)
                     .getConversationIdParameter()
                     .getInitialConversationId( facesContext.getExternalContext().getRequestParameterMap() );
      }
      else
      {
         return Id.nextId();
      }
   }

   private ConversationEntry createConversationEntry()
   {
      ConversationEntry entry = ConversationEntries.instance()
            .createConversationEntry( getCurrentConversationId(), getCurrentConversationIdStack() );
      if ( !entry.isNested() ) 
      {
         //if it is a newly created nested 
         //conversation, we already own the
         //lock
         entry.lock();
      }
      return entry;
   }

   /**
    * Promote a temporary conversation and make it long-running
    * 
    * @param initiator the name of the component starting the conversation.
    */
   @SuppressWarnings("deprecation")
   public void beginConversation(String initiator)
   {
      log.debug("Beginning long-running conversation");
      setLongRunningConversation(true);
      createConversationEntry().setInitiatorComponentName(initiator);
      Conversation.instance(); //force instantiation of the Conversation in the outer (non-nested) conversation
      storeConversationToViewRootIfNecessary();
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.beginConversation");
   }

   /**
    * Begin a new nested conversation.
    * 
    * @param ownerName the name of the component starting the conversation
    */
   @SuppressWarnings("deprecation")
   public void beginNestedConversation(String ownerName)
   {
      log.debug("Beginning nested conversation");
      List<String> oldStack = getCurrentConversationIdStack();
      if (oldStack==null)
      {
         throw new IllegalStateException("No long-running conversation active");
      }
      String id = Id.nextId();
      setCurrentConversationId(id);
      createCurrentConversationIdStack(id).addAll(oldStack);
      ConversationEntry conversationEntry = createConversationEntry();
      conversationEntry.setInitiatorComponentName(ownerName);
      storeConversationToViewRootIfNecessary();
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.beginConversation");
   }
   
   /**
    * Make a long-running conversation temporary.
    */
   public void endConversation(boolean beforeRedirect)
   {
      log.debug("Ending long-running conversation");
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.endConversation");
      setLongRunningConversation(false);
      destroyBeforeRedirect = beforeRedirect;
      endNestedConversations( getCurrentConversationId() );
      storeConversationToViewRootIfNecessary();
   }
   
   private void storeConversationToViewRootIfNecessary()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( facesContext!=null && Lifecycle.getPhaseId()==PhaseId.RENDER_RESPONSE )
      {
         FacesPage.instance().storeConversation();
      }
   }

   // two reasons for this: 
   // (1) a cache
   // (2) so we can unlock() it after destruction of the session context 
   private ConversationEntry currentConversationEntry; 
   
   public ConversationEntry getCurrentConversationEntry() 
   {
      if (currentConversationEntry==null)
      {
         currentConversationEntry = ConversationEntries.instance().getConversationEntry( getCurrentConversationId() );
      }
      return currentConversationEntry;
   }
   
   /**
    * Leave the scope of the current conversation, leaving
    * it completely intact.
    */
   public void leaveConversation()
   {
      initializeTemporaryConversation();
   }

   /**
    * Switch to another long-running conversation.
    * 
    * @param id the id of the conversation to switch to
    * @return true if the conversation exists
    */
   public boolean switchConversation(String id)
   {
      ConversationEntry ce = ConversationEntries.instance().getConversationEntry(id);
      if (ce!=null)
      {
         if ( ce.lock() )
         {
            unlockConversation();
            setCurrentConversationId(id);
            setCurrentConversationIdStack( ce.getConversationIdStack() );
            setLongRunningConversation(true);
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   public int getConversationTimeout() {
      return conversationTimeout;
   }

   public void setConversationTimeout(int conversationTimeout) {
      this.conversationTimeout = conversationTimeout;
   }
   
   /**
    * Temporarily promote a temporary conversation to
    * a long running conversation for the duration of
    * a browser redirect. After the redirect, the 
    * conversation will be demoted back to a temporary
    * conversation.
    */
   public void beforeRedirect()
   {
      //DONT BREAK, icefaces uses this
      if (!destroyBeforeRedirect)
      {
         ConversationEntry ce = getCurrentConversationEntry();
         if (ce==null)
         {
            ce = createConversationEntry();
         }
         //ups, we don't really want to destroy it on this request after all!
         ce.setRemoveAfterRedirect( !isLongRunningConversation() );
         setLongRunningConversation(true);
      }
   }

   /**
    * Temporarily promote a temporary conversation to
    * a long running conversation for the duration of
    * a browser redirect. After the redirect, the 
    * conversation will be demoted back to a temporary
    * conversation. Handle any changes to the conversation
    * id, due to propagation via natural id.
    */
   public void beforeRedirect(String viewId)
   {
      beforeRedirect();
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      String currentViewId = Pages.getViewId(facesContext);
      if ( viewId!=null && currentViewId!=null )
      {
         ConversationIdParameter currentPage = Pages.instance().getPage(currentViewId).getConversationIdParameter();
         ConversationIdParameter targetPage = Pages.instance().getPage(viewId).getConversationIdParameter();
         if ( isDifferentConversationId(currentPage, targetPage) )
         {
            updateCurrentConversationId( targetPage.getConversationId() );
         }      
      }
   }

   private boolean isDifferentConversationId(ConversationIdParameter sp, ConversationIdParameter tp)
   {
      return sp.getName()!=tp.getName() && ( sp.getName()==null || !sp.getName().equals( tp.getName() ) );
   }
   
   /**
    * Add the conversation id to a URL, if necessary
    * 
    * @deprecated use encodeConversationId(String url, String viewId)
    */
   public String encodeConversationId(String url)
   {
      //DONT BREAK, icefaces uses this
      return encodeConversationIdParameter( url, getConversationIdParameter(), getCurrentConversationId() );
   }
         
   /**
    * Add the conversation id to a URL, if necessary
    */
   public String encodeConversationId(String url, String viewId) 
   {
      //DONT BREAK, icefaces uses this
      ConversationIdParameter cip = Pages.instance().getPage(viewId).getConversationIdParameter();
      return encodeConversationIdParameter( url, cip.getParameterName(), cip.getParameterValue() );
   }
 
   private String encodeConversationIdParameter(String url, String paramName, String paramValue)
   {
         
      if ( ServletSession.instance().isInvalid() || containsParameter(url, paramName) )
      {
         return url;
      }
      else if (destroyBeforeRedirect)
      {
         if ( isNestedConversation() )
         {
            return new StringBuilder( url.length() + paramName.length() + 5 )
                  .append(url)
                  .append( url.contains("?") ? '&' : '?' )
                  .append(paramName)
                  .append('=')
                  .append( encode( getParentConversationId() ) )
                  .toString();
         }
         else
         {
            return url;
         }
      }
      else
      {
         StringBuilder builder = new StringBuilder( url.length() + paramName.length() + 5 )
               .append(url)
               .append( url.contains("?") ? '&' : '?' )
               .append(paramName)
               .append('=')
               .append( encode(paramValue) );
         if ( isNestedConversation() && !isReallyLongRunningConversation() )
         {
            builder.append('&')
                  .append(parentConversationIdParameter)
                  .append('=')
                  .append( encode( getParentConversationId() ) );
         }
         return builder.toString();
      }
   }

   public void interpolateAndRedirect(String url)
   {
      Map<String, Object> parameters = new HashMap<String, Object>();
      int loc = url.indexOf('?');
      if (loc>0)
      {
         StringTokenizer tokens = new StringTokenizer( url.substring(loc), "?=&" );
         while ( tokens.hasMoreTokens() )
         {
            String name = tokens.nextToken();
            String value = Interpolator.instance().interpolate( tokens.nextToken() );
            parameters.put(name, value);
         }
         url = url.substring(0, loc);
      }
      redirect(url, parameters, true);
   }
   
   /**
    * Add the parameters to a URL
    */
   public String encodeParameters(String url, Map<String, Object> parameters)
   {
      if ( parameters.isEmpty() ) return url;
      
      StringBuilder builder = new StringBuilder(url);
      for ( Map.Entry<String, Object> param: parameters.entrySet() )
      {
         String parameterName = param.getKey();
         if ( !containsParameter(url, parameterName) )
         {
            Object parameterValue = param.getValue();
            if (parameterValue instanceof Iterable)
            {
               for ( Object value: (Iterable) parameterValue )
               {
                  builder.append('&')
                        .append(parameterName)
                        .append('=');
                  if (value!=null)
                  {
                     builder.append(encode(value));
                  }
               }
            }
            else
            {
               builder.append('&')
                     .append(parameterName)
                     .append('=');
               if (parameterValue!=null)
               {
                  builder.append(encode(parameterValue));
               }
            }
         }
      }
      if ( url.indexOf('?')<0 ) 
      {
         builder.setCharAt( url.length() ,'?' );
      }
      return builder.toString();
   }

   private boolean containsParameter(String url, String parameterName)
   {
      return url.indexOf('?' + parameterName + '=')>0 || 
            url.indexOf( '&' + parameterName + '=')>0;
   }

   private String encode(Object value)
   {
      try
      {
         return URLEncoder.encode(String.valueOf(value),"UTF-8");
      }
      catch (UnsupportedEncodingException iee)
      {
         throw new RuntimeException(iee);
      }
   }
   
   /**
    * Redirect to the given view id, encoding the conversation id
    * into the request URL.
    * 
    * @param viewId the JSF view id
    */
   public void redirect(String viewId)
   {
      redirect(viewId, null, true);
   }
   
   /**
    * Redirect to the given view id, after encoding parameters and conversation id 
    * into the request URL.
    * 
    * @param viewId the JSF view id
    * @param parameters request parameters to be encoded (possibly null)
    * @param includeConversationId determines if the conversation id is to be encoded
    */
   public void redirect(String viewId, Map<String, Object> parameters, 
            boolean includeConversationId)
   {
      /*if ( Lifecycle.getPhaseId()==PhaseId.RENDER_RESPONSE )
      {
         throw new IllegalStateException("attempted to redirect during RENDER_RESPONSE phase");
      }*/
      FacesContext context = FacesContext.getCurrentInstance();
      String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
      if (parameters!=null) 
      {
         url = encodeParameters(url, parameters);
      }
      url = Pages.instance().encodePageParameters( 
               FacesContext.getCurrentInstance(), 
               url, 
               viewId, 
               parameters==null ? Collections.EMPTY_SET : parameters.keySet() 
            );
      if (includeConversationId)
      {
         beforeRedirect(viewId);
         url = encodeConversationId(url, viewId);
      }
      url = Pages.instance().encodeScheme(viewId, context, url);
      if ( log.isDebugEnabled() )
      {
         log.debug("redirecting to: " + url);
      }
      ExternalContext externalContext = context.getExternalContext();
      controllingRedirect = true;
      try
      {         
         externalContext.redirect( externalContext.encodeActionURL(url) );
      }
      catch (IOException ioe)
      {
         throw new RedirectException(ioe);
      }
      finally
      {
         controllingRedirect = false;
      }
      context.responseComplete(); //work around MyFaces bug in 1.1.1
   }
   
   /**
    * Called by the Seam Redirect Filter when a redirect is called.
    * Appends the conversationId parameter if necessary.
    * 
    * @param url the requested URL
    * @return the resulting URL with the conversationId appended
    */
   public String appendConversationIdFromRedirectFilter(String url, String viewId)
   {
      boolean appendConversationId = !controllingRedirect;
      if (appendConversationId)
      {
         beforeRedirect(viewId);         
         url = encodeConversationId(url, viewId);
      }
      return url;
   }

   /**
    * If a page description is defined, remember the description and
    * view id for the current page, to support conversation switching.
    * Called just before the render phase.
    */
   public void prepareBackswitch(FacesContext facesContext) 
   {
      
      Conversation conversation = Conversation.instance();

      //stuff from jPDL takes precedence
      org.jboss.seam.pageflow.Page pageflowPage = 
            isLongRunningConversation() &&
            Init.instance().isJbpmInstalled() && 
            Pageflow.instance().isInProcess() ?
                  Pageflow.instance().getPage() : null;
      
      if (pageflowPage==null)
      {
         //handle stuff defined in pages.xml
         Pages pages = Pages.instance();
         if (pages!=null) //for tests
         {
            String viewId = Pages.getViewId(facesContext);
            org.jboss.seam.pages.Page pageEntry = pages.getPage(viewId);
            if ( pageEntry.isSwitchEnabled() )
            {
               conversation.setViewId(viewId);
            }
            if ( pageEntry.hasDescription() )
            {
               conversation.setDescription( pageEntry.renderDescription() );
            }
            conversation.setTimeout( pages.getTimeout(viewId) );
         }
      }
      else
      {
         //use stuff from the pageflow definition
         if ( pageflowPage.isSwitchEnabled() )
         {
            conversation.setViewId( Pageflow.instance().getPageViewId() );
         }
         if ( pageflowPage.hasDescription() )
         {
            conversation.setDescription( pageflowPage.getDescription() );
         }
         conversation.setTimeout( pageflowPage.getTimeout() );
      }
      
      if ( isLongRunningConversation() )
      {
         //important: only do this stuff when a long-running
         //           conversation exists, otherwise we would
         //           force creation of a conversation entry
         conversation.flush();
      }

   }

   public String getConversationIdParameter()
   {
      return conversationIdParameter;
   }

   public void setConversationIdParameter(String conversationIdParameter)
   {
      this.conversationIdParameter = conversationIdParameter;
   }

   public int getConcurrentRequestTimeout()
   {
      return concurrentRequestTimeout;
   }

   public void setConcurrentRequestTimeout(int requestWait)
   {
      this.concurrentRequestTimeout = requestWait;
   }

   @Override
   public String toString()
   {
      return "Manager(" + currentConversationIdStack + ")";
   }

   protected String getParentConversationIdParameter()
   {
      return parentConversationIdParameter;
   }

   protected void setParentConversationIdParameter(String nestedConversationIdParameter)
   {
      this.parentConversationIdParameter = nestedConversationIdParameter;
   }

}
