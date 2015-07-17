<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/iscTaglib.xml" prefix="isomorphic" %>
<html>
<head>
  <title>Customer Care Web User Interface</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="gwt:property" content="locale=ka">
  <link type="text/css" rel="stylesheet" href="css/TicketMaster.css"/>
  <link type="image/x-icon" href="images/favicon.ico" rel="shortcut icon"/>

  <script>
    var isomorphicDir = "ticketmaster/sc/"
  </script>
  <script>
    window.isomorphicDir = "ticketmaster/sc/"
    window.isc_expirationOff = true;
  </script>

  <isomorphic:loadISC skin="EnterpriseBlue" modulesDir="modules/"
                      includeModules="Core,Foundation,Containers,Grids,Forms,DataBinding,Tools"/>

  <script type="text/javascript" src="ticketmaster/ticketmaster.nocache.js"></script>
  <script type="text/javascript" src="sc/DataSourceLoader?dataSource=UserManagerDS"></script>

</head>
<body>
<iframe src="javascript:''" id="__gwt_historyFrame"
        style="width: 0; height: 0; border: 0"></iframe>
</body>
</html>

