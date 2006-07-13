<?xml version="1.0"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:h="http://java.sun.com/jsf/html"
          xmlns:f="http://java.sun.com/jsf/core"
          xmlns="http://www.w3.org/1999/xhtml"
          version="1.2">
<jsp:directive.page contentType="text/html;charset=utf-8"/>

<html>
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

</jsp:root>
