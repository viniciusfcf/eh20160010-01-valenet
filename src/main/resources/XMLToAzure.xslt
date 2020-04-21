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
									<DBKeyLocation><xsl:value-of select="DBKeyLocation"/></DBKeyLocation> 
									<IDLocation><xsl:value-of select="IDLocation"/></IDLocation>
									<Description><xsl:value-of select="Description"/></Description>
									<Language><xsl:value-of select="Language"/></Language>
								</GetLocationStructure>
	    					 </xsl:if>
		   				     <xsl:if test="$name='GetOrganizationalUnitStructure'">
		   				     	<GetOrganizationalUnitStructure> 	
									<IDOrgUnit><xsl:value-of select="IDOrgUnit"/></IDOrgUnit> 
									<Description><xsl:value-of select="Description"/></Description>
									<Language><xsl:value-of select="Language"/></Language>
									<ManagerName><xsl:value-of select="ManagerName"/></ManagerName>
									<OrgUnitLevel><xsl:value-of select="OrgUnitLevel"/></OrgUnitLevel>
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

