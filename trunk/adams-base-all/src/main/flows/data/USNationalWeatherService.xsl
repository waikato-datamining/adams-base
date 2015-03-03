<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="/">
  <xsl:text>id&#9;title&#9;event&#9;effective&#9;expires&#9;status&#9;msg-type&#9;category&#9;urgency&#9;severity&#9;certainty&#9;areaDesc&#10;</xsl:text>
  <xsl:for-each select="feed/entry">
    <xsl:value-of select="id"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="title"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_event"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_effective"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_expires"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_status"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_msgType"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_category"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_urgency"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_severity"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_certainty"/>
    <xsl:text>&#9;</xsl:text>
    <xsl:value-of select="cap_areaDesc"/>
    <xsl:text>&#10;</xsl:text>
  </xsl:for-each>
</xsl:template>

</xsl:stylesheet>