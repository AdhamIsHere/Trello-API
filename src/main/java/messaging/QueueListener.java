package messaging;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.TextMessage;

import java.io.Serializable;


@MessageDriven(name = "queue", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/DLQ"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class QueueListener implements MessageListener {
	
	
	public void onMessage(Message recivedMessage) 
	{
		TextMessage msg = null ;
		try 
		{
			if(recivedMessage instanceof TextMessage) 
			{
				msg = (TextMessage) recivedMessage ;
				System.out.println("the message that was recieved : " + msg.getText());
				
			}
		}
		catch(JMSException e) 
		{
			throw new RuntimeException(e);
		}
	}
	

}
