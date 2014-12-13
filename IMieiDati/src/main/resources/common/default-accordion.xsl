<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" version="1.0" encoding="iso-8859-1" indent="yes"/>
     <xsl:template match="*">
	 <html>
	<head>
	</head>
	  <body>
	  <style>
		.fieldset-dati { border:1px solid #CCCCCC }
	  </style>
	  
	   <div id="form:accordion">
		<div id="form:accordion_acco">
 	   <xsl:for-each select="view">
 	    <xsl:if test="not(contains(@titolo,'_EVENTO_'))">
		  <xsl:call-template name="View">
		    <xsl:with-param name="view" select="dettaglioFonte/view"/>
		  </xsl:call-template>
		</xsl:if>
	   </xsl:for-each>
	   </div>
	   	 <input type="hidden" name="form:accordion_active" value="0"
			id="form:accordion_active" />
	   
	   </div>
		<script type="text/javascript">widget_form_accordion = new ice.ace.AccordionPanel("form:accordion",{"active":0,"dynamic":false,"animated":"slide","autoHeight":false,"collapsible":true});</script>
		
		   	   
	   <xsl:for-each select="fault">
	     <xsl:call-template name="FaultType">
	       <xsl:with-param name="faultType" select="dettaglioFonte/fault"/>
	     </xsl:call-template>
	   </xsl:for-each>

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
	  <h3>
        <a href="#" tabindex="-1"><xsl:value-of select="@titolo"/></a>
      </h3>
      <div>
	     <xsl:for-each select="elemento">
	       <xsl:call-template name="Elemento">
	         <xsl:with-param name="elemento" select="elemento"/>
	       </xsl:call-template>
	     </xsl:for-each>
	  </div>
	</xsl:template>
	
	<xsl:template name="Elemento" mode="toc">
	  <xsl:param name="elemento"/>
	  <xsl:apply-templates select="testo"/>
	  <xsl:apply-templates select="campo"/>
	  <xsl:apply-templates select="tabella"/>
	  <xsl:apply-templates select="link"/>
	</xsl:template>
	
	<xsl:template match="campo"> 
	 <xsl:if test="@id!='_KML_'">
      <table>
        <tr>
         <td style="font-weight:bold;margin-left: 5px;margin-right: 10px;">
          <b><xsl:value-of select="@titolo"/>:</b>
         </td>
         <td>
	       <xsl:value-of select="setValori/valore"/>
	     </td>
        </tr>
      </table>
     </xsl:if>
    </xsl:template>
    
	<xsl:template match="testo"> 
      <xsl:value-of select="@valore"/>
    </xsl:template>
    
	<xsl:template match="link">
      <a>
        <xsl:attribute name="target">_blank</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
        <xsl:value-of select="@url"/>
      </a>
    </xsl:template>

	<xsl:template match="tabella"> 
      <table style="width:100%">
	    <thead>
	      <tr class="iceDatTblColHdr">
	         <xsl:for-each select="header/columnName">
	           <td><xsl:value-of select="." /></td>
	         </xsl:for-each>
	      </tr>
	    </thead>
	    <tbody>
	      <xsl:for-each select="rows">
	        <tr class="iceDatTblRow1">
	          <xsl:for-each select="rowElement">
	            <td class="iceDatTblCol1">
	              <xsl:apply-templates select="testo"/>
	              <xsl:apply-templates select="link"/>
	            </td>
	          </xsl:for-each>
	        </tr>
	      </xsl:for-each>
	    </tbody>
	  </table>
    </xsl:template>

</xsl:stylesheet>