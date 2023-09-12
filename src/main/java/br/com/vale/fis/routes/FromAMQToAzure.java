package br.com.vale.fis.routes;


import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import br.com.vale.fis.log.enums.EventCode;
import br.com.vale.fis.log.enums.LogHeaders;

@Component
public class FromAMQToAzure extends RouteBuilder {
	
  @Value("${app.global.id}")
  private String globalId;

  @Value("${activemq.queue.response}")
  private String queueResponse;
  
  
  @Value("${activemq.queue.response-dev}")
  private String queueResponseDev;
	
  @Value("${azure.endpoint}")
  private String endpoint;
  
  @Value("${azure.endpoint-dev}")
  private String endpointDev;
	
  @Value("${azure.autorizationKey}")
  private String autorizationKey;
	
  @Value("${azure.soapAction}")
  private String soapAction;
  
  @Autowired
  private CamelContext ctx;
  @Autowired
  private static final String FUSE_LOG = "fuseLog";
  
  @PostConstruct
  private void init() {
	  ctx.setMessageHistory(false);
	  ctx.disableJMX();
	  ctx.setAllowUseOriginalMessage(false);
  }

  @Override
  public void configure() throws Exception {
  
	  onException(Exception.class)
	      .handled(true)
	      .bean(FUSE_LOG,"log(" + EventCode.E950 + ",'Generic Error ${exception.message}')")
		  ;
    

	  from("amqValenet:" + queueResponse)
	    .routeId("FromAMQToAzure")
        .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
        .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
        .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
        .bean(FUSE_LOG,"log(" + EventCode.V001 + ",'  Interface Started')")
        .bean(FUSE_LOG,"log(" + EventCode.V008 + ",' Get Master Data  - Started')")

		
		.setHeader("SOAPAction",simple(soapAction))
	    .setHeader(HttpHeaders.AUTHORIZATION,simple(autorizationKey))
	    .transform(simple("${body.replace('<getMasterDataResponse>', '<getMasterDataResponse xmlns=\"http://www.vale.com/EH/EH20160010_01/GetMasterData\">')}"))
		.setHeader(Exchange.CONTENT_TYPE,constant("text/xml;charset=utf-8"))
		.to("https://".concat(endpoint))
		.bean(FUSE_LOG,"log(" + EventCode.V108 + ",' Get Master Data - Finished')")
		.bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Interface Finished')")
        ;
	  
	  
	  /** Configuração para o ambiente SAP-EQ0 **/
	  
	
	  from("amqValenet:" + queueResponseDev)
		  .routeId("FromAMQToAzureDEV")
	      .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
	      .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
	      .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
	      .bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Interface Started (DEV)')")
	      .bean(FUSE_LOG,"log(" + EventCode.V008 + ",' Get Master Data (DEV) - Started')")
	  
			
	      .setHeader("SOAPAction",simple(soapAction))
		  .setHeader(HttpHeaders.AUTHORIZATION,simple(autorizationKey))
		  .transform(simple("${body.replace('<getMasterDataResponse>', '<getMasterDataResponse xmlns=\"http://www.vale.com/EH/EH20160010_01/GetMasterData\">')}"))
		  .setHeader(Exchange.CONTENT_TYPE,constant("text/xml;charset=utf-8"))
		  .to("https://".concat(endpointDev))
		  .bean(FUSE_LOG,"log(" + EventCode.V108 + ",' Get Master Data (DEV) - Finished')")
		  .bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Interface Finished (DEV)')")
	      ;

	}
}
