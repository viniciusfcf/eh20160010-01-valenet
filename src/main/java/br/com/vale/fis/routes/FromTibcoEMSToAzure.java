package br.com.vale.fis.routes;

import br.com.vale.fis.components.ValeTibcoEMSComponent;
import br.com.vale.fis.log.EventCode;
import br.com.vale.fis.log.ValeLog;
import br.com.vale.fis.log.ValeLogger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class FromTibcoEMSToAzure extends RouteBuilder {
	

		@Value("${tibco.queueOut}")
		private String tibcoQueueOut;
	
		@Value("${tibco.host}")
		private String host;
	
		@Value("${tibco.user}")
		private String user;
	
		@Value("${tibco.password}")
		private String password;

		@Value("${azure.endpoint}")
		private String endpoint;
		
		@Value("${azure.autorizationKey}")
		private String autorizationKey;
		
		@Value("${azure.soapAction}")
		private String soapAction;

  @Override
  public void configure() throws Exception {
  
	  onException(Exception.class)
	      .handled(true)
	      .bean(ValeLog.class, "logging(" + EventCode.E950 + ", ${exception.message})");
    
	  from("tibco:".concat(tibcoQueueOut)) 
	    .routeId("FromTibcoEMSToAzure")
		.setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		.bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		.setHeader("SOAPAction",simple(soapAction))
	    .setHeader(HttpHeaders.AUTHORIZATION,simple(autorizationKey))
		.to("xslt:XMLToAzure.xslt")
		.setHeader(Exchange.CONTENT_TYPE,constant("text/xml"))
		.inOnly ("https4://".concat(endpoint))
		.log(Exchange.HTTP_RESPONSE_TEXT)
		.bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");

	}
}
