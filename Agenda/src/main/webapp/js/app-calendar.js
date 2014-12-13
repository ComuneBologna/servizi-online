function calendar_reset_categories() {
	$('#nav-calendar-category li').removeClass('active');
	$('.fc-event').show();
}

function calendar_hide_events() {
	$('.fc-event').hide();
}

function sizing() {
	// refresh del calendario al ridimensionamento
	$('#calendar').fullCalendar('render');
}
$(document).ready(sizing);
$(window).resize(sizing);

$(document).ready(function() {
	
	var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();
		
		var localOptions = {
			lang: 'it',
			timeFormat: 'H:mm',
			firstDay: 1,
			buttonText: {
				today: 'Oggi',
				month: 'Mese',
				day: 'Giorno',
				week: 'Settimana'
			},
			monthNames: ['Gennaio','Febbraio','Marzo','Aprile','Maggio','Giugno','Luglio','Agosto','Settembre','Ottobre','Novembre','Dicembre'],
			monthNamesShort: ['Gen','Feb','Mar','Apr','Mag','Giu','Lug','Ago','Set','Ott','Nov','Dec'],
			dayNames: ['Domenica','Lunedì','Martedì','Mercoledì','Giovedì','Venerdì','Sabato'],
			dayNamesShort: ['Do','Lu','Ma','Me','Gi','Ve','Sa'],
			allDayText: 'Tutto il giorno',
			axisFormat: 'H:mm',
			
			events: [
				{
					title: 'Evento di una giornata',
					start: new Date(y, m, 1),
					className: 'categoria-3',
					url: '#calendario-evento-1'
				},
				{
					title: 'Evento esteso per più giorni',
					start: new Date(y, m, d-5),
					end: new Date(y, m, d-2),
					className: 'categoria-2',
					url: '#calendario-evento-1'
				},
				{
					id: 999,
					title: 'Evento ripetuto',
					start: new Date(y, m, d-3, 16, 0),
					allDay: false,
					className: 'categoria-2',
					url: '#calendario-evento-1'
				},
				{
					id: 999,
					title: 'Evento ripetuto',
					start: new Date(y, m, d+4, 16, 0),
					allDay: false,
					className: 'categoria-2',
					url: '#calendario-evento-1'
				},
				{
					title: 'Incontro',
					start: new Date(y, m, d, 10, 30),
					allDay: false,
					className: 'categoria-1',
					url: '#calendario-evento-1'
				},
				{
					title: 'Pranzo',
					start: new Date(y, m, d, 12, 0),
					end: new Date(y, m, d, 14, 0),
					allDay: false,
					className: 'categoria-3',
					url: '#calendario-evento-1'
				},
				{
					title: 'Anniversario',
					start: new Date(y, m, d+1, 19, 0),
					end: new Date(y, m, d+1, 22, 30),
					allDay: false,
					className: 'categoria-1',
					url: '#calendario-evento-1'
				},
				{
					title: 'Un altro evento',
					start: new Date(y, m, 28),
					end: new Date(y, m, 29),
					className: 'news',
					url: '#calendario-evento-1'
				}
			]
		}	
		$('#calendar').fullCalendar($.extend({
			
		  viewRender:	function() {
		  	calendar_reset_categories();
		  },
		  
		  header: {
	      left: 'prev,title,next',
	      center: 'today',
	      right: 'month,basicWeek,basicDay'
	    },
	    
	    editable: true,
	    
	    eventClick: function(event, element, view ) { 
	      $('a',$(element)).magnificPopup({
	        type:							'inline',
	        midClick:					true,
	        fixedContentPos:	false,
	        closeBtnInside:		true
	      });
	    }
	
	  }, localOptions));
	  
	  $('#calendar-small').fullCalendar($.extend({
	  		
  	  viewRender:	function() {
  	  	calendar_reset_categories();
  	  },
  	  
  	  header: {
        left: 'prev',
        center: 'title',
        right: 'next',
      },
      
      editable: true,
      
      eventClick: function(event, element, view ) { 
        $('a',$(element)).magnificPopup({
          type:							'inline',
          midClick:					true,
          fixedContentPos:	false,
          closeBtnInside:		true
        });
      }
  
    }, localOptions));
 	
});




