<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!-- Identity template, copies everything as is -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <!-- Override for target element -->
    <xsl:template match="units">
        <!-- Add new node -->
		<references size='4'>
		    <repository uri='https://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/' url='https://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/' type='0' options='1'/>
		    <repository uri='https://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/' url='https://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/' type='1' options='1'/>
		    <repository uri='http://download.eclipse.org/sirius/updates/releases/6.2.2/2019-06' url='http://download.eclipse.org/sirius/updates/releases/6.2.2/2019-06' type='1' options='1'/>
		    <repository uri='http://download.eclipse.org/sirius/updates/releases/6.2.2/2019-06' url='http://download.eclipse.org/sirius/updates/releases/6.2.2/2019-06' type='0' options='1'/>
  		</references>
		<!-- Copy the element -->
        <xsl:copy>
            <!-- And everything inside it -->
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
