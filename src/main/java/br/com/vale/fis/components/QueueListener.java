package br.com.vale.fis.components;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.stereotype.Component;

import br.com.vale.fis.connections.ValeAMQConnectionFactory;
import br.com.vale.fis.log.EventCode;

@Component
public class QueueListener {
	
	private static final Logger logger = LoggerFactory.getLogger(QueueListener.class);

	@Value("${amq.valenet.host}")
	private String host;
	@Value("${amq.valenet.user}")
	private String user;
	@Value("${amq.valenet.password}")
	private String passwd;

	@Autowired
	private CamelContext context;	

	@JmsListener(destination = "VALE.BR.AMQ.GETMASTERDATA.RESPONSE", containerFactory = "jmsFactory")
	public void devValenetConsumer(Message msg) throws JMSException {
		logger.info(EventCode.V001 + ", Message Received from VALE.BR.AMQ.GETMASTERDATA.RESPONSE.");
		context.createProducerTemplate().sendBody("direct:start", ((TextMessage) msg).getText());
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) throws Exception {
		return getConnection(connectionFactory, configurer, host, user, passwd);
	}

	private JmsListenerContainerFactory<?> getConnection(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer, String host, String userName, String passwd)
			throws Exception {
		ValeAMQConnectionFactory conn = new ValeAMQConnectionFactory();
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
		factory.setConcurrency("2-10");
		configurer.configure(factory, conn.getConnectionFactory("teste", host, userName, passwd, "teste"));
		return factory;
	}
}
