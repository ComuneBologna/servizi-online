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

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<portlet:resourceURL var="ajaxResourceUrl" />

<script type="text/javascript">	
function sizing() {
	$('#calendar-small').fullCalendar('render');
}

$(document).ready(sizing);

$(document).ready(function(){
	//Setup calendar
	$('#calendar-small').fullCalendar({
		//Get events for calendar
	    events: function(start, end, timezone, callback) {	    	
	    	var url = '<%=ajaxResourceUrl%>';

	    	//Build data
			var startFormatted = start.format("YYYY-MM-DD");
			var endFormatted = end.format("YYYY-MM-DD");

			//Ajax call to server with previous url and data
	        $.ajax({
	            url: url,
	            dataType: 'xml',
	            data: {
	                start: startFormatted,
	                end: endFormatted
	            },
	            success: function(doc) {					
	                var events = [];
	                $(doc).find('event').each(function() {	
	                    events.push({
	                    	title: $(this).attr('title'),
	                        start: $(this).attr('start')
	                    });
	                });
	                callback(events);
	            }
	        });
	    },
	    lang: 'it',
	    header: {
	      left: '',
	      center: 'title',
	      right: ''
	    }
	});
});
</script>

<%=request.getAttribute("agendaFrontPage")%>