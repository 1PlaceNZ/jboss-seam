package org.jboss.seam.ui.component;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIOutput;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;

import org.jboss.seam.navigation.Pages;
import org.jboss.seam.ui.util.ViewUrlBuilder;

public abstract class UISeamCommandBase extends UIOutput implements ActionSource
{

   private String encodedUrl;

   public abstract String getView();

   public String getUrl() throws UnsupportedEncodingException
   {
      if (encodedUrl == null)
      {
         FacesContext context = getFacesContext();
         String viewId = getView();
         if (viewId == null)
         {
            viewId = Pages.getViewId(getFacesContext());
         }

         ViewUrlBuilder url = new ViewUrlBuilder(viewId, getFragment());

         Set<String> usedParameters = new HashSet<String>();
         for (Object child : getChildren())
         {
            if (child instanceof UIParameter)
            {
               usedParameters.add(((UIParameter) child).getName());
               url.addParameter((UIParameter) child);
            }
         }

         if (viewId != null)
         {
            Map<String, Object> pageParameters = Pages.instance().getConvertedParameters(context,
                     viewId, usedParameters);
            for (Map.Entry<String, Object> me : pageParameters.entrySet())
            {
               UIParameter uip = new UIParameter();
               uip.setName(me.getKey());
               uip.setValue(me.getValue());
               url.addParameter(uip);
            }
         }

         MethodBinding action = getAction();
         if (action != null)
         {

            UIAction uiAction = new UIAction();
            uiAction.setAction( action.getExpressionString() );
            url.addParameter(uiAction);
         }

         if ("default".equals(getPropagation()) || "join".equals(getPropagation())
                  || "nest".equals(getPropagation()) || "end".equals(getPropagation()))
         {
            UIConversationId uiConversationId = UIConversationId.newInstance();
            uiConversationId.setViewId(viewId);
            url.addParameter(uiConversationId);
         }

         if ("join".equals(getPropagation()) || "nest".equals(getPropagation())
                  || "begin".equals(getPropagation()) || "end".equals(getPropagation()))
         {
            UIConversationPropagation uiPropagation = UIConversationPropagation.newInstance();
            uiPropagation.setType(getPropagation());
            uiPropagation.setPageflow(getPageflow());
            url.addParameter(uiPropagation);
         }

         ValueExpression taskInstanceValueExpression = getValueExpression("taskInstance");
         if (taskInstanceValueExpression != null)
         {
            UITaskId uiTaskId = UITaskId.newInstance();
            uiTaskId.setValueExpression("taskInstance", taskInstanceValueExpression);
            url.addParameter(uiTaskId);
         }

         UISelection uiSelection = getSelection();
         if (uiSelection != null)
         {
            url.addParameter(uiSelection);
         }
         encodedUrl = url.getEncodedUrl();
      }
      return encodedUrl;
   }

   public abstract void setView(String view);

   public abstract String getPropagation();

   public abstract void setPropagation(String propagtion);

   public abstract String getPageflow();

   public abstract void setPageflow(String pageflow);

   public abstract String getFragment();

   public abstract void setFragment(String fragment);

   public UISelection getSelection()
   {
      UIData parentUIData = getParentUIData();
      if (parentUIData != null)
      {
         if (parentUIData.getValue() instanceof DataModel)
         {
            String dataModelExpression = parentUIData.getValueExpression("value")
                     .getExpressionString();
            String dataModelName = dataModelExpression.substring(2,
                     dataModelExpression.length() - 1).replace('$', '.');
            UISelection uiSelection = UISelection.newInstance();
            uiSelection.setDataModel(dataModelName);
            uiSelection.setVar(parentUIData.getVar());
            return uiSelection;
         }
         else
         {
            return null;
         }
      }
      else
      {
         return null;
      }
   }

   public UIData getParentUIData()
   {
      UIComponent parent = this.getParent();
      while (parent != null)
      {
         if (parent instanceof UIData)
         {
            return (UIData) parent;
         }
         else
         {
            parent = parent.getParent();
         }
      }
      return null;
   }
   

   
   public void removeActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub
      
   }
   
   public ActionListener[] getActionListeners()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   public void addActionListener(ActionListener listener)
   {
      // TODO Auto-generated method stub
      
   }

}
