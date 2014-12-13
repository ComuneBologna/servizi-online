<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:variable name="context">/fascicolo/imieidati</xsl:variable>

    <xsl:variable name="txtCodiceFiscale" select="'CF'"/>
    <xsl:variable name="txtNatoA" select="'Nato a'"/>
    <xsl:variable name="txtNataA" select="'Nata a'"/>
    <xsl:variable name="txtIl" select="'il'"/>

    <xsl:template match="testo"> 
        <xsl:value-of select="@valore"/>
    </xsl:template>
    
    <xsl:template match="tabella">
	<xsl:variable name="NOME" select="1"/>
	<xsl:variable name="CF" select="2"/>
	<xsl:variable name="NATO_A" select="3"/>
	<xsl:variable name="PROV" select="4"/>
	<xsl:variable name="IL" select="5"/>

        <table style="width:100%">
            <xsl:for-each select="rows">
	    <xsl:variable name="FEMALE" select="substring(rowElement[$CF]/testo/@valore,10,2) &gt; 40"/>
            <tr class="iceDatTblRow1">
		<td class="accordion_td_header_image">
                  <xsl:if test="$FEMALE = true()">
		      <img src="{$context}/img/female.png" id="accordion_td_image_imieidati" class="img_header_accordion"></img>
                  </xsl:if>
                  <xsl:if test="$FEMALE = false()">
		      <img src="{$context}/img/male.png" id="accordion_td_image_imieidati" class="img_header_accordion"></img>
                  </xsl:if>
		</td>
		<td>
                    <xsl:value-of select="translate(rowElement[$NOME]/testo/@valore,'/',' ')"/><xsl:text>, </xsl:text>
                    <xsl:value-of select="$txtCodiceFiscale"/><xsl:text>: </xsl:text><xsl:value-of select="rowElement[$CF]/testo/@valore"/>, <br/>
                    <xsl:if test="$FEMALE = true()"><xsl:value-of select="$txtNataA"/></xsl:if>
                    <xsl:if test="$FEMALE = false()"><xsl:value-of select="$txtNatoA"/></xsl:if>
		    <xsl:text> </xsl:text><xsl:value-of select="rowElement[$NATO_A]/testo/@valore"/> - 
                    <xsl:value-of select="rowElement[$PROV]/testo/@valore"/><xsl:text>, </xsl:text>
                    <xsl:value-of select="$txtIl"/><xsl:text> </xsl:text><xsl:value-of select="rowElement[$IL]/testo/@valore"/><br/>
                </td>
	    </tr>
	    </xsl:for-each>
	</table>
    </xsl:template>

</xsl:stylesheet>

