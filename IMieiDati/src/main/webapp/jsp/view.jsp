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

if("liferay".equalsIgnoreCase(portalService)) { %>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortlet"%>
<%@page import="com.liferay.portal.security.ldap.PortalToLDAPConverter"%>
<% }
%>

<%	
	String currentPortletName = "";

	if ("liferay".equalsIgnoreCase(portalService)) { %>
		<%ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute("THEME_DISPLAY"); 
			if(themeDisplay.getPortletDisplay().getPortletName() !=null){
 				currentPortletName =  themeDisplay.getPortletDisplay().getPortletName();
			} %>
	<% }
%>
<%  
	PortletPreferences prefs = renderRequest.getPreferences();
	String sectionsToHide = "";
	if(prefs!=null){
		sectionsToHide = (String) prefs.getValue("sectionsToHide_" + currentPortletName, "");
	}
%>

<%if ("websphere".equalsIgnoreCase(portalService)) { %>
<script src="<%=request.getContextPath()%>/css/accordion.cs"></script>
<script src="<%=request.getContextPath()%>/js/jquery-1.11.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/accordion.js"></script>
<% }
%>

<%-- Save the list of checkbox values in an hidden div --%>
<div id="<%=currentPortletName%>_sezioni_nascoste" style="display: none;"><%= sectionsToHide %></div>
<div id="<%=currentPortletName%>_ajax_url" style="display: none;"><%= ajaxResourceUrl %></div>
<%-- Save the context in an hidden div --%>
<div id="contesto_imieidati" style="display: none;"><%=request.getContextPath()%></div>

<%-- Create Accordion --%>
<!-- 
<div id="singlePortlet_<%= currentPortletName%>">
	<div id="<%= currentPortletName%>_accordion">
		<div class="level" id="<%= currentPortletName%>_accordionContent">
		</div>
	</div>
</div>
-->

<div id="singlePortlet_<%= currentPortletName%>" class="imieidati-portlet">
    <div id="<%= currentPortletName%>__main-portlet-body">

<!-- START SECTIONS GENERATION ZONE -->


<!-- END SECTIONS GENERATION ZONE -->

  </div>  <!-- /main-content -->
</div> <!-- /container -->

</body>
</html>