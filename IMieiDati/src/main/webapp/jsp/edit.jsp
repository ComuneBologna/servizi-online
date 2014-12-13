<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>


<portlet:defineObjects />
<%
String portalService = "";
if(application.getAttribute("portalService")!=null){
	portalService = application.getAttribute("portalService").toString();
}

if("liferay".equalsIgnoreCase(portalService)) { %>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortlet"%>
<%@page import="com.liferay.portal.security.ldap.PortalToLDAPConverter"%>
<% }
%>


<%
	String currentPortletName = "";
	
	if ("liferay".equalsIgnoreCase(portalService)) {
%>
<%
	ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute("THEME_DISPLAY");
		if (themeDisplay.getPortletDisplay().getPortletName() != null) {
			currentPortletName = themeDisplay.getPortletDisplay().getPortletName();
		}
%>
<%
	}
%>

<%
	PortletPreferences prefs = renderRequest.getPreferences();

	String sectionsToHide = "";

	if (prefs != null) {
		sectionsToHide = (String) prefs.getValue("sectionsToHide_" + currentPortletName, "");
	}
%>

<%
	if ("websphere".equalsIgnoreCase(portalService)) {
%>
<script src="<%=request.getContextPath()%>/css/userConfig.cs"></script>
<script src="<%=request.getContextPath()%>/js/jquery-1.11.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/userConfig.js"></script>
<%
	}
%>

<%-- Save the list of checkbox values in an hidden div --%>
<div id="<%=currentPortletName%>_sezioni_nascoste" style="display: none;"><%=sectionsToHide%></div>

<%-- Save the context in an hidden div --%>
<div id="contesto_imieidati" style="display: none;"><%=request.getContextPath()%></div>

<%-- Create List --%>
<div id="singlePortlet_<%=currentPortletName%>">
	<div class="checkboxList"></div>
	<form id="<%=currentPortletName%>_userConfigForm"
		action="<portlet:actionURL/>" method="POST" class="sectionListForm">

		<h3>Lista delle Sezioni:</h3>
		<h5>Selezionare le sezioni che si desiderano visualizzare o
			nascondere.</h5>
		<br>
	</form>
</div>
</div>
</body>
</html>