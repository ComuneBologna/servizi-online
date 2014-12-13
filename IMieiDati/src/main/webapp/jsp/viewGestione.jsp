<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ page import="javax.portlet.PortletPreferences"%>


<portlet:defineObjects />

<%
	String portalService = "";

	if (application.getAttribute("portalService") != null) {
		portalService = application.getAttribute("portalService")
				.toString();
	}
%>

<%
	if ("websphere".equalsIgnoreCase(portalService)) {
%>
<script src="<%=request.getContextPath()%>/js/jquery-1.11.1.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.dataTables.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery.jeditable.mini.js"></script>
<script src="<%=request.getContextPath()%>/js/tab.js"></script>

<script src="<%=request.getContextPath()%>/css/tab.css"></script>
<script src="<%=request.getContextPath()%>/css/jquery.dataTables.css"></script>
<script src="<%=request.getContextPath()%>/css/jquery-ui.min.css"></script>

<%
	}
%>


<%-- Save the context in an hidden div --%>
<div id="contesto_imieidati" style="display: none;"><%=request.getContextPath()%></div>

<ul id="tabsGestione">
	<li><a href="#" name="tab1">Sezioni</a></li>
	<li><a href="#" name="tab2">Localizzazione</a></li>
</ul>

<div id="contentGestione">
	<h3>Pannello di Gestione delle Sezioni</h3>
	<div class="spinner" id="spinnerGestione">
		<img alt="" src="<%=request.getContextPath()%>/img/spinner.gif"
			id="img-spinner">
	</div>

	<div id="tab1">
		<table id="tableSezioni" class="display" cellspacing="0" width="100%">
			<thead>
				<tr>
					<th></th>
					<th>Id</th>
					<th>Codice</th>
					<th>Titolo</th>
					<th>Descrizione</th>
					<th>Immagine</th>
					<th>Uri Xsl</th>
					<th>Uri Xsl Mobile</th>
					<th>Dati Xsl</th>
					<th>Dati Xsl Mobile</th>
					<th>Html</th>
					<th>Abilitata</th>
					<th>Id Servizio</th>
					<th>Url Servizio</th>
					<th>Nome Servizio</th>
					<th>Username Servizio</th>
					<th>Password Servizio</th>
					<th>Portlet Abilitate</th>
				</tr>
			</thead>
			<tbody id="tableSezioniBody">
			</tbody>
		</table>
		<button id="add_row_sezione" class="add_row">Aggiungi</button>
		<button id="delete_row_sezione" class="delete_row">Elimina</button>
		<a href="#" class="show_hide">Show/hide</a>
		<div class="detailDiv">
			Modifica Dettaglio Sezione<a href="#" class="show_hide">hide</a>

			<form id="submittest" action="#">
				Id: <input type="text" name="Id" id="Id"><br> Codice: <input
					type="text" name="Codice" id="Codice"><br> Titolo: <input
					type="text" name="Titolo" id="Titolo"><br>
				Descrizione: <input type="text" name="descrizione" id="Descrizione"><br>
				Immagine: <input type="text" name="immagine" id="Immagine"><br>
				Uri Xsl: <input type="text" name="uriXsl" id="UriXsl"><br>
				Uri Xsl Mobile: <input type="text" name="uriXslMobile"
					id="UriXslMobile"><br> Dati Xsl: <input type="text"
					name="datiXsl" id="DatiXsl"><br> Dati Xsl Mobile: <input
					type="text" name="datiXslMobile" id="DatiXslMobile"><br>
				Html Statico: <input type="text" name="htmlStatico" id="HtmlStatico"><br>
				Sezione Abilitata: <input type="checkbox" name="abilitata"
					id="FlgAbilitato"><br> Id Servizio: <input type="text"
					name="idServizio" id="IdServizio"><br> Url Servizio: <input
					type="text" name="urlServizio" id="UrlServizio"><br>
				Nome Servizio: <input type="text" name="nomeServizio"
					id="NomeServizio"><br> Username Servizio: <input
					type="text" name="usernameServizio" id="UsernameServizio"><br>
				Password Servizio: <input type="text" name="passwordServizio"
					id="PasswordServizio"><br> Portlet Abilitate: <input
					type="text" name="portletAbilitate" id="PortletAbilitate"><br>
				<input type="submit" value="Salva" id="detailSaveButton">

			</form>

		</div>
	</div>
	<div id="tab2">
		<table id="tableLocalizzazioni" class="display" cellspacing="0"
			width="100%">
			<thead>
				<tr>
					<th>Chiave</th>
					<th>Locale</th>
					<th>Valore</th>

				</tr>
			</thead>
			<tbody id="tableLocalizzazioniBody">
			</tbody>
		</table>
		<button id="add_row" class="add_row">Aggiungi</button>
		<button id="delete_row" class="delete_row">Elimina</button>

		<div id="dialog-form" title="Aggiungi Localizzazione">
			<p class="validateTips">All form fields are required.</p>

			<form>
				<fieldset>
					<label>Chiave</label> <input type="text" name="key" id="key"
						class="text ui-widget-content ui-corner-all"> <label>Locale</label>
					<input type="text" name="locale" id="locale"
						class="text ui-widget-content ui-corner-all"> <label>Valore</label>
					<input type="text" name="value" id="value"
						class="text ui-widget-content ui-corner-all">

					<!-- Allow form submission with keyboard without duplicating the dialog button -->
					<input type="submit" tabindex="-1"
						style="position: absolute; top: -1000px">
				</fieldset>
			</form>
		</div>

	</div>
</div>

</body>
</html>