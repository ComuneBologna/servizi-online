//var featureSelected;
var cookieName='disabled_layers';
var separator='@!';
var map;
var disableEventChangeLayer=false;
var fromProjection = new OpenLayers.Projection("EPSG:4326");
var toProjection = new OpenLayers.Projection("EPSG:900913");
var pathToImage=null;

//var icon = new OpenLayers.Icon('https://maps.google.com/mapfiles/arrow.png');

// Funzione di logging
log = function (log_txt) {
//	alert('window.console='+(typeof window.console));
    if (typeof window.console != 'undefined') {
        console.log(log_txt);
    }
};

/* GESTIONE DEL COOKIE */
function mapLayerChanged(event) {
	if(disableEventChangeLayer)
		return;
	var alreadyPresent=false;
	var newValue='';
	var numDays=2;
	var layerCookie=getCookie(cookieName);
	if (layerCookie==null){
		setCookie(cookieName, event.layer.name, numDays);
	}
	else{
		var splitted=layerCookie.split(separator);
		if(splitted!=null)
			{
				for (var i =0;i<splitted.length;i++)
					{
					if(splitted[i]==event.layer.name)
						{
							alreadyPresent=true;
							continue;
						}
					else
						{
							if(newValue.length>0)
								newValue+=separator;
							newValue+=splitted[i]; 
						}
						
					}
			}
		if(!alreadyPresent)
			{
			if(newValue.length>0)
				newValue+=separator;
			newValue+=event.layer.name;
			}
		setCookie(cookieName, newValue, numDays);
	}
	
}
function getCookie(c_name)
{
	var c_value = document.cookie;
	var c_start = c_value.indexOf(" " + c_name + "=");
	if (c_start == -1)
	  {
	  c_start = c_value.indexOf(c_name + "=");
	  }
	if (c_start == -1)
	  {
	  c_value = null;
	  }
	else
	  {
	  c_start = c_value.indexOf("=", c_start) + 1;
	  var c_end = c_value.indexOf(";", c_start);
	  if (c_end == -1)
	  {
	c_end = c_value.length;
	}
	c_value = unescape(c_value.substring(c_start,c_end));
	}
	return c_value;
}

function setCookie(c_name,value,exdays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
	document.cookie=c_name + "=" + c_value;
}
	
/* Se viene passata una tile non viene processato il caricamento delle cartegorie */
var init = function(eventsArr, geocenter, servletUrl, categorieKmlJSON, contextPath, zoomValue, defaultIconMap, isTile) {
	OpenLayers.ImgPath=contextPath+"/img/";
	pathToImage=contextPath+"/img/";
	
	var selectArray=new Array();
	centerPoint= new OpenLayers.LonLat(geocenter.split(",")[0], geocenter.split(",")[1]).transform(
			fromProjection, toProjection);
	
	map = new OpenLayers.Map({
		eventListeners: {
			"changelayer": mapLayerChanged
		},
		div : "map",
		theme : null,
/*		
		controls : [ new OpenLayers.Control.Attribution(),
						new OpenLayers.Control.TouchNavigation({
							dragPanOptions : {
								enableKinetic : true
							}
						}), 
						new OpenLayers.Control.Zoom(), 
						
				],
*/				
		
		layers : [ new OpenLayers.Layer.OSM("OpenStreetMap", 
						[
						 	'https://a.tile.openstreetmap.org/${z}/${x}/${y}.png',
						 	'https://b.tile.openstreetmap.org/${z}/${x}/${y}.png',
						 	'https://c.tile.openstreetmap.org/${z}/${x}/${y}.png'
						], 
						 {
							transitionEffect : 'resize', 
							isBaseLayer: true, 
							displayInLayerSwitcher: false
						 }) //OSM
				],
				
		center : centerPoint,
		zoom : zoomValue
	});
	// Aggiunge il layer Markers
	var markers = new OpenLayers.Layer.Markers( "Markers" );
	map.addLayer(markers);
//	var icon = new OpenLayers.Icon('https://maps.google.com/mapfiles/arrow.png');
	var icon = new OpenLayers.Icon(pathToImage+'omino80.png');
	var marker = new OpenLayers.Marker(centerPoint, icon);
	markers.addMarker(marker);

	// Disabilita la rotella del mouse per evitare lo zoom sulla mappa col mouse
	var controls = map.getControlsByClass('OpenLayers.Control.Navigation');
	for(var i = 0; i < controls.length; ++i)
		 controls[i].disableZoomWheel();
	
	// Non carica le categorie se è una tile
	if (!isTile) {
    	var stileIcona=new OpenLayers.Style({ 
    		strokeColor: "#FFCC33", 
    		strokeWidth:10, 
    		strokeOpacity:1, 
    		fillColor:"#003399", 
    		fillOpacity: 1, 
    		externalGraphic: defaultIconMap,
    		labelYOffset: -32,
    		pointRadius: 16
    	});	
    	/* Settaggio dell'icona normale e dell'icona una volta cliccato l'oggetto (selezionato) */
    	var defaultStyleMap = new OpenLayers.StyleMap({ 
    		"default": stileIcona,
    		"select": stileIcona
    	});
    	$.each(categorieKmlJSON, function(ind1, categoria) {
    		$.each(categoria.kmls, function(ind2, layerKml) {
				var layerDinamico = new OpenLayers.Layer.Vector(layerKml.id, {
					styleMap: defaultStyleMap, // Icona di default
				    strategies: [new OpenLayers.Strategy.Fixed()],
				    displayInLayerSwitcher: false,
				    protocol: new OpenLayers.Protocol.HTTP({
				      url: servletUrl+"&idkml=" + layerKml.id,//invoco la servlet...,
				      format: new OpenLayers.Format.KML({
				        extractStyles: true, 
				        extractAttributes: true,
				        maxDepth: 2
				      })
				    })
				  });
				// Memorizza nell'oggetto passato il puntamento a questo layer per accenderlo o spegnerlo dall'utente
		    	layerKml.layer=layerDinamico;
		    	layerDinamico.setVisibility(true);
				map.addLayer(layerDinamico);
				selectArray.push(layerDinamico);
		    });
		});

		var selectControl = new OpenLayers.Control.SelectFeature(selectArray);
        selectControl.onSelect = function(feature) {
            if (feature.attributes.clickable != 'off') {
				$('#dettaglio-poi').html(feature.attributes.description);
				log('DETTAGLIO-POI: '+feature.attributes.description);
				var content = '<p>'+feature.attributes.name + '</p>';
				var popup = new OpenLayers.Popup.FramedCloud("NomePOI", 
										 feature.geometry.getBounds().getCenterLonLat(),
 										 new OpenLayers.Size(100,100),
										 content,
										 null, true, function() {
													selectControl.unselectAll();
												});
				feature.popup = popup;
				map.addPopup(popup);				
			}
        };
		selectControl.onUnselect = function(feature) {
            if(feature.popup) {
                map.removePopup(feature.popup);
                popup.popupSelect.unselect(feature);
                feature.popup.destroy();
                feature.popup=null;
//                delete feature.popup;
            }		
		};
        map.addControl(selectControl);
        selectControl.activate();		
	} // fine IF
	
	if (!isTile) {
		resetToCookiesVisibility();
	}
	
};

function resetToCookiesVisibility(){
	var cookVal=getCookie(cookieName);
	if(cookVal!=null)
		{
		var arrVal=cookVal.split(separator);
		disableEventChangeLayer=true;
			for (var i=0;i<map.layers.length;i++)
				{
				var currentLayer=map.layers[i];
				
				for(var y=0;y<arrVal.length;y++)
					{
						if(arrVal[y]==currentLayer.name) {
							currentLayer.setVisibility(false);
							// Disabilita anche la categoria corrispondente
							disabilitaCheckboxCategoriaPerKmls($("#listaCheckboxCategorie1 li div input"), arrVal[y]);
							disabilitaCheckboxCategoriaPerKmls($("#listaCheckboxCategorie2 li div input"), arrVal[y]);
						}
					}
				
				}
			disableEventChangeLayer=false;
		}
}

function disabilitaCheckboxCategoriaPerKmls(elementi, valore) {
	elementi.each(function(index, elemento) {
		var oggetto=$(elemento).data("Data");
		
		$.each(oggetto.kmls, function(ind3, entry) {
			if (entry.id==valore) {
				$(elemento).prop('checked', false);
			}
		});
	});
}

function showPosition(position) {
	var centerPoint=new OpenLayers.LonLat(position.coords.longitude, position.coords.latitude);
	log('posizione acquisita: lon:' + position.coords.longitude + ' lat: ' + position.coords.latitude);
	log('centerPoint: lon:' + centerPoint.lon + ' lat: ' + centerPoint.lat);
	var distanzaCentro=OpenLayers.Util.distVincenty(centroCitta, centerPoint);
	if (distanzaCentro > distanzaKmDalCentroCitta) {
		log('Troppo distante dal centro di '+distanzaCentro+ ' Km');
		var centroTrasformato=centroCitta.transform(fromProjection, toProjection);
		map.setCenter(centroTrasformato, 15);
		spostaMarkerTuSeiQui(centroTrasformato);
		
	} else {
		var centroTrasformato=centerPoint.transform(fromProjection, toProjection);
		map.setCenter(centroTrasformato, 15);
		spostaMarkerTuSeiQui(centroTrasformato);

		log('Utente posizionato a '+distanzaCentro+ ' Km dal centro.');
	}
}

function showError(error) {
	log('Geolocalizzazione non avvenuta: '+error.code + ": " + error.message);
}

function getGeoLocation() {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(showPosition, showError);
	} else {
		log('Geolocalizzazione non supportata dal browser');
	}
}

//Elimina tutto ciò che è dopo il numero civico prima del trattino del cap
//Se il cap non c'e' torna lo stesso valore ricevuto
function parsaIndirizzo(valore) {
	var posVirgola=valore.indexOf(',');
	var parteIniziale=valore.slice(0,posVirgola+1);
	var posTrattino=valore.indexOf('-');
	var r = /\d+/;
	var sottostringa=valore.slice(posVirgola);
	var numeroCivico=sottostringa.match(r);
	if (posTrattino!=-1) {
		var parteFinale=valore.slice(posTrattino);
		var cap=parteFinale.match(r);
		var posizioneCap=parteFinale.search(cap);
		var parteFinaleDefinitiva=parteFinale.slice(posizioneCap+cap.toString().length);
		valore=parteIniziale+' '+numeroCivico+' '+parteFinaleDefinitiva;
	} 
	return valore;
}

/*
 * Funzione di spostamento del merker che è stato impostato inizialmente sul centro convenzionale 
 * della mappa oppure con la geolocalizzazione.
 */
function spostaMarkerTuSeiQui(punto) {
	var layer=map.getLayersByName('Markers')[0];
	layer.removeMarker(layer.markers[0]);
//	var icon = new OpenLayers.Icon('https://maps.google.com/mapfiles/arrow.png');
	var icon = new OpenLayers.Icon(pathToImage+'omino80.png');
	marker = new OpenLayers.Marker(punto, icon.clone());
	layer.addMarker(marker);
}
