<?xml version="1.0"?>
<html xmlns:jsp="http://java.sun.com/JSP/Page" 
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns="http://www.w3.org/1999/xhtml">
  <jsp:output doctype-root-element="html"
              doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
              doctype-system="http://www.w3c.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"/>
  <jsp:directive.page contentType="text/html"/>
  <head>
    <title>Successfully Registered New User</title>
  </head>
  <body>
    <f:view>
      Welcome, <h:outputText value="#{user.name}"/>, 
      you are successfully registered as <h:outputText value="#{user.username}"/>.
    </f:view>
  </body>
</html>
