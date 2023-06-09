package com.openhealth.demo;

import java.sql.Timestamp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		
		SpringApplication.run(DemoApplication.class, args);

	}
	@Bean
	public MessageChannel mqttInputChannel() {
			return new DirectChannel();
	}

	@Bean
	public MessageProducer inbound() {
		System.out.println("connecting to mtqq....");
			MqttPahoMessageDrivenChannelAdapter adapter =
							new MqttPahoMessageDrivenChannelAdapter("tcp://192.168.178.67:1883", "testClient1",
																							 "test/message");
			adapter.setCompletionTimeout(5000);
			adapter.setConverter(new DefaultPahoMessageConverter());
			adapter.setQos(1);
			adapter.setOutputChannel(mqttInputChannel());
			return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
			return new MessageHandler() {

					@Override
					public void handleMessage(Message<?> message) throws MessagingException {
						  
							System.out.println("Door status: " + message.getPayload());
							jdbcTemplate().update("INSERT INTO events(timestamp, message) values(?,?)", new Timestamp(System.currentTimeMillis()), message.getPayload());
					}

			};	
		}
	@Bean
  public JdbcTemplate jdbcTemplate() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://localhost:5432/openhealth");
    dataSource.setUsername("admin");
    dataSource.setPassword("Redhat1!");

    return new JdbcTemplate(dataSource);
}
	
}
