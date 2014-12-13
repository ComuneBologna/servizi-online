<%@page import="it.eng.eli4u.imieidati.utils.IMieiDatiConstants"%>
<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>

<portlet:defineObjects />
<portlet:resourceURL  var="ajaxResourceUrl"/>
<%
String portalService = "";
if(application.getAttribute("portalService")!=null){
	portalService = application.getAttribute("portalService").toString();
}
%>

<%if ("websphere".equalsIgnoreCase(portalService)) { %>
<script src="<%=request.getContextPath()%>/css/accordion.cs"></script>
<script src="<%=request.getContextPath()%>/js/jquery-1.11.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/accordion.js"></script>
<% }
%>
<div style="display: none;">
	<div id="<%=request.getAttribute(IMieiDatiConstants.PORTLET_NOME_REQUEST_ATTRIBUTE) %>_portal"><%=portalService %></div>
	<div id="<%=request.getAttribute(IMieiDatiConstants.PORTLET_NOME_REQUEST_ATTRIBUTE) %>_hidePortlet"><%=request.getAttribute(IMieiDatiConstants.IS_ADMIN_ATTRIBUTE) %></div>
	<div id="imieidati_precaching_<%=request.getAttribute(IMieiDatiConstants.PORTLET_NOME_REQUEST_ATTRIBUTE) %>" ></div>
	<div id="<%=request.getAttribute(IMieiDatiConstants.PORTLET_NOME_REQUEST_ATTRIBUTE)%>_ajax_url"><%= ajaxResourceUrl %></div>
</div>