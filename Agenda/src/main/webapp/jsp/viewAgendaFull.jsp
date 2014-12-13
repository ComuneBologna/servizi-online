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

<portlet:resourceURL var="ajaxResourceUrl" />

<script type="text/javascript">	

function log(log_txt) {
//	alert('window.console='+(typeof window.console));
    if (typeof window.console != 'undefined') {
        console.log(log_txt);
    }
};

$(document).ready(function(){

	// Gestione dei preferiti
    $( "body" ).on( "click", "#preferiti", function() {
		var idAgenda=$(this).parent().children('#idAgenda').val();
    	var url = '<%=ajaxResourceUrl%>';
    	var classe=$(this).attr('class');
    	var flag=null;
    	if (classe=='pref') {
    		flag='S';
    	} else {
    		flag='N';
    	}
		
    	$.ajax({
    		type: "POST",
    		context : this,
			url : url,
			async : true,
			dataType : "json",
            data: {
                idAgenda : idAgenda,
                aggiungiPreferiti : flag
            },
			success : function (data) {
				log('JSON: '+JSON.stringify(data));
				$(this).toggleClass('down');
				// Refetcha tutti gli eventi
				$('#calendar').fullCalendar('refetchEvents');	
			},
			error : function (jqXHR, textStatus, errorThrown) {
				log('Errore: '+textStatus+'-'+errorThrown);
			}
		});
	});
/*
	$( "body" ).on( "click", "a.pref#preferiti", function() {
        $(this).toggleClass("down");
    });
*/    
	// Fine gestione dei preferiti
	
	//Handles category checkbox click event
	$(":checkbox").click(function(event) {
		//Process click if there is at least one checked.
		if ($(":checkbox:checked").length > 0) {
			//Set checked attribute accordingly
			if (this.checked) {
		       $(this).attr('checked', 'checked');
		    } else {
		       $(this).removeAttr('checked');
		    }
		    //Refresh calendar and list
		    $('#calendar').fullCalendar('refetchEvents');	
   		    updateElenco();
		} 
		//Prevent click event if only one checked 
		else {
			event.preventDefault();
		}    
	});	

	$(".tab-header a").click(function() {
		$('#calendar').fullCalendar('refetchEvents');
	});
	
	//Handles scrolling to full detail event when event is clicked on calendar
	$( "body" ).on( "click", "a.fc-event", function() {
		var jumpTo = "#event-" + $(this).attr('id');
	    $('html, body').animate({
	        scrollTop: $(jumpTo).offset().top
	    }, 1000);
	});

	//Setup calendar
	$('#calendar').fullCalendar({
		//Get events for calendar
	    events: function(start, end, timezone, callback) {	
		    //Build url    	
	    	var url = '<%=ajaxResourceUrl%>';
			var parameter = "&dateRange=true";
			var finalUrl = url + parameter;

			//Build data
			var startFormatted = start.format("YYYY-MM-DD");
			var endFormatted = end.format("YYYY-MM-DD");
			var checkBoxesChecked = getCategoryString();

			//Ajax call to server with previous url and data
	        $.ajax({
	            url: finalUrl,
	            dataType: 'xml',
	            data: {
	                start: startFormatted,
	                end: endFormatted,
	                checkBoxesChecked : checkBoxesChecked
	            },
	            success: function(doc) {					
	                var events = [];
	                var previousStart = '';
	                var count = 1;
	                var limit;
	                var view = $('#calendar').fullCalendar( 'getView' );

	                //Determine the maximum number of events to show based on the view
	                if (view.name == 'month') {
	                	limit = 2;
					} else if (view.name == 'basicWeek') {
						limit = 3;
					}
	                
	                //Just shows the first two events for any given day
	                $(doc).find('event').each(function() {
		                if (previousStart == $(this).attr('start')) {
							count++;
			            } else {
							count = 1;
					    }												
						
					    if (count < limit) {
					    	events.push({
		                    	title: $(this).attr('title'),
		                        start: $(this).attr('start'),
		                        borderColor: $(this).attr('color'),
		                        id: $(this).attr('id')
		                    });
						}	                    
	                    previousStart = $(this).attr('start');
	                });
	                callback(events);
	            }
	        });
	    },
	    //Add id attribute to each event for later use
	    eventRender: function(event, element) {
	        element.attr("id", event.id);
	    },
	    //Refetch events when view type changes (week or month). 
	    //This is for resetting the limit of events to show per day.
	    viewRender: function( view, element ) {
	    	$('#calendar').fullCalendar( 'refetchEvents' );	    	
		},
	    //Fetch associated calendar list
	    eventAfterAllRender: function( view ) {
		    //Build url
	    	var url = '<%=ajaxResourceUrl%>';
			var parameter = "&calendarioElenco=true";
			var finalUrl = url + parameter;

			//Build data
			var startFormatted = view.start.format("YYYY-MM-DD");
			var endFormatted = view.end.format("YYYY-MM-DD");
			var checkBoxesChecked = getCategoryString();

			//Ajax call to server with previous url and data
	        $.ajax({
	        	type : "POST",
	            url: finalUrl,
	            dataType: 'text',
	            data: {
	                start: startFormatted,
	                end: endFormatted,
	                checkBoxesChecked : checkBoxesChecked
	            },
	            success: function(data) {
		            $("#tab-calendario ul.eventi").remove();					
	            	$("#tab-calendario .button-container-right").after(data);

	            	// Sets event background colour if in mobile display
	    	     	// Moves eventi preferiti if in mobile display
	    	        if ($( window ).width() < 768) {
	    	    		$(".fc-event-container").each(function() {
	    	    			var bg_color = $(this).find("a").css("border-color");
	    	    			$(this).css("background-color", bg_color);
	    	    		});
	    	    		$(".prefLabel").each(function() {	
	    	    			var k = $(this).parents(".evento-giorno").children(".evento-contenuto");
	    	    			$(this).detach().insertAfter(k);
	    	    			$(this).css("margin-top", "10px");
	    	    		});
	    	        }
	            }
	        });	     	
		},
	    lang: 'it',
	    header: {
	      left: 'prev,title,next',
	      center: '',
	      right: 'basicWeek,month'
	    }
	});

	//Handles functionality to download calendar in iCal format
	$( ".btn-secondary" ).click(function(event) {
		event.preventDefault();
		
		//Build url		
		var url = '<%=ajaxResourceUrl%>';
		var parameter = "&download=true";
		var finalUrl = url + parameter;

		//Build data
		var today = new Date();
		var params = {"start": today};

		//Ajax call to server with previous url and data
		$.ajax({
		    type : "POST",
		    url : finalUrl,
		    data: params,
		    success : function(response, status, request) 
		    {
		    	var disp = request.getResponseHeader('Content-Disposition');
		        if (disp && disp.search('attachment') != -1) {
		            var form = $('<form method="POST" action="' + finalUrl + '">');
		            $.each(params, function(k, v) {
		                form.append($('<input type="hidden" name="' + k +
		                        '" value="' + v + '">'));
		            });
		            $('body').append(form);
		            form.submit();
		        }		    	
		    }
	  	});
	});

	//Handles mostra piu functionality for list
	$( ".call-to-action a" ).click(function(event) {
		//Prevents default form submit event
		event.preventDefault();

		//Build url
		var url = '<%=ajaxResourceUrl%>';
		var parameter = "&mostraPiu=true";
		var finalUrl = url + parameter;

		//Ajax call to server with previous url and data
		$.ajax({
		    type : "POST",
		    url : finalUrl,
		    cache:false,
		    dataType: "text",
		    success : function(data) 
		    {
			    if (data === "") {
			    	$(".call-to-action").hide();
				} else {
					var i = data.toLowerCase().indexOf("</li>");
					if (i > 0 && i <= 16) {
						$(".evento").last().after(data);
					} else {
						var arr = data.split(/<li class="row evento categoria-\d">/);
						$(".evento-dettaglio").last().after(arr[0]);
						$.each(arr, function( index, value ) {
						  	if (index != 0) {
						  		$(".evento").last().after('<li class="row evento categoria-1">' + value);
							}
						});
					}					
				}
		    	
		    }
	  	});
	});	

	//Function to update the elenco when a category changes
	function updateElenco() {
		//Build url
		var url = '<%=ajaxResourceUrl%>';
		var parameter = "&filterElencoByCategory=true";
		var finalUrl = url + parameter;

		//Build data
		var startFormatted = moment().format("YYYY-MM-DD");
		var checkBoxesChecked = getCategoryString();

		//Ajax call to server with previous url and data
	    $.ajax({
            url: finalUrl,
            dataType: 'text',
            data: {
            	start: startFormatted,
                end: null,
                checkBoxesChecked : checkBoxesChecked
            },
            success: function(data) {					
            	$("#tab-elenco ul.eventi").replaceWith(data);
            	$(".call-to-action").show();
            }
        });
	}

	//Function to build a string of categories selected for filtering events
	function getCategoryString () {
		var checkBoxesChecked = "";

		$(":checkbox").each(function() {
			if (this.checked) {
				checkBoxesChecked += $(this).attr('id') + ",";
			}				  
		});

		return checkBoxesChecked.substring(0, checkBoxesChecked.length - 1);
	}

	// Displays categories tile if in mobile display
	if ($( window ).width() < 768) {
		var children = $(".main-container").children(".main-content");
		if (children.length > 0) {
			$(".hidden-xs").attr("style", "display: block !important");
		}
		$(".prefLabel").each(function() {	
			var k = $(this).parents(".evento-giorno").children(".evento-contenuto");
			$(this).detach().insertAfter(k);
			$(this).css("margin-top", "10px");
		});
	}

	//Add and style the "Visualizza per:" label
	$(".fc-center").text("Visualizza per:");
	$(".fc-center").css("float","right");
	$(".fc-center").css("font-size","1.3em");
	$(".fc-center").css("margin-top","0.3em");

});

</script>

<%=request.getAttribute("events")%>

