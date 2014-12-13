<%@include file="/init.jsp"%>

<%@page language="java" import="java.io.InputStream" %>
<%@page language="java" import="java.util.Properties" %>

<%	
	InputStream stream = getServletContext().getResourceAsStream("/WEB-INF/classes/portlet.properties");
	Properties prop = new Properties();
	prop.load(stream);
	
	//Set field values to stored preferences or properties values if null	
	String baseURL = GetterUtil.getString(portletPreferences.getValue("baseURL", prop.getProperty("baseHtmlUrl")));
	String count = GetterUtil.getString(portletPreferences.getValue("count", prop.getProperty("count")));
	String category = GetterUtil.getString(portletPreferences.getValue("category", prop.getProperty("category")));
	String summary = GetterUtil.getString(portletPreferences.getValue("summary", prop.getProperty("summary")));
	String list = GetterUtil.getString(portletPreferences.getValue("list", prop.getProperty("list")));
	
	boolean summarySelected;
	if (summary.equals("true")) {
		summarySelected = false;
	} else {
		summarySelected = true;
	}
	
	boolean listSelected;
	if (list.equals("true")) {
		listSelected = false;
	} else {
		listSelected = true;
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
				<aui:input name="preferences--count--" type="text" value="<%=count%>" />
			</aui:column>
			<aui:column>
				<aui:input name="preferences--category--" type="text" value="<%=category%>" />
			</aui:column>
			<aui:column>
				<aui:select label="Summary" name="preferences--summary--">
					<aui:option value="true" selected="<%=summarySelected%>">true</aui:option>
					<aui:option value="false" selected="<%=summarySelected%>">false</aui:option>
				</aui:select>
				<%-- <aui:input name="preferences--summary--" type="text" value="<%=summary%>" /> --%>
			</aui:column>
			<aui:column>
				<aui:select label="List" name="preferences--list--">
					<aui:option value="true" selected="<%=listSelected%>">true</aui:option>
					<aui:option value="false" selected="<%=listSelected%>">false</aui:option>
				</aui:select>
				<%-- <aui:input name="preferences--list--" type="text" value="<%=list%>" /> --%>
			</aui:column>
			<aui:button-row>
				<aui:button type="submit" />
			</aui:button-row>
		</aui:layout>
	</aui:fieldset>
</aui:form>