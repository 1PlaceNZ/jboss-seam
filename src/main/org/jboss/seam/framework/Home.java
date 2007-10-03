package org.jboss.seam.framework;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Base class for components which provide persistence
 * operations to a managed entity instance. This class 
 * may be reused by either configuration or extension, 
 * and may be bound directly to a view, or accessed by 
 * some intermediate Seam component.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.CONVERSATION)
public abstract class Home<T, E> extends MutableController<T>
{
   private static final long serialVersionUID = -5462396456614090423L;
   
   private Object id;
   protected E instance;
   private Class<E> entityClass;
   protected ValueExpression newInstance;

   private String deletedMessage = "Successfully deleted";
   private String createdMessage = "Successfully created";
   private String updatedMessage = "Successfully updated";
   
   protected void updatedMessage()
   {
      debug("updated entity #0 #1", getEntityClass().getName(), getId());
      getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getUpdatedMessageKey(), getUpdatedMessage() );
   }
   
   protected void deletedMessage()
   {
      debug("deleted entity #0 #1", getEntityClass().getName(), getId());
      getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getDeletedMessageKey(), getDeletedMessage() );
   }
   
   protected void createdMessage()
   {
      debug("created entity #0 #1", getEntityClass().getName(), getId());
      getFacesMessages().addFromResourceBundleOrDefault( SEVERITY_INFO, getCreatedMessageKey(), getCreatedMessage() );
   }

   @Create
   public void create()
   {
      if ( getEntityClass()==null )
      {
         throw new IllegalStateException("entityClass is null");
      }
   }

   @Transactional
   public E getInstance()
   {
      joinTransaction();
      if (instance==null)
      {
         initInstance();
      }
      return instance;
   }
   
   public void clearInstance()
   {
      setInstance(null);
      setId(null);
   }

   protected void initInstance()
   {
      if ( isIdDefined() )
      {
         if ( !isTransactionMarkedRollback() )
         {
            //we cache the instance so that it does not "disappear"
            //after remove() is called on the instance
            //is this really a Good Idea??
            setInstance( find() );
         }
      }
      else
      {
         setInstance( createInstance() );
      }
   }
   
   protected void joinTransaction() {}
   
   protected E find()
   {
      return null;
   }

   protected E handleNotFound()
   {
      throw new EntityNotFoundException( getId(), getEntityClass() );
   }

   protected E createInstance()
   {
      if (newInstance!=null)
      {
         return (E) newInstance.getValue();
      }
      else if (getEntityClass()!=null)
      {
         try
         {
            return getEntityClass().newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         return null;
      }
   }

   public Class<E> getEntityClass()
   {
      if (entityClass==null)
      {
         Type type = getClass().getGenericSuperclass();
         if (type instanceof ParameterizedType)
         {
            ParameterizedType paramType = (ParameterizedType) type;
            entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
         }
         else
         {
            throw new IllegalArgumentException("Could not guess entity class by reflection");
         }
      }
      return entityClass;
   }

   public void setEntityClass(Class<E> entityClass)
   {
      this.entityClass = entityClass;
   }
   
   public Object getId()
   {
      return id;
   }

   public void setId(Object id)
   {
      if ( setDirty(this.id, id) ) setInstance(null);
      this.id = id;
   }
   
   protected void assignId(Object id)
   {
      setDirty(this.id, id);
      this.id = id;
   }
   
   public boolean isIdDefined()
   {
      return getId()!=null && !"".equals( getId() );
   }

   public void setInstance(E instance)
   {
      setDirty(this.instance, instance);
      this.instance = instance;
   }

   public ValueExpression getNewInstance()
   {
      return newInstance;
   }

   public void setNewInstance(ValueExpression newInstance)
   {
      this.newInstance = newInstance;
   }

   public String getCreatedMessage()
   {
      return createdMessage;
   }

   public void setCreatedMessage(String createdMessage)
   {
      this.createdMessage = createdMessage;
   }

   public String getDeletedMessage()
   {
      return deletedMessage;
   }

   public void setDeletedMessage(String deletedMessage)
   {
      this.deletedMessage = deletedMessage;
   }

   public String getUpdatedMessage()
   {
      return updatedMessage;
   }

   public void setUpdatedMessage(String updatedMessage)
   {
      this.updatedMessage = updatedMessage;
   }
   
   protected String getMessageKeyPrefix()
   {
      String className = getEntityClass().getName();
      return className.substring( className.lastIndexOf('.') + 1 ) + '_';
   }
   
   protected String getCreatedMessageKey()
   {
      return getMessageKeyPrefix() + "created";
   }
   
   protected String getUpdatedMessageKey()
   {
      return getMessageKeyPrefix() + "updated";
   }
   
   protected String getDeletedMessageKey()
   {
      return getMessageKeyPrefix() + "deleted";
   }
   
   protected void raiseAfterTransactionSuccessEvent()
   {
      raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess");
      raiseTransactionSuccessEvent("org.jboss.seam.afterTransactionSuccess." + getSimpleEntityName());
   }
   
   protected String getSimpleEntityName()
   {
      String name = getEntityName();
      return name.lastIndexOf(".") > 0 && name.lastIndexOf(".") < name.length()  ? name.substring(name.lastIndexOf(".") + 1, name.length()) : name;
   }
   
   protected abstract String getEntityName();
   
}
