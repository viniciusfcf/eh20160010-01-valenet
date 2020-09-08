package br.com.vale.fis.routes;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import br.com.vale.fis.components.ValeTibcoEMSComponent;
import br.com.vale.fis.log.EventCode;
import br.com.vale.fis.log.LogHeaders;

@Component
public class FromTibcoEMSToAzure extends RouteBuilder {
	
  @Value("${app.global-id}")
  private String globalId;

  @Value("${tibco.queueIn}")
  private String tibcoQueueIn;
 
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
		  .log(EventCode.E950 + ", ${exception.message}");

	  from("tibco:".concat(tibcoQueueOut)) 
	    .routeId("FromAMQToAzure")
        .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
        .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
        .log(EventCode.V001 + ", Get Master Data  - Started")
		
		.setHeader("SOAPAction",simple(soapAction))
	    .setHeader(HttpHeaders.AUTHORIZATION,simple(autorizationKey))
	    .transform(simple("${body.replace('<getMasterDataResponse>', '<getMasterDataResponse xmlns=\"http://www.vale.com/EH/EH20160010_01/GetMasterData\">')}"))
		.setHeader(Exchange.CONTENT_TYPE,constant("text/xml;charset=utf-8"))
		.inOnly ("https4://".concat(endpoint))
        .log(EventCode.V100 + ", Get Master Data - Finished");

	}
}
