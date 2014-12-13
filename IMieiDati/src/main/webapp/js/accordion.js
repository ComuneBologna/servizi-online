/**
 * @author      Rossella Petrucci
 * @version     1.0
 */

/** * Global Variable that defines the Context and sections to hide*/
contesto = '';

////var imd_IE_DELTA = 200;

function roundCorners() {
	if (/MSIE (\d+\.\d+);/.test(navigator.userAgent)){ //test for MSIE x.x;
		var ie_version = new Number(RegExp.$1); // capture x.x portion and store as a number
		
		if (ie_version < 9) {
			//alert("IN - IE version: " + ie_version);
			var allAccordionsA = $("a[id$='-toggler']");
			//alert("allAccordionsA: " + allAccordionsA);
			//if (allAccordionsA != null) {
				//alert("allAccordionsA.length: " + allAccordionsA.length);
			//}
		
			////if (allAccordionsA.length == 0) {
			////	window.setTimeout(function(){roundCorners();}, imd_IE_DELTA);
			////}
			$.each(allAccordionsA, function(index, a) {
				//alert("index: " + index + " -- a.id: " + a.id);
				$(this).corner("19px");
			});
		}
	}
	
}


/**
 * Execute a function when the DOM is fully loaded
 */
$(document).ready(function() {
///alert('DEBUG - accordion.js - document.ready() FIRED!');
	//Initial Settings
	/** * Save the Context ***** */
	contesto = $('#contesto_imieidati').text();

	/*
	 * Definizione della funzione trim che manca in IE8. 
	 *   Se il browser non la supporta, questa funzione la definisce
	 */
	if (typeof String.prototype.trim !== 'function') {
	    String.prototype.trim = function() {
	        return this.replace(/^\s+|\s+$/g, '');
	    };
	}
		
	//Get all Portlets div
	var allPortlets = $('div[id^="singlePortlet_"]');
	
	for(var i = 0; i < allPortlets.length; i++){
		
		var divPortletId = allPortlets[i].id;
		
		//Get PortletName from div created on the page
		var singlePortletName = divPortletId.replace(/^singlePortlet_/, '');
		
		var sectionsToHide = $('#' + singlePortletName + '_sezioni_nascoste').text();

		getAccordionData(singlePortletName, sectionsToHide);
	}
	
});

function getAccordionData(singlePortletName, sectionsToHide){
	var postUrl = $('#'+singlePortletName+'_ajax_url').text();
//	var postdata = JSON.stringify();
	
	// NOTE: Use cmd: "ListaSezioni" for normal behaviour
	//       Use cmd: "ListaSezioniValide" to hide empty sections (SLOW)
	$.ajax({
		url: postUrl,
		type: 'POST',
		data: {cmd : "ListaSezioniValide",token : "132465"},
		success:function(json){
			//alert('DEBUG - SUCCESS' + json);
			var sectionsArray = null;
			try {
				sectionsArray = JSON.parse(json); 
			} catch(err) {
				sectionsArray = json;
			} 
			//alert('DEBUG - sectionsArray: ' + sectionsArray);
			
			// OLD version
			//createAccordion(singlePortletName, sectionsArray, sectionsToHide);
			//$("#" + singlePortletName + "_accordion .expanded").hide();
			createAccordion(singlePortletName, sectionsArray, sectionsToHide);
		},
		error: function (request, status, error) {
            console.log(request.responseText);
        }
	});

}


/**
 * Create the accordion dynamically - Updated version
 */
function createAccordion(singlePortletName, sectionsArray, sectionsToHide) {
	///alert('DEBUG - createAccordion_NEW() INVOKED!');

    // Use array for all selected checkbox
    var allSectionsHide = sectionsToHide.split(";");
    
    // check for some error
	try {
		if (sectionsArray != null && sectionsArray.substring) {
			//alert('DEBUG - sectionsArray: ' + sectionsArray);
			if (sectionsArray.trim().startsWith('<')) {
				//alert('DEBUG - appending...');
				$('#' + singlePortletName + '__main-portlet-body').append(sectionsArray);
				return;
			}
		}
	} catch(err) {}
    

    // create accordion
    //alert('DEBUG - createAccordion() -- sectionsArray.length: ' + sectionsArray.length);
    for (var i = 0; i < sectionsArray.length; i++) {
    	///alert('DEBUG - createAccordion_NEW() -- i: ' + i);

        // Check if is Hidden From Preferences ids saved
        var isHidden = 0;

        for (var j = 0; j < allSectionsHide.length; j++) {
        	var sectionId = allSectionsHide[j];
            if (sectionId == sectionsArray[i]["id"]) {
                isHidden = 1;
            }
        }

        if (Boolean(isHidden) == 1) {
            continue;
        }

        //Check if section is enabled
        var isEnabled = sectionIsEnabled(i, singlePortletName, sectionsArray);
		
        if (Boolean(isEnabled) == 0) {
            continue;
        }


        // create section html layout
        var sectionName = singlePortletName + '_section_' + sectionsArray[i]["id"];
        var title = sectionsArray[i]["titolo"];
        var description = sectionsArray[i]["descrizione"];
        var iconImage = sectionsArray[i]["iconImage"];

        var html = buildSectionLayout(sectionName, title, description, iconImage);
        $('#' + singlePortletName + '__main-portlet-body').append(html);
        

        // add service invokation function
        $('#' + sectionName + '-toggler').click( function(event) {
        	var nodeName = event.target.parentNode.id;
            ///alert('DEBUG - event.target.parentNode.id: ' + event.target.parentNode.id);

            var currentSectionName = getSectionName(nodeName);
            var currentSectionID = getSectionId(nodeName);
            
            // detect current accordion mode
            var accordionModeClass = $('#collapse-' + currentSectionName).attr("class");
            ///alert('DEBUG -- accordionModeClass( #collapse-' + currentSectionName + ' ): ' + accordionModeClass);
            var modalClass = " in";
            if (accordionModeClass.indexOf(modalClass, accordionModeClass.length - modalClass.length) !== -1) {
                ///alert('DEBUG - Accordion is OPEN');

            } else {
                ///alert('DEBUG - Accordion is CLOSED -- Invoking service...');

                // prepare service invokation
                var waiting_object = buildWaitingObject(currentSectionName);
                $('#' + currentSectionName + '-accordion-body').empty();
                $('#' + currentSectionName + '-accordion-body').append(waiting_object);
                
                // add service data
                getServiceData(currentSectionID, singlePortletName, currentSectionName, sectionsArray);
                
                //var serviceHtml = getServiceData();
                //$('#' + sectionName + '-accordion-body').append(serviceHtml);
            }
        });
    }
    
	// Finally, save section numbers, for IE updating
	roundCorners();
   
}


function getSectionId(currentSectionName) {
    var id = "-11";
    if (currentSectionName != null) {
        var start = currentSectionName.lastIndexOf('_') + 1;
        var end = currentSectionName.lastIndexOf('-');
        if (start > 0 && end > start) {
            id = currentSectionName.substring(start, end);
        }
    }
    return id;
}

function getSectionName(currentSectionName) {
    var name = "";
    if (currentSectionName != null) {
        var end = currentSectionName.lastIndexOf('-');
        if (end > 0) {
            name = currentSectionName.substring(0, end);
        }
    }
    return name;
}

function findSectionIndex(sectionID, sectionsArray) {
	for (var i = 0; i < sectionsArray.length; i++) {
		if (sectionsArray[i].id == sectionID) {
			return i;
		}
	}
	return -1;
}

/**
 *  Check if section is enabled for this portlet
 * 
 * @param id
 *            the id of the accordion header clicked
 */
function sectionIsEnabled(id, singlePortletName, sectionsArray){
	
	// Check if the section is enabled for this portlet by using portlet
	// name!
	var isEnabled = 0;
	var portletAbilitateString = sectionsArray[id]["portletAbilitate"];

	if (portletAbilitateString != null && (portletAbilitateString !='')) {
		var allPortletEnabled = portletAbilitateString.split(";");

        for (var j = 0; j < allPortletEnabled.length; j++) {
        	var portletEnabled = allPortletEnabled[j];
			if (portletEnabled == singlePortletName) {
				isEnabled = 1;
			}
        }
		
	}
	
	//If null or empty, all portlet has this section!
	else{
		isEnabled = 1;
	}
	
	return isEnabled;
}


/**
 * Get service data: Fill accordion
 */
function getServiceData(sectionID, singlePortletName, sectionName, sectionsArray) {
	var index = findSectionIndex(sectionID, sectionsArray);
	// ajax call for getting html data to insert in div content
	///alert('DEBUG - getServiceData() -- index: ' + index + ' - sectionName: ' + sectionName);
	var postUrl = $('#'+singlePortletName+'_ajax_url').text();
	var postdata = {cmd : "DettaglioSezione",token : "132465",id : sectionsArray[index].id, codice : sectionsArray[index].id};
	///alert('DEBUG - getServiceData() -- postdata: ' + postdata);
	$.ajax({
		url: postUrl,
		type: 'POST',
		data: postdata,
		success:function(response){
			//alert('DEBUG - getServiceData() SUCCESS - sectionName: ' + sectionName);
			$('#' + sectionName + '-icons').hide();
			$('#' + sectionName + '-accordion-body').append(response);
		},
		error: function (request, status, error) {
			console.log(status);
			console.log(error);
            console.log(request.responseText);
        }
	});

	return false;
}

function buildWaitingObject(sectionName) {
	var html = '' + 
		'<div id="' + sectionName + '-icons">' +
		'    <ul>' + 
		'        <li class="ion-loading-a" data-pack="default" data-tags="spinner, waiting, refresh, animation" data-animation="true"></li>' +
		'    </ul>' + 
		'</div>';
	return html;
}




function buildSectionLayout_OLD(sectionName, title, description, iconImage) {
	// NOTE: aggiungere  "panel-heading"  in class del  div 3a riga
	//       rimuovere classe "portlet" in <section>  4a riga
  var html = '' + 
  ''+
  '    <div class="panel panel-accordion">' +
  '      <section class="portlet" id="' + sectionName + '">' +
  '' +
  '        <div class="panel-heading">' +
  '          <h3 class="panel-title portlet-title">' +
  '            <span class="portlet-title-text portlet-title-icon">' + 
  '                <i class="fa ' + iconImage + '"></i>' +
  '' + 				title + 
  '           </span>' +
  '          </h3>' +
  '          <p style="line-height: 1.42857;">' + description + '</p>' +
  '        </div> <!-- /panel-heading -->' +
  '' +
  '        <div style="padding-top: 10px; padding-bottom: 10px;" class="imieidati-header panel-body">' +
  '          <div class="portlet-content-container">';
//  '            <div class="portlet-body">' +
//  '            </div> <!-- /portlet-body -->' +
  html = html + '          </div> <!-- /portlet-content-container -->' +
  '        </div> <!-- /panel-body -->' +
  '' +
  '        <a id="' + sectionName + '-toggler" data-toggle="collapse" data-parent="#' + sectionName + '" href="#collapse-' + sectionName + '" title="Visualizza contenuto" class="toggle panel-toggle collapsed">' +
  '          <span class="sr-only">Visualizza contenuto</span>' +
  '          <span class="glyphicon glyphicon-chevron-down"></span>' +
  '          <span class="glyphicon glyphicon-chevron-up"></span>' +
  '        </a>' +
  '' +
  '        <div class="panel-collapse collapse" id="collapse-' + sectionName + '">' +
  '          <div style="padding-top: 0px; padding-bottom: 10px;" class="panel-body imieidati-body">' +
  '            <div class="portlet-content-container">' +
  '              <div id="' + sectionName + '-accordion-body" class="portlet-body">' +
  '' +  // SERVICE-GENERATED SECTION --- START
  '' +  // SERVICE-GENERATED SECTION --- END
  '              </div> <!-- /portlet-body -->' +
  '            </div> <!-- /portlet-content-container -->' +
  '          </div> <!-- /panel-body -->' +
  '        </div> <!-- /panel-collapse -->' +
  '' +
  '      </section> <!-- /portlet -->' +
  '    </div> <!-- panel -->' +
  ''+
  '';

    return html;
}


function buildSectionLayout(sectionName, title, description, iconImage) {
  var html = '' + 
  ''+
  '    <div class="panel panel-accordion">' +
  '      <section class="portlet" id="' + sectionName + '">' +
  '' +
  '        <div style="padding-top: 10px; padding-bottom: 0px;" class="panel-heading">' +
  '          <h2 style="width: 100%;" class="panel-title portlet-title">' + title + '</h2>' +
  '        </div>' +
  '' +
  '        <div class="imieidati-header panel-body imieidati-description">' +
  '          <div class="portlet-content-container">' +
  '            <div class="portlet-body">' +
  '              <p style="margin-top: 6px; margin-left: 5px; line-height: 1.42857; ">' + description + '</p>' +
  '            </div> <!-- /portlet-body -->' +
  '          </div> <!-- /portlet-content-container -->' +
  '        </div> <!-- /panel-body -->' +
  '' +
  '        <a style="right: 20px; bottom: 12px;" id="' + sectionName + '-toggler" data-toggle="collapse" data-parent="#' + sectionName + '" href="#collapse-' + sectionName + '" title="Visualizza contenuto" class="toggle panel-toggle-bottom collapsed">' +
  '          <span class="sr-only">Visualizza contenuto</span>' +
  '          <span class="glyphicon glyphicon-chevron-down"></span>' +
  '          <span class="glyphicon glyphicon-chevron-up"></span>' +
  '        </a>' +
  '' +
  '        <div style="padding-bottom: 40px;" class="panel-collapse collapse" id="collapse-' + sectionName + '">' +
  '          <div style="padding-top: 0px; padding-bottom: 10px;" class="panel-body imieidati-body">' +
  '            <div class="portlet-content-container">' +
  '              <div id="' + sectionName + '-accordion-body" class="portlet-body">' +
  '' +  // SERVICE-GENERATED SECTION --- START
  '' +  // SERVICE-GENERATED SECTION --- END
  '              </div> <!-- /portlet-body -->' +
  '            </div> <!-- /portlet-content-container -->' +
  '          </div> <!-- /panel-body -->' +
  '        </div> <!-- /panel-collapse -->' +
  '' +
  '      </section> <!-- /portlet -->' +
  '    </div> <!-- panel -->' +
  ''+
  '';

    return html;
}