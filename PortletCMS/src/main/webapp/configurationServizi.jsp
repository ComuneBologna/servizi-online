<%@include file="/init.jsp"%>

<%@page language="java" import="java.io.InputStream" %>
<%@page language="java" import="java.util.Properties" %>

<%	
	InputStream stream = getServletContext().getResourceAsStream("/WEB-INF/classes/portlet.properties");
	Properties prop = new Properties();
	prop.load(stream);
	
	//Set field values to stored preferences or properties values if null	
	String baseURL = GetterUtil.getString(portletPreferences.getValue("baseURL", prop.getProperty("serviziBaseHtmlUrl")));
	String type = GetterUtil.getString(portletPreferences.getValue("type", "full"));
	
	boolean fullSelected;
	if (type.equals("full")) {
		fullSelected = true;
	} else {
		fullSelected = false;
	}
	
	boolean sidebarSelected;
	if (type.equals("sidebar")) {
		sidebarSelected = true;
	} else {
		sidebarSelected = false;
	}
	
	boolean articoloSelected;
	if (type.equals("articolo")) {
		articoloSelected = true;
	} else {
		articoloSelected = false;
	}	
%>

<liferay-portlet:actionURL portletConfiguration="true"	var="configurationURL" />

<aui:form action="<%=configurationURL%>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:fieldset>
		<aui:layout>
			<aui:column>
				<aui:input name="preferences--baseURL--" type="text" value="<%=baseURL%>" />
			</aui:column>
			<aui:column>
				<aui:select label="Type" name="preferences--type--">
					<aui:option value="full" selected="<%=fullSelected%>">full</aui:option>
					<aui:option value="sidebar" selected="<%=sidebarSelected%>">sidebar</aui:option>
					<aui:option value="articolo" selected="<%=articoloSelected%>">articolo</aui:option>
				</aui:select>
			</aui:column>
			<%-- <aui:field-wrapper>
				<aui:input inlineLabel="full" name="preferences--type--" type="radio" value="full" label="full" checked="<%=fullSelected%>"/>
				<aui:input inlineLabel="sidebar" name="preferences--type--" type="radio" value="sidebar" label="sidebar" checked="<%=sidebarSelected%>"/>
				<aui:input inlineLabel="articolo" name="preferences--type--" type="radio" value="articolo" label="articolo" checked="<%=articoloSelected%>"/>
			</aui:field-wrapper> --%>
			<aui:button-row>
				<aui:button type="submit" />
			</aui:button-row>
		</aui:layout>
	</aui:fieldset>
</aui:form>