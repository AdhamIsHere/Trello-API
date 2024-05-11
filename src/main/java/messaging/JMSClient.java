package Messaging;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;

@Startup
@Singleton

public class JMSClient {

	@Resource(mappedName = "java:/jms/queue/DLQ")
	Queue queue;

	@Inject
	JMSContext context;

	public void sendMessage(String msg) {
		try {
			JMSProducer producer = context.createProducer();
			TextMessage message = (TextMessage) context.createTextMessage(msg);
			producer.send(queue, message);
			System.out.println("the message is :" + message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
