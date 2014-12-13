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
--%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<script>
	$(document).ready(function(){
		if ($("portlet-cms-style").length == 0) {
			$('head').append('<%=renderRequest.getAttribute("drupalCSS")%>');
		}
	});
</script>

<section class="portlet">
	<div class="panel panel-default">
		<div class="panel-body">
			<%=renderRequest.getAttribute("body")%>
		</div>
	</div>
</section>