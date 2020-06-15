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
public class PostSVAToAMQ extends RouteBuilder {

	  @Value("${activemq.queue}")
	  private String queueRequest;

	
	@Override
	public void configure() throws Exception {


		onException(Exception.class).handled(true)
				.setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
				.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
				.setBody(simple(createResponse("NOK", "${exception.message}")))
				.bean(ValeLog.class, "logging(" + EventCode.E950 + ", ${exception.message})");

		
		rest("/sva") 
		  .consumes(MediaType.APPLICATION_JSON_VALUE)
		  .produces(MediaType.APPLICATION_JSON_VALUE)
		  .post("/evento") 
		  .route()
		  .routeId("PostSvaSmartEventos")
		  .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		  .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
		  .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqOt:".concat(queueRequest))
		  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
		
		
		rest("/sva") 
		  .consumes(MediaType.APPLICATION_JSON_VALUE)
		  .produces(MediaType.APPLICATION_JSON_VALUE)
		  .post("/mensagens") 
		  .route()
		  .routeId("PostSvaSmartMensagen")
		  .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
		  .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
		  .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
		  .convertBodyTo(String.class, "UTF-8")
		  .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
		  .to("amqOt:".concat(queueRequest))
		  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
		 
		
	}

	private String createResponse(String status, String messageError) {
		return "{\"status\": \"" + status + "\",\"messageError\": \"" + messageError + "\"}";
	}
}
