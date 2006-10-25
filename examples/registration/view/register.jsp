<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns:s="http://jboss.com/products/seam/taglib"
          xmlns="http://www.w3.org/1999/xhtml"
          version="2.0">
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
  <jsp:directive.page contentType="text/html"/>
  <head>
    <title>Register New User</title>
  </head>
  <body>
    <f:view>
      <h:form>
        <table border="0">
          <s:validateAll>
            <tr>
              <td>Username</td>
              <td><h:inputText value="#{user.username}" required="true"/></td>
            </tr>
            <tr>
              <td>Real Name</td>
              <td><h:inputText value="#{user.name}" required="true"/></td>
            </tr>
            <tr>
              <td>Password</td>
              <td><h:inputSecret value="#{user.password}" required="true"/></td>
            </tr>
          </s:validateAll>
        </table>
        <h:messages/>
        <h:commandButton type="submit" value="Register" action="#{register.register}"/>
      </h:form>
    </f:view>
  </body>
</jsp:root>
