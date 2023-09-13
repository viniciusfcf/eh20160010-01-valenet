package br.com.vale.fis.routes;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import br.com.vale.fis.log.enums.EventCode;
import br.com.vale.fis.log.enums.LogHeaders;

@Component
public class PostAPIManagementToAMQ extends RouteBuilder {
	
	@Value("${app.global.id}")
	private String globalId;
	
	@Value("${activemq.queue.request}")
	private String queueRequest;
	
	@Value("${activemq.queue.request-dev}")
	private String queueRequestDev;

	@Value("${azure.endpoint}")
	private String endpoint;
	
	@Value("${azure.autorizationKey}")
	private String autorizationKey;
	
	@Value("${azure.soapAction}")
	private String soapAction;
	
	  @Autowired
	  private static final String FUSE_LOG = "fuseLog";
	
	@Override
	public void configure() throws Exception {			   

	   onException(Exception.class).handled(true)
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_XML_VALUE))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setBody(simple(createResponse("NOK", "${exception.message}")))
				.bean(FUSE_LOG,"log(" + EventCode.E950 + ",'Generic Error ${exception.message}')")
				;

		
		rest("/location") 
		  .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) 
		  .post("/structure") 
		  	.route()
		  	.routeId("PostCompanyStructure")
		  	.setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
		  	.setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
		  	.setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
		  	.bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Send Company Structure - Started')")
		  	.convertBodyTo(String.class, "UTF-8")
		  	.setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		    .to("amqValenet:".concat(queueRequest).concat("?disableReplyTo=true"))
			.bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Send Company Structure - Finished')")
           ;
	 
		
		rest("/organizational") 
			.consumes(MediaType.APPLICATION_XML_VALUE)
			.produces(MediaType.APPLICATION_XML_VALUE) .post("/unit") .route()
			.routeId("PostOrganizationalUnit")
		     .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
		     .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))  
		     .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
		 	 .bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Send Organizational Unit - Started')")
			 .convertBodyTo(String.class, "UTF-8")
			 .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
			 .to("amqValenet:".concat(queueRequest).concat("?disableReplyTo=true"))
			 .bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Send Organizational Unit - Finished')")
	        ;

		
		rest("/category") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/event") .route()
		  .routeId("PostCategoryEvent")
	      .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
	      .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
	      .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
	      .bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Send Category Event  - Started')")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequest).concat("?disableReplyTo=true"))
		  .bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Send Category Event  - Finished')");
		
		
		/** Configuração para o ambiente SAP-EQ0 **/
		
		rest("/location") 
		  .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) 
		  .post("/structure/DEV") 
		  .route()
		  .routeId("PostCompanyStructureDEV")
		  .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
		  .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
		  .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
		  .bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Send Company Structure (DEV) - Started')")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequestDev).concat("?disableReplyTo=true"))
		  .bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Send Company Structure (DEV) - Finished')")
          ;
	 
		
		rest("/organizational") 
			.consumes(MediaType.APPLICATION_XML_VALUE)
			.produces(MediaType.APPLICATION_XML_VALUE) 
			.post("/unit/DEV") 
			.route()
			.routeId("PostOrganizationalUnitDEV")
		     .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
		     .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
		     .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
		     .bean(FUSE_LOG,"log(" + EventCode.V001 + ",' Send Organizational Unit (DEV) - Started')")
			 .convertBodyTo(String.class, "UTF-8")
			 .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
			 .to("amqValenet:".concat(queueRequestDev).concat("?disableReplyTo=true"))
			 .bean(FUSE_LOG,"log(" + EventCode.V100 + ",' Send Organizational Unit (DEV) - Finished')")
	         ;

		
		rest("/category") 
		  .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) 
		  .post("/event/DEV") 
		  .route()
		  .routeId("PostCategoryEventDEV")
	      .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
	      .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
	      .setProperty(LogHeaders.SYSTEM_NAME.value, simple("FUSE"))
	      .bean(FUSE_LOG,"log(" + EventCode.V001 + ",'Send Category Event (DEV) - Started')")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequestDev).concat("?disableReplyTo=true"))
	      .bean(FUSE_LOG,"log(" + EventCode.V100 + ",'Send Category Event (DEV) - Finished')")
		  ;
		 
	}

	private String createResponse(String status, String messageError) {
		return "{\"status\": \"" + status + "\",\"messageError\": \"" + messageError + "\"}";
	}
}
