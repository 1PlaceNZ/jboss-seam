package org.jboss.seam.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.model.DataModel;

import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;

public class HtmlLink extends HtmlOutputLink implements ActionSource
{
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLink";
   private String view;
   private MethodBinding action;
   private String pageflow;
   private String propagation = "default";
   private String fragment;
   private boolean disabled;
   private String outcome;
   
   private UISelection getSelection()
   {
      UIData parentUIData = getParentUIData();
      if (parentUIData!=null)
      {
         if ( parentUIData.getValue() instanceof DataModel )
         {
            String dataModelExpression = parentUIData.getValueExpression("value").getExpressionString();
            String dataModelName = dataModelExpression.substring(2, dataModelExpression.length()-1).replace('$','.');
            UISelection uiSelection = new UISelection();
            uiSelection.setDataModel(dataModelName);
            uiSelection.setVar( parentUIData.getVar() );
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
      while (parent!=null)
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
   
   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      if ( !isRendered() ) return;
      
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("a", this);
      writer.writeAttribute("id", getClientId(context), "id");
      String viewId;
      ValueExpression viewBinding = getValueExpression("view");
      if (viewBinding!=null)
      {
         viewId = (String) viewBinding.getValue(context.getELContext());
      }
      else if (view!=null)
      {
         viewId = view;
      }
      else
      {
         viewId = Pages.getViewId(context);
      }
      
      String url = context.getApplication().getViewHandler().getActionURL(context, viewId);
      String encodedUrl = context.getExternalContext().encodeActionURL(url);
      encodedUrl = Pages.instance().encodeScheme(viewId, context, encodedUrl);
      
      String characterEncoding = context.getResponseWriter().getCharacterEncoding();
      boolean first = true;
      Set<String> usedParameters = new HashSet<String>();
      
      boolean conversationIdEncoded = false;
      Page page = Pages.instance().getPage(viewId);
      
      for ( Object child: getChildren() )
      {
         if (child instanceof UIParameter)
         {
            UIParameter uip = (UIParameter) child;
            if ( uip.getValue()!=null )
            {
               encodedUrl += getParameterString(characterEncoding, uip, first);
               first = false;
               
               if ( uip.getName().equals( page.getConversationIdParameter().getParameterName() ) )
               {
                  conversationIdEncoded = true;
               }
            }
            usedParameters.add( uip.getName() );
         }
      }
     
      if (viewId!=null)
      {
         Map<String, Object> pageParameters = Pages.instance().getConvertedParameters(context, viewId, usedParameters);
         for ( Map.Entry<String, Object> me: pageParameters.entrySet() )
         {
            UIParameter uip = new UIParameter();
            uip.setName( me.getKey() );
            uip.setValue( me.getValue() );
            encodedUrl += getParameterString(characterEncoding, uip, first);
            first = false;
           
            if (!conversationIdEncoded && me.getKey().equals(page.getConversationIdParameter().getParameterName()))
            {
               conversationIdEncoded = true;
            }
         }
      }
     
      if ( action!=null || outcome!=null )
      {
         UIAction uiAction = new UIAction();
         uiAction.setAction( action==null ? outcome : action.getExpressionString() );
         encodedUrl += getParameterString(characterEncoding, uiAction, first);
         first = false;
      }
      
      if (( "default".equals(propagation) || "join".equals(propagation) || 
            "nest".equals(propagation) || "end".equals(propagation) ) )
      {
         //always add the id, since conversations could begin after link is rendered
         if ( !conversationIdEncoded )
         {
            UIConversationId uiConversationId = new UIConversationId();
            uiConversationId.setViewId(viewId);
            encodedUrl += getParameterString(characterEncoding, uiConversationId, first);
            first = false;
         }
      }
      
      if ( "join".equals(propagation) || "nest".equals(propagation) ||
           "begin".equals(propagation) || "end".equals(propagation) )
      {
         UIConversationPropagation uiPropagation = new UIConversationPropagation();
         uiPropagation.setType(propagation);
         ValueExpression pageflowBinding = getValueExpression("pageflow");
         String pf = pageflowBinding==null ? pageflow : (String) pageflowBinding.getValue( context.getELContext() );
         uiPropagation.setPageflow(pf);
         encodedUrl  += getParameterString(characterEncoding, uiPropagation, first);
         first = false;
      }
      
      ValueExpression taskInstanceValueBinding = getValueExpression("taskInstance");
      if (taskInstanceValueBinding!=null)
      {
         UITaskId uiTaskId = new UITaskId();
         uiTaskId.setValueExpression("taskInstance", taskInstanceValueBinding);
         encodedUrl  += getParameterString(characterEncoding, uiTaskId, first);
         first = false;
      }
      
      UISelection uiSelection = getSelection();
      if (uiSelection!=null)
      {
         encodedUrl += getParameterString(characterEncoding, uiSelection, first);
         first = false;
      }
            
      ValueExpression fragmentBinding = getValueExpression("fragment");
      String frag = fragmentBinding==null ? fragment : (String) fragmentBinding.getValue( context.getELContext() );
      if (frag!=null)
      {
         encodedUrl += '#' + frag;
      }
      
      if ( !isDisabled(context) )
      {
         writer.writeAttribute("href", encodedUrl, null);
      }
      HTML.renderHTMLAttributes(writer, this, HTML.ANCHOR_PASSTHROUGH_ATTRIBUTES);
      
      Object label = getValue();
      writer.flush();
      if (label!=null) 
      {
         writer.writeText( label, null );
      }
      
   }
   
   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      context.getResponseWriter().endElement("a");
   }
   
   @SuppressWarnings("deprecation")
   private String getParameterString(String characterEncoding, UIParameter param, boolean first) 
         throws UnsupportedEncodingException
   {
      Object value = param.getValue();
      String strValue = value==null ? "" : value.toString();
      String encoded = characterEncoding==null ? 
            URLEncoder.encode(strValue) : //to work around what appears to be a bug in ADF
            URLEncoder.encode(strValue, characterEncoding);
      return (first ? '?' : '&') + param.getName() + '=' + encoded;
   }
   
   public String getView()
   {
      return view;
   }
   
   public void setView(String viewId)
   {
      this.view = viewId;
   }
   
   private boolean isDisabled(FacesContext facesContext)
   {
      ValueExpression disabledValueBinding = getValueExpression("disabled");
      return disabledValueBinding==null ? 
            disabled : (Boolean) disabledValueBinding.getValue(facesContext.getELContext());
   }
   
   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      view = (String) values[1];
      pageflow = (String) values[2];
      propagation = (String) values[3];
      action = (MethodBinding) restoreAttachedState(context, values[4]);
      disabled = (Boolean) values[5];
   }
   
   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[6];
      values[0] = super.saveState(context);
      values[1] = view;
      values[2] = pageflow;
      values[3] = propagation;
      values[4] = saveAttachedState(context, action);
      values[5] = disabled;
      return values;
   }
   
   public String getPageflow()
   {
      return pageflow;
   }
   
   public String getPropagation()
   {
      return propagation;
   }
   
   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }
   public void setPropagation(String propagation)
   {
      this.propagation = propagation;
   }
   
   public MethodBinding getAction()
   {
      return action;
   }
   
   public void setAction(MethodBinding action)
   {
      this.action = action;
   }
   
   public String getFragment()
   {
      return fragment;
   }
   
   public void setFragment(String fragment)
   {
      this.fragment = fragment;
   }
   
   @Override
   public boolean isDisabled()
   {
      return disabled;
   }
   
   @Override
   public void setDisabled(boolean disabled)
   {
      this.disabled = disabled;
   }
   
   //IMPLEMENT ActionSource:
   
   public void addActionListener(ActionListener listener) { }
   
   public MethodBinding getActionListener()
   {
      return null;
   }
   
   public ActionListener[] getActionListeners()
   {
      return null;
   }
   
   public boolean isImmediate()
   {
      return false;
   }
   
   public void setImmediate(boolean immediate) { }
   
   public void removeActionListener(ActionListener listener) {}
   
   public void setActionListener(MethodBinding actionListener) {}
   
   public String getOutcome()
   {
      return outcome;
   }
   
   public void setOutcome(String outcome)
   {
      this.outcome = outcome;
   }
}
