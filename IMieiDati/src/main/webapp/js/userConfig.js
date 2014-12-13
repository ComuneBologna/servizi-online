/**
 * @author      Rossella Petrucci
 * @version     1.0
 */

/** * Global Variable that defines the Context */
var contesto = '';

/**
 * Execute a function when the DOM is fully loaded
 */
$(document).ready(function() {

	/** * Save the Context ***** */
	contesto = $('#contesto_imieidati').text();
	
	//Get all Portlets div
	var allPortlets = $('div[id^="singlePortlet_"]');
	
	for(var i = 0; i < allPortlets.length; i++){
		
		var divPortletId = allPortlets[i].id;
		
		//Get PortletName from div created on the page
		var singlePortletName = divPortletId.replace(/^singlePortlet_/, '');
		
		var sectionsToHide = $('#' + singlePortletName + '_sezioni_nascoste').text();

		getSectionsData(singlePortletName, sectionsToHide);
	}
});

function getSectionsData(singlePortletName, sectionsToHide){
	
	// ajax call for getting sections info
	$.post(contesto + "/rest/imieidatiservice", {
		cmd : "ListaSezioni",
		token : "132465"
	}, function(json) {
		var sectionsList = JSON.parse(json);
		printAccordionList(singlePortletName, sectionsList, sectionsToHide);

	});
}

/**
 * Print Accordion List dinamically
 * 
 * @param myArray
 *            a list of objects that can be used for populate the list with data
 */
function printAccordionList(singlePortletName, sectionsList, sectionsToHide) {

	// Use array for all selected checkbox
	var allSectionsHide = sectionsToHide.split(";");

	// create accordion
	for (var i = 0; i < sectionsList.length; i++) {

		// accordion first section: I MIEI DATI non può essere rimossa quindi
		// prosegui il ciclo
		if (sectionsList[i]["codice"] == "IMD") {
			continue;
		}
		
		//Check if the section is enabled for this portlet by using portlet name!
		var isEnabled = 0;
		var portletAbilitateString = sectionsList[i]["portletAbilitate"];

		if (portletAbilitateString != null) {
			var allPortletEnabled = portletAbilitateString.split(";");
			allPortletEnabled.forEach(function(portletEnabled) {
				if (portletEnabled == singlePortletName) {
					isEnabled = 1;
				}
			});
		}
		
		if (Boolean(isEnabled) == 0) {
			continue;
		}

		// Checkbox is selected? Check if myarray[i]["id"] is inside
		// allsectionshide array
		var isHidden = 0;

		allSectionsHide.forEach(function(sectionId) {
			if (sectionId == sectionsList[i]["id"]) {
				isHidden = 1;
			}
		});
		
		
		// Create Span tag
		$('#' + singlePortletName + '_userConfigForm').append(jQuery('<span/>', {
			id : singlePortletName + '_span_' + sectionsList[i]["id"],
		}));
		
		$('#' + singlePortletName + '_span_' + sectionsList[i]["id"]).attr("title", "Seleziona la sezione "  + "\"" + sectionsList[i]["titolo"] + " " + sectionsList[i]["descrizione"] + "\"");
		$('#' + singlePortletName + '_span_' + sectionsList[i]["id"]).attr("role", "checkbox");
		$('#' + singlePortletName + '_span_' + sectionsList[i]["id"]).attr("aria-label", "Seleziona la sezione "  + "\"" + sectionsList[i]["titolo"] + " " + sectionsList[i]["descrizione"] + "\"");
		
		//select tag
		$('#' + singlePortletName + '_span_' + sectionsList[i]["id"]).append(jQuery('<select/>', {
			id : singlePortletName + '_combobox_' + sectionsList[i]["id"],
			class : 'checkboxItem',
			name : "sectionsToHide_%" + singlePortletName + "%_" + sectionsList[i]["id"],
		}));
		
				
		//Check if section is Visible or not
		if (Boolean(isHidden) == 1) {		
			
			$('#' + singlePortletName + '_combobox_' + sectionsList[i]["id"]).append(jQuery('<option/>', {
				id : singlePortletName + '_option1_'  + sectionsList[i]["id"],
				class : 'checkboxItem',
				value : '0',
				}));
			
			$('#' + singlePortletName + '_combobox_' + sectionsList[i]["id"]).append(jQuery('<option/>', {
				id : singlePortletName + '_option2_'  + sectionsList[i]["id"],
				class : 'checkboxItem',
				selected: "selected",
				value : '1',
				}));
		}
		
		else{
			
			$('#' + singlePortletName + '_combobox_' + sectionsList[i]["id"]).append(jQuery('<option/>', {
				id : singlePortletName + '_option1_'  + sectionsList[i]["id"],
				class : 'checkboxItem',
				selected: "selected",
				value : '0',
				}));
			
			$('#' + singlePortletName + '_combobox_' + sectionsList[i]["id"]).append(jQuery('<option/>', {
				id : singlePortletName + '_option2_'  + sectionsList[i]["id"],
				class : 'checkboxItem',
				value : '1',
				}));
		}
		
		$('#' + singlePortletName + '_option1_' + sectionsList[i]["id"]).html("Visibile");	
		$('#' + singlePortletName + '_option2_' + sectionsList[i]["id"]).html("Non Visibile");
		
		// Create Label
		$('#' + singlePortletName + '_span_' + sectionsList[i]["id"]).append(jQuery('<label/>', {
			id : singlePortletName + '_label_' + sectionsList[i]["id"],
			class : 'checkboxLabel',
		}));

		// Add label text
		$('#' + singlePortletName + '_label_' + sectionsList[i]["id"]).html(
				sectionsList[i]["titolo"] + " " + sectionsList[i]["descrizione"]);

		// Add new line space
		$('#' + singlePortletName + '_label_' + sectionsList[i]["id"]).after("<br />");

	}

	$('#' + singlePortletName + '_userConfigForm').append(jQuery('<input/>', {
		id: singlePortletName + '_submit_button',
		value : "salva",
		class : 'submit_button',
		type : "submit"
	}));
	
	$('#' + singlePortletName + '_submit_button').attr("role", "button");
	$('#' + singlePortletName + '_submit_button').attr("tabindex", "0");
	$('#' + singlePortletName + '_submit_button').attr("aria-label", "Salva le sezioni nascoste");
	$('#' + singlePortletName + '_submit_button').attr("title", "Salva le sezioni nascoste");
}
