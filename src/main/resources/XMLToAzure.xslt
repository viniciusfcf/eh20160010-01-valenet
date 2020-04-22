<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output omit-xml-declaration="no" indent="yes"/>
	<xsl:template match="/getMasterDataResponse/obj">
	<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"  
				   xmlns:get="http://www.vale.com/EH/EH20160010_01/GetMasterData">
			<soap:Body>
				<getMasterDataResponse>
					<obj>
						<Header>
							<messageID><xsl:value-of select="Header/messageID"/></messageID> 
						</Header>
						<Body>
	   				     <xsl:for-each select="Body/*">
		   				     <xsl:variable name="name" select="string(local-name())"/>
		   				     <xsl:if test="$name='GetLocationStructure'">
	   				     		<GetLocationStructure> 	
									<xsl:if test="DBKeyLocation!=''">
									    <DBKeyLocation><xsl:value-of select="DBKeyLocation"/></DBKeyLocation>
									</xsl:if>
									<xsl:if test="IDLocation!=''">
									    <IDLocation><xsl:value-of select="IDLocation"/></IDLocation>
									</xsl:if>
									<xsl:if test="Description!=''">
									    <Description><xsl:value-of select="Description"/></Description>
									</xsl:if>
									<xsl:if test="Language!=''">
									    <Language><xsl:value-of select="Language"/></Language>
									</xsl:if>
								</GetLocationStructure>
	    					 </xsl:if>
		   				     <xsl:if test="$name='GetOrganizationalUnitStructure'">
		   				     	<GetOrganizationalUnitStructure> 	
 								    <xsl:if test="IDOrgUnit!=''">
									    <IDOrgUnit><xsl:value-of select="IDOrgUnit"/></IDOrgUnit>
									</xsl:if>
 								    <xsl:if test="Description!=''">
									    <Description><xsl:value-of select="Description"/></Description>
									</xsl:if>
								    <xsl:if test="Language!=''">
									    <Language><xsl:value-of select="Language"/></Language>
									</xsl:if>
								    <xsl:if test="ManagerName!=''">
									    <ManagerName><xsl:value-of select="ManagerName"/></ManagerName>
									</xsl:if>
									<xsl:if test="OrgUnitLevel!=''">
										<OrgUnitLevel><xsl:value-of select="OrgUnitLevel"/></OrgUnitLevel>
									</xsl:if>
								</GetOrganizationalUnitStructure>
			    			 </xsl:if>
  		                 </xsl:for-each>	
						</Body>
					</obj>
				</getMasterDataResponse>
			</soap:Body>
		</soap:Envelope>
	</xsl:template>
</xsl:stylesheet>

