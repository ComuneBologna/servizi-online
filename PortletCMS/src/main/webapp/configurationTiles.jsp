<%@include file="/init.jsp"%>

<%@page language="java" import="java.io.InputStream" %>
<%@page language="java" import="java.util.Properties" %>

<%	
	InputStream stream = getServletContext().getResourceAsStream("/WEB-INF/classes/portlet.properties");
	Properties prop = new Properties();
	prop.load(stream);
	
	//Set field values to stored preferences or properties values if null	
	String baseURL = GetterUtil.getString(portletPreferences.getValue("baseURL", prop.getProperty("tilesBaseHtmlUrl")));
	String tileId = GetterUtil.getString(portletPreferences.getValue("tileId", ""));
	String idWeak = GetterUtil.getString(portletPreferences.getValue("idWeak", ""));
	String idStrong = GetterUtil.getString(portletPreferences.getValue("idStrong", ""));
	String targetId = GetterUtil.getString(portletPreferences.getValue("targetId", ""));
	String targetPage = GetterUtil.getString(portletPreferences.getValue("targetPage", ""));
	
%>



<liferay-portlet:actionURL portletConfiguration="true"	var="configurationURL" />

<aui:form action="<%=configurationURL%>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:fieldset>
		<aui:layout>
			<aui:column>
				<aui:input name="preferences--baseURL--" type="text" value="<%=baseURL%>" />
			</aui:column>
			<div id="baseDiv">
				<aui:column>
					<aui:input name="preferences--tileId--" type="text" value="<%=tileId%>" />
				</aui:column>
				<aui:column>
					<aui:input name="preferences--targetPage--" type="text" value="<%=targetPage%>" />
				</aui:column>
				<aui:column>
					<aui:input name="preferences--targetId--" type="text" value="<%=targetId%>" />
				</aui:column>									
			</div>
			<div id="profiledTileDiv">
				<aui:column>
					<aui:input name="preferences--idWeak--" type="text" value="<%=idWeak%>"  />
				</aui:column>	
				<aui:column>
					<aui:input name="preferences--idStrong--" type="text" value="<%=idStrong%>" />
				</aui:column>
			</div>								
			<aui:button-row>
				<aui:button type="submit" />
			</aui:button-row>
		</aui:layout>
	</aui:fieldset>
</aui:form>