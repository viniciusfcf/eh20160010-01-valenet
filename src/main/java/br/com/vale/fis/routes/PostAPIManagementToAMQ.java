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
public class PostAPIManagementToAMQ extends RouteBuilder {
	
	
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
				.bean(ValeLog.class, "logging(" + EventCode.E950 + ", ${exception.message})");

		
		rest("/location") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/structure") .route()
		  .routeId("PostCompanyStructure")
		  .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		  .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
		  .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequest))
		  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
	 
		
		rest("/organizational") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/unit") .route()
		  .routeId("PostOrganizationalUnit")
		  .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		  .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
		  .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequest))
		  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
		 
		rest("/category") .consumes(MediaType.APPLICATION_XML_VALUE)
		  .produces(MediaType.APPLICATION_XML_VALUE) .post("/event") .route()
		  .routeId("PostCategoryEvent")
		  .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		  .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
		  .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqValenet:".concat(queueRequest))
		  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
		 
		
		
	}

	private String createResponse(String status, String messageError) {
		return "{\"status\": \"" + status + "\",\"messageError\": \"" + messageError + "\"}";
	}
}
