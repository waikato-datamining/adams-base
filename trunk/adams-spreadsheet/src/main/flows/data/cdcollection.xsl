<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="/">
  <xsl:text>title,artist,country,company,price,year&#10;</xsl:text>
  <xsl:for-each select="catalog/cd">
    <xsl:value-of select="title"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="artist"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="country"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="company"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="price"/>
    <xsl:text>,</xsl:text>
    <xsl:value-of select="year"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>