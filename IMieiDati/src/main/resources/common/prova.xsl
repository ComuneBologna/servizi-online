<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:template match="*">
	 <html>
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
	  <fieldset class="fieldset-dati">
	   <legend><br><xsl:value-of select="@titolo"/></br></legend>  
	     <xsl:for-each select="elemento">
	       <xsl:call-template name="Elemento">
	         <xsl:with-param name="elemento" select="elemento"/>
	       </xsl:call-template>
	     </xsl:for-each>
	  </fieldset>
	</xsl:template>
	
	<xsl:template name="Elemento" mode="toc">
	  <xsl:param name="elemento"/>
	  <xsl:apply-templates select="testo"/>
	  <xsl:apply-templates select="campo"/>
	  <xsl:apply-templates select="tabella"/>
	  <xsl:apply-templates select="link"/>
	</xsl:template>
	
	
	<xsl:template match="campo">
      <table>
		<xsl:choose>
			<xsl:when test="@id='nome' or @id='cognome'">
		        <xsl:attribute name="style">display: inline-table;</xsl:attribute>
			</xsl:when>
		</xsl:choose>
        <tr>
         <td>
		 <xsl:choose>
			<xsl:when test="@id='nome'">
		        <xsl:attribute name="style">font-weight:bold;margin-left: 5px;margin-right: 10px;</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
		        <xsl:attribute name="style">color: red; font-weight:bold;margin-left: 5px;margin-right: 10px;</xsl:attribute>
			</xsl:otherwise>
		 </xsl:choose>	         
          <b><xsl:value-of select="@titolo"/>:</b>
         </td>
         <td>
         	<input type="text" readonly="readonly">
       			 <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
       			 <xsl:attribute name="value"><xsl:value-of select="setValori/valore"/></xsl:attribute>
         	</input>
	     </td>
        </tr>
      </table>
    </xsl:template>
	
	<!-- 
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
     -->
    
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
		<xsl:choose>
			<xsl:when test="@id='combo'">
		      <table style="display: inline-table;">
			    <tbody>
			        <tr class="iceDatTblRow1">
			        	<td>
				          <b><xsl:value-of select="@titolo"/>:</b>
			        	</td>
			            <td class="iceDatTblCol1">
			            	<select>
						      <xsl:for-each select="rows">
						          <xsl:for-each select="rowElement">
						          	<option>
						              <xsl:apply-templates select="testo"/>
									</option>						              
						          </xsl:for-each>
						      </xsl:for-each>
							</select>						      
			            </td>
			        </tr>
			    </tbody>
			  </table>			
			</xsl:when>
			<xsl:otherwise>
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
			</xsl:otherwise>
		</xsl:choose>	
    </xsl:template>

</xsl:stylesheet>