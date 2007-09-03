package org.jboss.seam.navigation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Metadata for a &lt;redirect/&gt; in pages.xml
 * 
 * @author Gavin King
 *
 */
public final class RedirectNavigationHandler extends NavigationHandler
{
   private final ValueExpression<String> viewId;
   private final List<Param> params;
   private final String message;
   private final Severity severity;
   private final String control;

   public RedirectNavigationHandler(ValueExpression<String> viewId, List<Param> params, String message, Severity severity, String control)
   {
      this.viewId = viewId;
      this.params = params;
      this.message = message;
      this.severity = severity;
      this.control = control;
   }

   @Override
   public boolean navigate(FacesContext context)
   {
      addFacesMessage(message, severity, control);
      
      Map<String, Object> parameters = new HashMap<String, Object>();
      for ( Param parameter: params )
      {
         String value = parameter.getStringValueFromModel(context);
         //render it even if the value is null, since we want it
         //to override page parameter values which would be
         //appended by the redirect filter
         //if (value!=null)
         //{
            parameters.put( parameter.getName(), value );
         //}
      }
      
      redirect(viewId == null ? null : viewId.getValue(), parameters);
      return true;
   }

}