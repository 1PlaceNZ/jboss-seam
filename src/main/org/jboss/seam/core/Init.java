//$Id$
package org.jboss.seam.core;


import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.seam.Component;
import org.jboss.seam.Namespace;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.transaction.Transaction;

/**
 * A Seam component that holds Seam configuration settings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("org.jboss.seam.core.init")
@Install(value=false, precedence=BUILT_IN)
public class Init
{
   
   private Namespace rootNamespace = new Namespace(null);
   
   private Collection<Namespace> globalImports = new ArrayList<Namespace>();
   
   //private boolean isClientSideConversations = false;
   private boolean jbpmInstalled;
   private String jndiPattern;
   private boolean debug;
   private boolean myFacesLifecycleBug;
   private String userTransactionName;
   //private String transactionManagerName;
   private boolean transactionManagementEnabled = true;
   
   private Map<String, List<ObserverMethod>> observerMethods = new HashMap<String, List<ObserverMethod>>();
   private Map<String, List<ObserverMethodExpression>> observerMethodBindings = new HashMap<String, List<ObserverMethodExpression>>();
   private Map<String, FactoryMethod> factories = new HashMap<String, FactoryMethod>();
   private Map<String, FactoryExpression> factoryMethodExpressions = new HashMap<String, FactoryExpression>();
   private Map<String, FactoryExpression> factoryValueExpressions = new HashMap<String, FactoryExpression>();
   
   private Set<String> autocreateVariables = new HashSet<String>();
   private Set<String> installedFilters = new HashSet<String>();
   private Set<String> resourceProviders = new HashSet<String>();
   
   private Set<String> hotDeployableComponents = new HashSet<String>();
   
   private Map<String, String> converters = new HashMap<String, String>();
   private Map<String, String> validators = new HashMap<String, String>();
   private Map<Class, String> convertersByClass = new HashMap<Class, String>();
   
   private long timestamp;
   private File[] hotDeployPaths;
   
   @Create
   public void create()
   {
      /*if (transactionManagerName!=null)
      {
         Transactions.setTransactionManagerName(transactionManagerName);
      }*/
      if (userTransactionName!=null)
      {
         Transaction.setUserTransactionName(userTransactionName);
      }
   }
   
   public static Init instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      Init init = (Init) Contexts.getApplicationContext().get(Init.class);
      //commented out because of some test cases:
      /*if (init==null)
      {
         throw new IllegalStateException("No Init exists");
      }*/
      return init;
   }
   
   /*public boolean isClientSideConversations()
   {
      return isClientSideConversations;
   }

   public void setClientSideConversations(boolean isClientSideConversations)
   {
      this.isClientSideConversations = isClientSideConversations;
   }*/
   
   public static class FactoryMethod {
	   private Method method;
	   private Component component;
      private ScopeType scope;
      
	   FactoryMethod(Method method, Component component)
	   {
		   this.method = method;
		   this.component = component;
         scope = method.getAnnotation(org.jboss.seam.annotations.Factory.class).scope();
	   }
      
      public ScopeType getScope()
      {
         return scope;
      }
      public Component getComponent()
      {
         return component;
      }
      public Method getMethod()
      {
         return method;
      }
      @Override
      public String toString()
      {
         return "FactoryMethod(" + method + ')';
      }
   }
   
   public static class FactoryExpression 
   {
      private String expression;
      private ScopeType scope;
      
      FactoryExpression(String expression, ScopeType scope)
      {
         this.expression = expression;
         this.scope = scope;
      }
      
      public MethodExpression getMethodBinding()
      {
         //TODO: figure out some way to cache this!!
         return Expressions.instance().createMethodExpression(expression);
      }
      public ValueExpression getValueBinding()
      {
         //TODO: figure out some way to cache this!!
         return Expressions.instance().createValueExpression(expression);
      }
      public ScopeType getScope()
      {
         return scope;
      }
      @Override
      public String toString()
      {
         return "FactoryBinding(" + expression + ')';
      }
   }
   
   public FactoryMethod getFactory(String variable)
   {
      return factories.get(variable);
   }
   
   public FactoryExpression getFactoryMethodExpression(String variable)
   {
      return factoryMethodExpressions.get(variable);
   }
   
   public FactoryExpression getFactoryValueExpression(String variable)
   {
      return factoryValueExpressions.get(variable);
   }
   
   private void checkDuplicateFactory(String variable)
   {
      if ( factories.containsKey(variable) || factoryMethodExpressions.containsKey(variable) || factoryValueExpressions.containsKey(variable) )
      {
         //throw new IllegalStateException("duplicate factory for: " + variable);
      }
   }
   
   public void addFactoryMethod(String variable, Method method, Component component)
   {
      checkDuplicateFactory(variable);
	   factories.put( variable, new FactoryMethod(method, component) );
   }

   public void addFactoryMethodExpression(String variable, String methodBindingExpression, ScopeType scope)
   {
      checkDuplicateFactory(variable);
      factoryMethodExpressions.put( variable, new FactoryExpression(methodBindingExpression, scope) );
   }
   
   public void addFactoryValueExpression(String variable, String valueBindingExpression, ScopeType scope)
   {
      checkDuplicateFactory(variable);
      factoryValueExpressions.put( variable, new FactoryExpression(valueBindingExpression, scope) );
   }
   
   public static class ObserverMethod 
   {
      private Method method;
      private Component component;
      private boolean create;
      
      ObserverMethod(Method method, Component component, boolean create)
      {
         this.method = method;
         this.component = component;
         this.create = create;
      }

      public Component getComponent()
      {
         return component;
      }

      public Method getMethod()
      {
         return method;
      }

      public boolean isCreate()
      {
         return create;
      }

      @Override
      public String toString()
      {
         return "ObserverMethod(" + method + ')';
      }
   }
   
   public static class ObserverMethodExpression
   {
      private MethodExpression methodBinding;
      
      ObserverMethodExpression(MethodExpression method)
      {
         this.methodBinding = method;
      }

      public MethodExpression getMethodBinding()
      {
         return methodBinding;
      }

      @Override
      public String toString()
      {
         return "ObserverMethodBinding(" + methodBinding + ')';
      }
   }
   
   public List<ObserverMethod> getObserverMethods(String eventType)
   {
      return observerMethods.get(eventType);
   }
   
   public List<ObserverMethodExpression> getObserverMethodExpressions(String eventType)
   {
      return observerMethodBindings.get(eventType);
   }
   
   public void addObserverMethod(String eventType, Method method, Component component, boolean create)
   {
      List<ObserverMethod> observerList = observerMethods.get(eventType);
      if (observerList==null)
      {
         observerList = new ArrayList<ObserverMethod>();
         observerMethods.put(eventType, observerList);
      }
      observerList.add( new ObserverMethod(method, component, create) );
   }
   
   public void addObserverMethodExpression(String eventType, MethodExpression methodBinding)
   {
      List<ObserverMethodExpression> observerList = observerMethodBindings.get(eventType);
      if (observerList==null)
      {
         observerList = new ArrayList<ObserverMethodExpression>();
         observerMethodBindings.put(eventType, observerList);
      }
      observerList.add( new ObserverMethodExpression(methodBinding) );
   }
   
   public boolean isJbpmInstalled()
   {
      return jbpmInstalled;
   }
   
   public String getJndiPattern() 
   {
      return jndiPattern;
   }
    
   public void setJndiPattern(String jndiPattern) 
   {
	   this.jndiPattern = jndiPattern;
   }
   public boolean isDebug()
   {
      return debug;
   }
   public void setDebug(boolean debug)
   {
      this.debug = debug;
   }
   
   public boolean isMyFacesLifecycleBug()
   {
      return myFacesLifecycleBug;
   }
   
   public void setMyFacesLifecycleBug(boolean myFacesLifecycleBugExists)
   {
      this.myFacesLifecycleBug = myFacesLifecycleBugExists;
   }

   public void setJbpmInstalled(boolean jbpmInstalled)
   {
      this.jbpmInstalled = jbpmInstalled;
   }

   /**
    * The JNDI name of the JTA UserTransaction
    */
   public String getUserTransactionName()
   {
      return userTransactionName;
   }

   public void setUserTransactionName(String userTransactionName)
   {
      this.userTransactionName = userTransactionName;
   }

   public boolean isAutocreateVariable(String name)
   {
      return autocreateVariables.contains(name);
   }
   
   public void addAutocreateVariable(String name)
   {
      autocreateVariables.add(name);
   }

   public Namespace getRootNamespace()
   {
      return rootNamespace;
   }
   
   public void importNamespace(String namespaceName)
   {
      Namespace namespace = getRootNamespace();
      StringTokenizer tokens = new StringTokenizer(namespaceName, ".");
      while ( tokens.hasMoreTokens() )
      {
         namespace = namespace.getOrCreateChild( tokens.nextToken() );
      }
      globalImports.add(namespace);
   }

   public void addInstalledFilter(String name)
   {
      installedFilters.add(name);
   }
   
   public Set<String> getInstalledFilters()
   {
      return installedFilters;
   }
   
   public void addResourceProvider(String name)
   {
      resourceProviders.add(name);
   }
   
   public Set<String> getResourceProviders()
   {
      return resourceProviders;
   }

   public Set<String> getHotDeployableComponents()
   {
      return hotDeployableComponents;
   }

   public void addHotDeployableComponent(String name)
   {
      this.hotDeployableComponents.add(name);
   }

   public Map<String, String> getConverters()
   {
      return converters;
   }

   public Map<Class, String> getConvertersByClass()
   {
      return convertersByClass;
   }

   public Map<String, String> getValidators()
   {
      return validators;
   }
   
   public boolean hasHotDeployableComponents()
   {
      return hotDeployPaths!=null;
   }

   public File[] getHotDeployPaths()
   {
      return hotDeployPaths;
   }

   public void setHotDeployPaths(File[] hotDeployJars)
   {
      this.hotDeployPaths = hotDeployJars;
   }

   public long getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.timestamp = timestamp;
   }

   public boolean isTransactionManagementEnabled()
   {
      return transactionManagementEnabled;
   }

   public void setTransactionManagementEnabled(boolean transactionManagementEnabled)
   {
      this.transactionManagementEnabled = transactionManagementEnabled;
   }

   public Collection<Namespace> getGlobalImports()
   {
      return globalImports;
   }
}
