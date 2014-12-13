<%--
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
"Benvenuto <%=renderRequest.getAttribute("userName")%> " 
--%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.theme.PortletDisplay"%>
<%@page import="com.liferay.portal.theme.ThemeDisplay"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<script type="text/javascript">
function toTitleCase(str) {
	str = str.toLowerCase();
    return str.replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
    });
}

var nomeCamelized = toTitleCase("Ciao <%=renderRequest.getAttribute("userName")%>");
$("[id^=portlet_PortletCMSBenvenuto_WAR] h2").text(nomeCamelized);
</script>

<%=renderRequest.getAttribute("drupalBody") %>