<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:variable name="context">/fascicolo/imieidati</xsl:variable>

    <xsl:variable name="txtNatoA" select="'Nato a'"/>
    <xsl:variable name="txtNataA" select="'Nata a'"/>
    <xsl:variable name="txtResidenteIn" select="'Residente in'"/>
    <xsl:variable name="txtCodiceFiscale" select="'Codice Fiscale'"/>

    <xsl:template match="testo"> 
        <xsl:value-of select="@valore"/>
    </xsl:template>
    
    <xsl:template match="tabella">
	<xsl:variable name="NOME" select="1"/>
	<xsl:variable name="CF" select="2"/>
	<xsl:variable name="NATO_A" select="4"/>
	<xsl:variable name="IL" select="5"/>
	<xsl:variable name="VIA" select="9"/>
	<xsl:variable name="NUM" select="10"/>


        <table style="width:100%">
            <xsl:for-each select="rows">
	    <xsl:variable name="FEMALE" select="substring(rowElement[$CF]/testo/@valore,10,2) &gt; 40"/>
            <tr class="iceDatTblRow1">
		<td class="accordion_td_header_image">
		    <img src="{$context}/img/user.png" id="accordion_td_image_imieidati" class="img_header_accordion"></img>
		</td>
		<td>
                    <xsl:value-of select="rowElement[$NOME]/testo/@valore"/><br/>
                    <xsl:if test="$FEMALE = true()"><xsl:value-of select="$txtNataA"/></xsl:if>
                    <xsl:if test="$FEMALE = false()"><xsl:value-of select="$txtNatoA"/></xsl:if>
		    <xsl:text> </xsl:text><xsl:value-of select="rowElement[$NATO_A]/testo/@valore"/>, <xsl:value-of select="rowElement[$IL]/testo/@valore"/><br/>
                    <xsl:value-of select="$txtResidenteIn"/><xsl:text> </xsl:text><xsl:value-of select="rowElement[$VIA]/testo/@valore"/>, <xsl:value-of select="rowElement[$NUM]/testo/@valore"/><br/>
                    <xsl:value-of select="$txtCodiceFiscale"/><xsl:text>: </xsl:text><xsl:value-of select="concat(substring(rowElement[$CF]/testo/@valore,1,3),' ',substring(rowElement[$CF]/testo/@valore,4,3),' ',substring(rowElement[$CF]/testo/@valore,7,5),' ',substring(rowElement[$CF]/testo/@valore,12,4),' ',substring(rowElement[$CF]/testo/@valore,16,1))"/><br/>
                </td>
	    </tr>
	    </xsl:for-each>
	</table>
    </xsl:template>

</xsl:stylesheet>

