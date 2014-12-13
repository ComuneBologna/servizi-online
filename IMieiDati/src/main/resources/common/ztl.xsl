<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="*">


<html>
  <head>
<script type="text/javascript">
<xsl:text>
      var arrID = new Array();
      var arrNome = new Array();
      var arrContrassegno = new Array();
      var arrValidita = new Array();
      var arrRow = new Array();
      var spc = "&amp;nbsp;";
      var lineSeparator = spc + spc + "-" + spc + spc;

      var i18n_titolare = "Titolare" + ":" + spc + spc;
      var i18n_valido = "valido" + spc;


      function loadDataDelayed() {
//alert('loadDataDelayed() - START!');
          window.setTimeout("loadData()", 100);
      }


      function show() {
//alert('SHOW() - START!');
          var comboBox = document.getElementById("combo_ztl");
//alert("arrContrassegno.length: " + arrContrassegno.length);
//alert("comboBox.options.length - " + comboBox.options.length);
          for (var i = 0; i &lt; arrContrassegno.length; i++) {
              var newOption = document.createElement("option");
              newOption.text = arrContrassegno[i];
              newOption.value = arrID[i];
              comboBox.options.add(newOption);
          }
          var comboBox = document.getElementById("combo_ztl");
          changeText();
//alert('SHOW() - END');
      }

      function changeText() {
//alert('changeText() - START!');
        // set Titolare
	var combo = document.getElementById("combo_ztl");
        var index = combo.selectedIndex;
	var id = combo.options[combo.selectedIndex].value;
	var contrassegno = combo.options[combo.selectedIndex].text;

        var txtNomeVal = document.getElementById("nomeVal");
        var name = arrNome[index];
        var validity = arrValidita[index];
        var str = i18n_titolare + name + lineSeparator + i18n_valido + validity;
        txtNomeVal.innerHTML = "&lt;html>&lt;body>" + str + "&lt;/body>&lt;/html>";

        // set Veicoli
        var strVeicoli = "&lt;table>";
        for (var i = 0; i &lt; arrRow.length; i += 4) {
            var currID = arrRow[i];
            if (currID.valueOf() == id.valueOf()) {
//alert("targa: " + arrRow[i+1]);
////                strVeicoli += arrRow[i+1] + lineSeparator + arrRow[i+2] + lineSeparator + i18n_valido + arrRow[i+3] + "&lt;br>";
                  strVeicoli += "&lt;tr>&lt;td>" + arrRow[i+1] + "&lt;/td>&lt;td>" + lineSeparator;
                  strVeicoli += "&lt;/td>&lt;td>" + arrRow[i+2] + "&lt;/td>&lt;td>" + lineSeparator;
                  strVeicoli += "&lt;/td>&lt;td>" + i18n_valido + arrRow[i+3] + "&lt;/td>&lt;/tr>"; 
            }
	}
        strVeicoli += "&lt;/table>";
        var txtNomeVal = document.getElementById("veicoliList");
        txtNomeVal.innerHTML = "&lt;html>&lt;body>" + strVeicoli + "&lt;/body>&lt;/html>";

//alert('changeText() - END!');
      }



//// OK   window.setTimeout("loadData()", 1000);
//// OK   window.onload=show();
      window.onload=loadDataDelayed();

</xsl:text>
</script>
  </head>
<!--  <body onload="loadData();"> -->
    <body>
	  <style>
		.fieldset-dati { border:1px solid #CCCCCC }
	  </style>
 	   <xsl:for-each select="view">
 	    <xsl:if test="not(contains(@titolo,'_EVENTO_'))">
		  <xsl:call-template name="View">
		    <xsl:with-param name="view" select="dettaglioFonte/view"/>
		  </xsl:call-template>
		</xsl:if>
	   </xsl:for-each>
	   
	   <xsl:for-each select="fault">
	     <xsl:call-template name="FaultType">
	       <xsl:with-param name="faultType" select="dettaglioFonte/fault"/>
	     </xsl:call-template>
	   </xsl:for-each>

    Contrassegno:&#160;&#160;<select style="align: left;" id="combo_ztl" onChange="changeText();"></select><br/>
    <div id="nomeVal"> </div><br/>
    <table>
      <tr>
        <td style="vertical-align: text-top; margin: 0px; padding: 4px;">Veicoli associati:&#160;&#160;</td>
        <td><div id="veicoliList"> </div></td>
      </tr>
    </table>
  </body>
</html>


	</xsl:template>
	
	<xsl:template name="FaultType" mode="toc">
	  <xsl:param name="faultType"/>
	  <fieldset class="fieldset-dati">
	    <legend><br>Errore caricamento servizio</br></legend>
	    <div>Codice Errore : <b><xsl:value-of select="code"/></b></div> 
	    <div>Descrizione Errore : <b><xsl:value-of select="description"/></b></div> 
	  </fieldset>
	</xsl:template>

	
	<xsl:template name="View" mode="toc">
	  <xsl:param name="view"/>
 <!-- 	  DEBUG - <xsl:value-of select="@titolo"/>  -->
          <xsl:if test="@titolo = 'contrassegni'">
<script type="text/javascript">
<xsl:text>

    function loadData() {
//alert('loadData() INVOKED!!');

</xsl:text>
	     <xsl:for-each select="elemento">
	       <xsl:call-template name="Elemento">
	         <xsl:with-param name="elemento" select="elemento"/>
	       </xsl:call-template>
	     </xsl:for-each>
<xsl:text>

        show();
    }
</xsl:text>
</script>
	  </xsl:if>
	</xsl:template>

	
	<xsl:template name="Elemento" mode="toc">
	  <xsl:param name="elemento"/>
          <xsl:if test="tabella/@titolo = 'Utente'">
	    arrNome = arrNome.concat("<xsl:value-of select="tabella/header/columnName[1]"/>");
	    arrContrassegno = arrContrassegno.concat("<xsl:value-of select="tabella/header/columnName[2]"/>");
	    arrValidita = arrValidita.concat("<xsl:value-of select="tabella/header/columnName[3]"/>");
	  </xsl:if>
          <xsl:if test="tabella/@titolo = 'Veicoli'">
<!--	    <br/> -->
	    arrID = arrID.concat("<xsl:value-of select="tabella/@id"/>");

	     <xsl:for-each select="tabella/rows">
		
                arrRow = arrRow.concat("<xsl:value-of select="../@id"/>");
		arrRow = arrRow.concat("<xsl:value-of select="rowElement[1]/testo/@valore"/>");
		arrRow = arrRow.concat("<xsl:value-of select="rowElement[2]/testo/@valore"/>");
		arrRow = arrRow.concat("<xsl:value-of select="rowElement[3]/testo/@valore"/>");
<!--                <br/> -->
	     </xsl:for-each>
<!--	     <br/> -->
	  </xsl:if>
	</xsl:template>

</xsl:stylesheet>
