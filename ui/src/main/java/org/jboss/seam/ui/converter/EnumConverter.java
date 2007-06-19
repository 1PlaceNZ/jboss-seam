package org.jboss.seam.ui.converter;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Collection;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.jsf.Converter;

public class EnumConverter implements javax.faces.convert.Converter
{
   public Object getAsObject(FacesContext context, UIComponent comp, String value)
            throws ConverterException
   {
      ValueExpression expr = comp.getValueExpression("value");

      Class enumType = expr.getType(context.getELContext());
      if (enumType.isEnum())
      {
         return Enum.valueOf(enumType, value);
      }
      else
      {
         for (Object child : comp.getChildren())
         {
            if (child instanceof UIComponent)
            {
               UIComponent c = (UIComponent) child;
               expr = c.getValueExpression("value");
               Object val = expr.getValue(context.getELContext());
               if (val == null)
               {
                  throw new ConverterException("Cannot get items");
               }

               Class t = val.getClass();
               if (t.isArray() && t.getComponentType().isEnum())
               {
                  return Enum.valueOf(t.getComponentType(), value);
               }
               else if (val instanceof Collection)
               {
                  t = ((Collection) val).iterator().next().getClass();
                  return Enum.valueOf(t, value);
               }
            }
         }
      }

      throw new ConverterException("Unable to find selectItems with enum values.");
   }

   public String getAsString(FacesContext context, UIComponent component, Object object)
            throws ConverterException
   {
      return object == null ? null : ((Enum) object).name();
   }

}
