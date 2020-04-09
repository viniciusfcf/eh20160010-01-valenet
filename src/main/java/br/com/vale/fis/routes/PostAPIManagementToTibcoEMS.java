package br.com.vale.fis.routes;

import br.com.vale.fis.components.ValeTibcoEMSComponent;
import br.com.vale.fis.log.EventCode;
import br.com.vale.fis.log.ValeLog;
import br.com.vale.fis.log.ValeLogger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class PostAPIManagementToTibcoEMS extends RouteBuilder {

  @Value("${tibco.queue}")
  private String tibcoQueue;

  @Value("${tibco.host}")
  private String host;

  @Value("${tibco.user}")
  private String user;

  @Value("${tibco.password}")
  private String password;

  @Override
  public void configure() throws Exception {

    getContext().addComponent("tibco", new ValeTibcoEMSComponent(host, user, password).getTibcoComponent());

    onException(Exception.class)
    .handled(true)
    .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_XML_VALUE))
    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
    .setBody(simple(createResponse("NOK", "${exception.message}")))
    .bean(ValeLog.class, "logging(" + EventCode.E950 + ", ${exception.message})");
   
    rest("/company")
    .consumes(MediaType.APPLICATION_XML_VALUE)
    .produces(MediaType.APPLICATION_XML_VALUE)
    .post("/structure")
      .route()
      .routeId("PosCompanyStructure")
      .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
      .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
      .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
      .convertBodyTo(String.class, "UTF-8")
      .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
      .to("tibco:".concat(tibcoQueue))
      .end()
	  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
    
    rest("/organizational")
    .consumes(MediaType.APPLICATION_XML_VALUE)
    .produces(MediaType.APPLICATION_XML_VALUE)
    .post("/unit")
      .route()
      .routeId("PosOrganizationalUnit")
      .setHeader(ValeLogger.ROUTE_ID.getValue()).simple("${routeId}")
      .setHeader(ValeLogger.LOG_BODY.getValue()).simple("false")
      .bean(ValeLog.class, "logging(" + EventCode.V001 + ", Start)")
      .convertBodyTo(String.class, "UTF-8")
      .setHeader("CamelHttpCharacterEncoding", constant("UTF-8"))
      //.to("tibco:".concat(tibcoQueue))
      .to("mock:xml")
      .end()
	  .bean(ValeLog.class, "logging(" + EventCode.V100 + ", Finished)");
    
	}

	private String createResponse(String status, String messageError) {
		return "{\"status\": \"" + status + "\",\"messageError\": \"" + messageError + "\"}";
	}
}
