<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.*" %>
<%@ page import="java.net.InterfaceAddress" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.PrintWriter" %>
<%--
  Created by IntelliJ IDEA.
  User: paata
  Date: 6/25/15
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%=application.getRealPath("/")%>
<%=InetAddress.getLocalHost().getHostAddress()%>


<%
  try {
    Enumeration<NetworkInterface> e1=NetworkInterface.getNetworkInterfaces();
    while (e1.hasMoreElements()){
      NetworkInterface ifc=e1.nextElement();
      if(ifc.isUp()) {
        Enumeration<InetAddress> e=ifc.getInetAddresses();
        while (e.hasMoreElements()){
          out.println("Addr="+e.nextElement().getHostAddress());
        }
      }
    }
  }catch (Exception e){
    PrintWriter pw=new PrintWriter(out);
    e.printStackTrace(pw);
  }






%>
</body>
</html>
