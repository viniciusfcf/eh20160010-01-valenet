package br.com.vale.fis.routes;



import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

// import br.com.vale.fis.log.enums.EventCode;
// import br.com.vale.fis.log.enums.LogHeaders;

@Component
@java.lang.SuppressWarnings("all")
public class FromAMQToAzure extends RouteBuilder {
	
	  @Value("${app.global.id}")
	  private String globalId;

	  @Value("${activemq.queue.response}")
	  private String queueResponse;
		
	  @Value("${azure.endpoint}")
	  private String endpoint;
		
	  @Value("${azure.autorizationKey}")
	  private String autorizationKey;
		
	  @Value("${azure.soapAction}")
	  private String soapAction;
	  

  
  @Autowired
  private CamelContext ctx;
  @Autowired
  private static final String FUSE_LOG = "fuseLog";
  
  @PostConstruct
  void init() {
	  ctx.setMessageHistory(false);
	  ctx.disableJMX();
	  ctx.setAllowUseOriginalMessage(false);
  }

  @Override
  public void configure() throws Exception {
  
	  onException(Exception.class)
	      .handled(true)
	      .log("log(EventCode.E950,'Generic Error ${exception.message}')")
		  ;
    

	from("activemq:" + queueResponse.concat("?disableReplyTo=true"))
	    .routeId("FromAMQToAzure")
        .setProperty("LogHeaders.GLOBAL_ID.value", constant(globalId))
        .setProperty("LogHeaders.ROUTE_ID.value", simple("${routeId}"))
        .setProperty("LogHeaders.SYSTEM_NAME.value", simple("FUSE"))
        .log("log(" + "EventCode.V001" + ",'  Interface Started')")
        .log("log(" + "EventCode.V008" + ",' Get Master Data  - Started')")

		
		.setHeader("SOAPAction",simple(soapAction))
	    .setHeader(HttpHeaders.AUTHORIZATION,simple(autorizationKey))
	    .transform(simple("${body.replace('<getMasterDataResponse>', '<getMasterDataResponse xmlns=\"http://www.vale.com/EH/EH20160010_01/GetMasterData\">')}"))
		.setHeader(Exchange.CONTENT_TYPE,constant("text/xml;charset=utf-8"))
		.to("https://".concat(endpoint))
		.log("log(" + "EventCode.V108" + ",' Get Master Data - Finished')")
		.log("log(" + "EventCode.V100" + ",' Interface Finished')")
        ;
	  
	 

	}
}
