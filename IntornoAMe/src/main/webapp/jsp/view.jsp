<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />
 
<portlet:resourceURL  var="ajaxResourceUrl" escapeXml="false" />

<%=request.getAttribute("visual")%>
    
<script>

var centroCitta=new OpenLayers.LonLat('<%=request.getAttribute("longitudineCentroCitta")%>', '<%=request.getAttribute("latitudineCentroCitta")%>');
var distanzaKmDalCentroCitta='<%=request.getAttribute("distanzaKmDalCentroCitta")%>';
var urlRicercaLuogo='<%=request.getAttribute("urlRicercaLuogo")%>';
var coordinatePoligonoRicerca='<%=request.getAttribute("coordinatePoligonoRicerca")%>';

/*
 * Definizione della funzione trim che manca in IE8. Se il browser non la supporta questa funzione la definisce
 */
if(typeof String.prototype.trim !== 'function') {
	  String.prototype.trim = function() {
	    return this.replace(/^\s+|\s+$/g, '');
	  }
}
	
function inizializzaCheckbox(elementi, categorieKmlJSON) {
	var contaCategorie=0;
	elementi.each(function( index ) {
		var valoreId=$( this ).prop('id');
		$( this ).data("Data", categorieKmlJSON[contaCategorie]);  
		// Inserisce il listener per l'evento click del singolo checkbox
		$( this ).click(function() {
			eliminaTuttiPopup();
			var statoCheckbox=$(this).is(':checked');
			$.each($(this).data("Data").kmls, function(ind4, kml) {
				kml.layer.setVisibility(statoCheckbox);
			});
		});
		contaCategorie++;
	});
};

/*
 * Cancella tutti i popup eventualmente aperti sulla mappa
 */
function eliminaTuttiPopup() {
	var pops = map.popups;
	for(var a = 0; a < pops.length; a++){
		map.removePopup(map.popups[a]);
	};		
}
 
$(document).ready(function() {
	
	var categorieKmlJSON=JSON.parse('<%=request.getAttribute("categorieKmlJSON")%>');
	var elementi = $("#listaCheckboxCategorie1 li div input");
	inizializzaCheckbox(elementi, categorieKmlJSON);
	elementi = $("#listaCheckboxCategorie2 li div input");
	inizializzaCheckbox(elementi, categorieKmlJSON);

	var centerPoint=null;
	var defaultIconMap='<%=request.getAttribute("defaultIconMap")%>';
	var utenteLoggato='<%=request.getAttribute("utenteLoggato")%>';
	var codiceFiscale='<%=request.getAttribute("codiceFiscale")%>';
	var tipoAccount='<%=request.getAttribute("tipoAccount")%>';

	// Se non è un utente Federa oppure non è loggato

	/* LOGICA DI POSIZIONAMENTO */
	if (utenteLoggato==null||codiceFiscale==null||tipoAccount!='Federa') {
		centerPoint=centroCitta;
		init(null, centerPoint.lon+', '+centerPoint.lat, '<%=ajaxResourceUrl%>',categorieKmlJSON,'<%=request.getContextPath()%>',14, defaultIconMap);		
		getGeoLocation();
	} else {
		var indirizzo='<%=request.getAttribute("indirizzoResidenza")%>';
		if (indirizzo=='null') {	
			log('nessun indirizzo trovato.');
			centerPoint=centroCitta;
			init(null, centerPoint.lon+', '+centerPoint.lat, '<%=ajaxResourceUrl%>',categorieKmlJSON,'<%=request.getContextPath()%>',14, defaultIconMap);		
			getGeoLocation();
			
		} else {
			log('Utente ACSOR con indirizzo di residenza: '+indirizzo);

			/*
			* Questa parte era nella funziona cercaLocalita() che e' stata spostata qui per 
			* risolvere il problema della ricerca asincrona per IE8 e Chrome
			*/
			var valIndirizzo = '?format=json&limit=10&q='+parsaIndirizzo(indirizzo);
			log('URL ricerca: '+urlRicercaLuogo + valIndirizzo);
			// IE8
			$.support.cors = true;

			$.ajax({
				type: "GET",
				url: urlRicercaLuogo + valIndirizzo,
				async: true,
				dataType: "json",
				contentType: 'text/plain',
				success: function(data){
					// Indirizzo non trovato: centro Citta'!
					if (data==null||data=='') {
						log("Indirizzo non trovato, selezionato il centro citta'");
						centerPoint=centroCitta;
					} else {
						log("Occorrenze trovate a partire dall'indirizzo ACSOR: "+data.length);
						// Risultati multipli: seleziona il centro Citta'!
//						if (data.length>1) {
//							centerPoint=centroCitta;
//						} else {
							centerPoint= new OpenLayers.LonLat(data[0].lon, data[0].lat);
							log('distanza in km: ' + OpenLayers.Util.distVincenty(centroCitta, centerPoint)+ ' distanza configurata km:'+distanzaKmDalCentroCitta);
							// Indirizzo individuato e univoco: se troppo distante dal centro Citta' restituisce il centro Citta'
							var distanzaIndirizzo=OpenLayers.Util.distVincenty(centroCitta, centerPoint);
							if (distanzaIndirizzo > distanzaKmDalCentroCitta) {
								log('Indirizzo troppo distante a '+distanzaIndirizzo+ ' Km dal centro.');
								centerPoint=centroCitta;
							}
//						}
					}
					init(null, centerPoint.lon+', '+centerPoint.lat, '<%=ajaxResourceUrl%>',categorieKmlJSON,'<%=request.getContextPath()%>',14, defaultIconMap);		
					getGeoLocation();
			
				},
				error: function(jqXHR, textStatus, errorThrown ) {
					log('ERRORE--'+textStatus);
					centerPoint=centroCitta;
					init(null, centerPoint.lon+', '+centerPoint.lat, '<%=ajaxResourceUrl%>',categorieKmlJSON,'<%=request.getContextPath()%>',14, defaultIconMap);		
					getGeoLocation();
				}
			});
//			centerPoint=cercaLocalita(indirizzo,distanzaKmDalCentroCitta);
			
		}
	}
	
//	init(null, centerPoint.lon+', '+centerPoint.lat, '<%=ajaxResourceUrl%>',categorieKmlJSON,'<%=request.getContextPath()%>',14, defaultIconMap);		

	$('#esplora-search').autocomplete({
		source: function( request, response ) { 
			// IE8
			$.support.cors = true;
			
			var valore=$('#esplora-search').val();	
			if (valore.trim()!='') {
				$.ajax({
					type : "GET",
					url : urlRicercaLuogo+'?format=json&bounded=1&viewbox='+ coordinatePoligonoRicerca +'&limit=10&street='+valore,
					async : true,
					cache : false,
					dataType : "json",
					contentType: 'text/plain',
					/*
					beforeSend: function(xhr){
								xhr.setRequestHeader('Origin', 'nominatim.openstreetmap.org');
							},	
					*/		
					success : function (data) {
						if (data == null || data == '') {
							log('Indirizzo NON TROVATO con:'+valore);
							// Fa sparire il menu altrimenti col messaggio di errore si sposta sopra il campo di ricerca
							$("#esplora-search").autocomplete("close");
							$('#msgInfo').html('Indirizzo non trovato.').show();	
						} else {
							log('Indirizzi trovati.');
							$('#msgInfo').html('').hide();	
							$('#msgErrore').html('').hide();	
							response($.map(data, function (item) {
								return {
									label: item.display_name,
									value: item.display_name,
									lon: item.lon,
									lat: item.lat
								};
							}));
							$('#esplora-search').keydown().focus();
						}
					},
					error : function (jqXHR, textStatus, errorThrown) {
						// Fa sparire il menu altrimenti col messaggio di errore si sposta sopra il campo di ricerca
						$("#esplora-search").autocomplete("close");
						$('#msgErrore').html('Servizio di ricerca momentaneamente non disponibile').show();	
						log('Errore: '+textStatus+'-'+errorThrown);
					}
				});
			} // Fine IF 
		},
		minLength: 5,
		delay: 800,
		select : function (event, ui) {
			log('SELECT');
			$('#msgInfo').html('').hide();	
			$('#msgErrore').html('').hide();
			// Sbianca l'eventuale Dettaglio POI visualizzato
			$('#dettaglio-poi').html('');
			eliminaTuttiPopup();

			var centerPoint = new OpenLayers.LonLat(ui.item.lon, ui.item.lat).transform(fromProjection, toProjection);
			map.setCenter(centerPoint, 16); // zoom
			spostaMarkerTuSeiQui(centerPoint);			
		},
		autoFocus: false,
		// Disabilita l'aggiornamento del campo di input quando l'utente si sposta con le frecce
		// tra gli item del dropdown
		focus: function (event, ui) {
			event.preventDefault();
		}
	});

	// Pulisce eventuali messaggi di non trovato o errore appena l'utente pulisce il campo
	$('#esplora-search').on("keydown",function() {
		$('#msgInfo').html('').hide();	
		$('#msgErrore').html('').hide();	
	});	 
	
//	getGeoLocation();
	
});

</script>								
         