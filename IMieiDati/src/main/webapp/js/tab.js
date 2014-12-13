/**
 * @author      Rossella Petrucci
 * @version     1.0
 */

/** * Global Variable that defines the Context */
var contesto = '';
var sectionArray = null;
var localizationArray = null;
var localeTable;
var dialog, form, key = $("#key"), value = $("#value"), locale = $("#locale"), allFields = $(
		[]).add(key).add(value).add(locale), tips = $(".validateTips");

/**
 * Execute a function when the DOM is fully loaded
 */
$(document).ready(function() {

	/** * Save the Context ***** */
	contesto = $('#contesto_imieidati').text();

	/** * Create Tabs ***** */
	createTabs();

	/** * Load first tab Content ***** */
	loadFirstTab();

	$("#add_row_sezione").button().on("click", function() {
		$(".detailDiv").slideToggle();
	});

	$("#delete_row_sezione").button().on("click", function() {
		deleteSections();
	});

	/** *Second Tab Settings***** */

	// Dialog Form
	dialog = $("#dialog-form").dialog({
		autoOpen : false,
		height : 300,
		width : 350,
		modal : true,
		buttons : {
			"Aggiungi Localizzazione" : addRow,
			Cancel : function() {
				dialog.dialog("close");
			}
		},
		close : function() {
			form[0].reset();
			allFields.removeClass("ui-state-error");
		}
	});

	// Save Button on Dialog Form
	form = dialog.find("form").on("submit", function(event) {
		event.preventDefault();
		addRow();
	});

	$("#add_row").button().on("click", function() {
		dialog.dialog("open");
	});

	$("#delete_row").button().on("click", function() {

		// Search Checkbox with Property Checked
		var checkBoxItems = document.getElementsByClassName('checkboxDelete');
		var checkedItems = new Array();

		for (var i = 0; i < checkBoxItems.length; i++) {
			if (checkBoxItems[i].checked == '1') {

				var checkBoxIdString = checkBoxItems[i].id.split("_");
				checkedItems.push(checkBoxIdString[3]);
			}
		}

		// Delete Elements Checked
		$.post(contesto + "/rest/imieidatiservice", {
			cmd : "GestioneLocalizzazioni",
			token : "132465",
			json : JSON.stringify(checkedItems),
			operation : "delete"
		}, function(json) {

			var array = json.split(',');

			for (var int = 0; int < array.length; int++) {

				var nRow = $(this).parents('tr')[array[int]];
				localeTable.fnDeleteRow(nRow);

			}

			var table = $('#tableLocalizzazioni').DataTable();
			table.fnClearTable(0);
			table.fnDraw();

		});

	});
});

/**
 * Delete Sections
 */
function deleteSections() {

	// Search Checkbox with Property Checked
	var checkBoxItems = document.getElementsByClassName('checkboxDelete');
	var checkedItems = new Array();

	for (var i = 0; i < checkBoxItems.length; i++) {
		if (checkBoxItems[i].checked == '1') {

			var checkBoxIdString = checkBoxItems[i].id.split("_");
			checkedItems.push(checkBoxIdString[3]);
		}
	}

	// Rest call for saving section
	$.post(contesto + "/rest/imieidatiservice", {
		cmd : "GestioneSezioni",
		token : "132465",
		json : JSON.stringify(checkedItems),
		operation : "delete"
	}, function(json) {

		$(".detailDiv").hide();

		var table = $('#tableSezioni').DataTable();
		table.row('.selected').remove().draw(false);
		table.fnClearTable(0);
		table.fnDraw();
	});
}

/**
 * Add Row To Table Localizzazione
 */
function addRow() {
	var valid = true;
	allFields.removeClass("ui-state-error");

	var localizzazione = new Object();

	localizzazione.chiave = $('#key').val();
	localizzazione.valore = $('#value').val();
	localizzazione.locale = $('#locale').val();

	$.post(contesto + "/rest/imieidatiservice", {
		cmd : "GestioneLocalizzazioni",
		token : "132465",
		json : JSON.stringify(localizzazione),
		operation : "add"
	}, function(json) {

		dialog.dialog("close");

		var table = $('#tableLocalizzazioni').DataTable();
		table.fnClearTable(0);
		table.fnDraw();
	});

	return valid;
}

/**
 * Create tabs
 */
function createTabs() {

	$("#contentGestione").find("[id^='tab']").hide(); // Hide all content
	$("#tabsGestione li:first").attr("id", "current"); // Activate the first
	// tab
	$("#contentGestione #tab1").fadeIn(); // Show first tab's content

	$('#tabsGestione a').click(function(e) {
		e.preventDefault();

		if ($(this).closest("li").attr("id") == "current") { // detection for
																// current tab
			return;
		} else {
			$("#contentGestione").find("[id^='tab']").hide(); // Hide

			$("#tabsGestione li").attr("id", ""); // Reset
			// id's
			$(this).parent().attr("id", "current"); // Activate
			// this
			$('#' + $(this).attr('name')).fadeIn();

			var tabIndex = $('#current').index();

			if (tabIndex == '0') {
				loadFirstTab();

				$("#tab1").find("[id^='tab']").show(); // Show

			}

			else {
				loadSecondTab();

				$("#tab2").find("[id^='tab']").show(); // Show

			}

		}

	});
}

/**
 * Load First Tab Content
 */
function loadFirstTab() {

	$(".detailDiv").hide();

	// rest call for getting sections info
	$.post(contesto + "/rest/imieidatiservice", {
		cmd : "ListaSezioni",
		token : "132465"
	}, function(json) {

		sectionArray = JSON.parse(json);

		/** * Create Table ***** */
		var table;
		if ( $.fn.dataTable.isDataTable( '#tableSezioni' ) ) {
			
		 table = $('#tableSezioni').dataTable();
		}
		else
		{
			createTableFirstSection();
			
		 table = $('#tableSezioni').dataTable({
				"bLengthChange" : false, // used to hide the "Show Number of
				// entries
				"bFilter" : false,
				"bInfo" : false,
				"bPaginate" : false,
				"bSort" : false,
				"retrieve": true
			});
		}

		// Detect Click on Table Row
		$('#tableSezioniBody').on(
				'click',
				'tr',
				function() {

					// if same cell clicked, deselect row and hide detail
					if ($(this).hasClass('selected')
							&& $('.selected').eq($(this).index())) {

						$(this).removeClass('selected');
						$(".detailDiv").hide();

					}
					// else select and show detail
					else {
						table.$('tr.selected').removeClass('selected');
						$(this).addClass('selected');

						$(".detailDiv").hide();

						id = this.rowIndex - 1;

						$('#Id').val(sectionArray[id]["id"]);
						$('#Codice').val(sectionArray[id]["codice"]);
						$('#Titolo').val(sectionArray[id]["titolo"]);
						$('#Descrizione').val(sectionArray[id]["descrizione"]);
						$('#Immagine').val(sectionArray[id]["image"]);
						$('#UriXsl').val(sectionArray[id]["uriXsl"]);
						$('#UriXslMobile')
								.val(sectionArray[id]["uriXslMobile"]);
						$('#DatiXsl').val(sectionArray[id]["datiXsl"]);
						$('#DatiXslMobile').val(
								sectionArray[id]["datiXslMobile"]);
						$('#HtmlStatico').val(sectionArray[id]["htmlStatico"]);
						$("#FlgAbilitato").attr('checked',
								sectionArray[id]["flgAbilitato"]);
						$("#FlgAbilitato").val(
								$("#FlgAbilitato").prop('checked'));
						$('#IdServizio').val(sectionArray[id]["idServizio"]);
						$('#UrlServizio').val(sectionArray[id]["urlServizio"]);
						$('#NomeServizio')
								.val(sectionArray[id]["nomeServizio"]);
						$('#UsernameServizio').val(
								sectionArray[id]["usernameServizio"]);
						$('#PasswordServizio').val(
								sectionArray[id]["passwordeServizio"]);
						$('#PortletAbilitate').val(
								sectionArray[id]["portletAbilitate"]);

						$(".detailDiv").slideToggle();
					}
				});

		// Save Button Function
		$('#detailSaveButton').click(function() {

			// Modify section Object or create new one
			var sezione = new Object();
			sezione.id = $('#Id').val();
			sezione.codice = $('#Codice').val();
			sezione.titolo = $('#Titolo').val();
			sezione.descrizione = $('#Descrizione').val();
			sezione.image = $('#Immagine').val();
			sezione.uriXsl = $('#UriXsl').val();
			sezione.uriXslMobile = $('#UriXslMobile').val();
			sezione.datiXsl = $('#DatiXsl').val();
			sezione.datiXslMobile = $('#DatiXslMobile').val();
			sezione.htmlStatico = $('#HtmlStatico').val();
			sezione.flgAbilitato = $('#FlgAbilitato').prop('checked');
			sezione.idServizio = $('#IdServizio').val();
			sezione.nomeServizio = $('#NomeServizio').val();
			sezione.urlServizio = $('#UrlServizio').val();
			sezione.usernameServizio = $('#UsernameServizio').val();
			sezione.passwordeServizio = $('#PasswordServizio').val();
			sezione.portletAbilitate = $('#PortletAbilitate').val();

			// Rest call for saving section
			$.post(contesto + "/rest/imieidatiservice", {
				cmd : "GestioneSezioni",
				token : "132465",
				json : JSON.stringify(sezione),
				operation : "add/modify"
			}, function(json) {

				table.row('.selected').remove().draw(false);

				table.fnClearTable(0);
				table.fnDraw();
			});

		});

		// Hide Loader
		$("#spinnerGestione").hide();

		// Show table and body. On default they are hidden!! (display:none is
		// default)
		document.getElementById("tableSezioni").style.display = "table";
		document.getElementById("tableSezioniBody").style.display = "";

	});

}

/**
 * Load Second Tab Content
 */
function loadSecondTab() {

	// rest call for getting sections info
	$.post(contesto + "/rest/imieidatiservice", {
		cmd : "ListaLocalizzazioni",
		token : "132465"
	}, function(json) {

		localizationArray = JSON.parse(json);

		/** * Create Table ***** */
		if ( $.fn.dataTable.isDataTable( '#tableLocalizzazioni' ) ) {
			
		}
		else
		{
			createTableSecondSection();
		}

		/** * Make Table Editable ***** */
		$('#tableLocalizzazioniBody td').editable(function(sValue) {
			/* Get the position of the current data from the node */
			var aPos = localeTable.fnGetPosition(this);

			/* Get the data array for this row */
			var aData = localeTable.fnGetData(aPos[0]);

			/* Update the data array and return the value */
			aData[aPos[1]] = sValue;

			// Modify section Object
			var localizzazione = localizationArray[aPos[0]];

			// if column 0 update key
			if (aPos[1] == '0')
				localizzazione.chiave = sValue;

			// if column 1 update locale
			else if (aPos[1] == '1')
				localizzazione.locale = sValue;

			// if column 2 update value
			else if (aPos[1] == '2')
				localizzazione.valore = sValue;

			// Save in DB
			$.post(contesto + "/rest/imieidatiservice", {
				cmd : "GestioneLocalizzazioni",
				token : "132465",
				json : JSON.stringify(localizzazione),
				operation : "update"
			}, function(json) {

			});

			return sValue;
		}, {
			"onblur" : 'submit'
		}); /* Submit the form when bluring a field */

		/* Init DataTables */
		var table;
		if ( $.fn.dataTable.isDataTable( '#tableSezioni' ) ) {
			
			localeTable = $('#tableLocalizzazioni').dataTable();
		}
		else
		{			
			localeTable = $('#tableLocalizzazioni').dataTable({
				"bLengthChange" : false, // used to hide the "Show Number of
				// entries
				"bFilter" : false,
				"bInfo" : false,
				"bPaginate" : false,
				"bSort" : false,
				"retrieve": true
			});
		}


		// Hide Loader
		$("#spinnerGestione").hide();

		// Show table and body. On default they are hidden!! (display:none is
		// default)
		document.getElementById("tableLocalizzazioni").style.display = "table";
		document.getElementById("tableLocalizzazioniBody").style.display = "";

	});

}

/**
 * Create table Localizzazioni dinamically
 */
function createTableSecondSection() {

	// create one row for each localization in db
	for (var i = 0; i < localizationArray.length; i++) {

		// ROW
		$('#tableLocalizzazioniBody').append(jQuery('<tr/>', {
			id : 'table_tr_' + i
		}));

		// POPULATE EACH COLUMN FOR ROW CREATED
		$('#table_tr_' + i).append(jQuery('<td/>', {
			id : 'table_td_' + "key_" + i
		}).text(localizationArray[i]["chiave"]));

		$('#table_tr_' + i).append(jQuery('<td/>', {
			id : 'table_td_' + "locale_" + i
		}).text(localizationArray[i]["locale"]));

		$('#table_tr_' + i).append(jQuery('<td/>', {
			id : 'table_td_' + "value_" + i
		}).text(localizationArray[i]["valore"]));

		// CheckBOX for DELETE
		$('#table_tr_' + i).append(
				jQuery('<input/>', {
					id : 'table_td_' + "checkbox_"
							+ localizationArray[i]["idLocalizz"],
					class : "checkboxDelete",
					type : 'checkbox'
				}));
	}

}

/**
 * Create the table Sezioni dinamically
 */
function createTableFirstSection() {

	// create one row for each section in db
	for (var i = 0; i < sectionArray.length; i++) {

		// ROW
		$('#tableSezioniBody').append(jQuery('<tr/>', {
			id : 'table_tr_' + i
		}));

		// CheckBOX for DELETE
		$('#table_tr_' + i).append(jQuery('<input/>', {
			id : 'table_td_' + "checkbox_" + sectionArray[i]["id"],
			class : "checkboxDelete",
			type : 'checkbox'
		}));

		// EACH COLUMN FOR ROW CREATED
		createColumn("id", i);
		createColumn("codice", i);
		createColumn("titolo", i);
		createColumn("descrizione", i);
		createColumn("image", i);
		createColumn("uriXsl", i);
		createColumn("uriXslMobile", i);
		createColumn("datiXsl", i);
		createColumn("datiXslMobile", i);
		createColumn("htmlStatico", i);
		createColumn("flgAbilitato", i);
		createColumn("idServizio", i);
		createColumn("urlServizio", i);
		createColumn("nomeServizio", i);
		createColumn("usernameServizio", i);
		createColumn("passwordeServizio", i);
		createColumn("portletAbilitate", i);

	}
}

function createColumn(attribute, index) {

	if (sectionArray[index][attribute] != null) {
		$('#table_tr_' + index).append(jQuery('<td/>', {
			id : 'table_td_' + attribute + "_" + index
		}).text(sectionArray[index][attribute]));
		
	} else {
		$('#table_tr_' + index).append(jQuery('<td/>', {
			id : 'table_td_' + attribute + "_" + index
		}).text(""));
	}
}