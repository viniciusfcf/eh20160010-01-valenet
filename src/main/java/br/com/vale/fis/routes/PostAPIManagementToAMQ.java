package br.com.vale.fis.routes;


import br.com.vale.fis.log.EventCode;
import br.com.vale.fis.log.LogHeaders;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class PostAPIManagementToAMQ extends RouteBuilder {
	
	@Value("${app.global-id}")
	private String globalId;
	
	@Value("${activemq.queue.request}")
	private String queueRequest;

	@Value("${azure.endpoint}")
	private String endpoint;
	
	@Value("${azure.autorizationKey}")
	private String autorizationKey;
	
	@Value("${azure.soapAction}")
	private String soapAction;
	
	
	@Override
	public void configure() throws Exception {			   

	   onException(Exception.class).handled(true)
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_XML_VALUE))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setBody(simple(createResponse("NOK", "${exception.message}")))
				.log(EventCode.E950 + ", ${exception.message}");;

		
		rest("/location") 
		  .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) 
		  .post("/structure") 
		  	.route()
		  	.routeId("PostCompanyStructure")
		  	.setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
		  	.setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
		  	.log(EventCode.V001 + ", Send Company Structure - Started")
		  	.convertBodyTo(String.class, "UTF-8")
		  	.setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		    .inOnly("amqValenet:".concat(queueRequest))
            .log(EventCode.V100 + ", Send Company Structure - Finished");
	 
		
		rest("/organizational") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/unit") .route()
		  .routeId("PostOrganizationalUnit")
	      .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
	      .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
	      .log(EventCode.V001 + ", Send Organizational Unit - Started")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .inOnly("amqValenet:".concat(queueRequest))
          .log(EventCode.V100 + ", Send Organizational Unit - Finished");

		
		rest("/category") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/event") .route()
		  .routeId("PostCategoryEvent")
	      .setProperty(LogHeaders.GLOBAL_ID.value, constant(globalId))
	      .setProperty(LogHeaders.ROUTE_ID.value, simple("${routeId}"))
	      .log(EventCode.V001 + ", Send Category Event  - Started")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .inOnly("amqValenet:".concat(queueRequest))
          .log(EventCode.V100 + ", Send Category Event - Finished");
		 
	}

	private String createResponse(String status, String messageError) {
		return "{\"status\": \"" + status + "\",\"messageError\": \"" + messageError + "\"}";
	}
}
