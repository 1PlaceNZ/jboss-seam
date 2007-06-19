package org.jboss.seam.faces;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.Query;
import org.jboss.seam.jsf.ArrayDataModel;
import org.jboss.seam.jsf.ListDataModel;
import org.jboss.seam.jsf.MapDataModel;
import org.jboss.seam.jsf.SetDataModel;

@Name("org.jboss.seam.jsf.jsfProvider")
@Install(precedence=BUILT_IN, classDependencies="javax.faces.context.FacesContext")
@Scope(STATELESS)
@Intercept(NEVER)
public class JsfProvider
{
   
   public DataModel getDataModel(Object value)
   {
      if (value instanceof List)
      {
         return new ListDataModel( (List) value );
      }
      else if (value instanceof Object[])
      {
         return new ArrayDataModel( (Object[]) value ); 
      }
      else if (value instanceof Map)
      {
         return new MapDataModel( (Map) value );
      }
      else if (value instanceof Set)
      {
         return new SetDataModel( (Set) value );
      }
      else
      {
         throw new IllegalArgumentException("unknown collection type: " + value.getClass());
      }
   }
   
   public DataModel getDataModel(Query query)
   {
      return getDataModel( query.getResultList() );
   }
   
   public static JsfProvider instance()
   {
      return (JsfProvider) Component.getInstance(JsfProvider.class, ScopeType.STATELESS);
   }
   
}
