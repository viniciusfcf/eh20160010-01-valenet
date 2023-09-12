package br.com.vale.fis;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public ServletRegistrationBean camelServletRegistrationBean() {
    ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/vale/fis/api/*");
    registration.setName("CamelServlet");
    return registration;
  }
}
