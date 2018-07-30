package com.svatah;

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.logging.SimpleLogFormatter;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Test;

public class MqttPublishSampleTest{

	String topic        = "MQTT";
	String broker       = "tcp://localhost:1883";

	@Test
	public void clientTest(){
		Thread t1=new Thread(){
			@Override
			public void run(){
				Scanner sc=new Scanner(System.in);
				MqttPublishSampleTest send=new MqttPublishSampleTest();
				while(true){
					System.out.println("Enter message: ");  
					String msg=sc.nextLine();  
					if(msg.equals("$exit$")){
						sc.close();
						System.exit(0);
					}
					send.testMQTTSender("atulsharma",msg,2);
				}
			}
		};
		t1.start();

		Thread t2=new Thread(){
			@Override
			public void run(){
				MqttPublishSampleTest recieve=new MqttPublishSampleTest();
				recieve.testMQTTReciever("testuser");
			}
		};
		t2.start();
	}

	public void testMQTTSender(String clientId, String content, int qos){
		System.out.println("woking in thread number "+Thread.currentThread().getId()+".");  
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient sender = null;
		try {
			sender = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts1 = new MqttConnectOptions();
			connOpts1.setCleanSession(false);
			connOpts1.setKeepAliveInterval(60);
			System.out.println("Connecting to broker: "+broker);
			sender.connect(connOpts1);
			System.out.println("Connected");
			sender.subscribe(topic);
			System.out.println("Publishing message: "+content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(qos);
			sender.publish(topic, message);
			System.out.println("Message published");
			sender.disconnect();
			System.out.println("Disconnected");

		} catch(MqttException me) {
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
			try {
				sender.disconnect();
			} catch (MqttException e) {
				e.printStackTrace();
			}
			System.out.println("Disconnected");
		}

	}

	public void testMQTTReciever(String clientId){
		System.out.println("woking in thread number "+Thread.currentThread().getId()+".");  
		MemoryPersistence persistence = new MemoryPersistence();
		final MqttClient reciever;		
		try {
			reciever=new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();

			connOpts.setCleanSession(false);
			connOpts.setKeepAliveInterval(60);
			System.out.println("Connecting to broker: "+broker);
			reciever.connect(connOpts);
			System.out.println("connected ....");

			reciever.subscribe(topic, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String messageTxt = new String(message.getPayload());
					System.out.println(topic + "> " + messageTxt);	               
				}
			});
			
			Handler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(Level.FINEST);
			consoleHandler.setFormatter(new SimpleLogFormatter());
			Logger.getLogger("org.eclipse.paho").addHandler(consoleHandler);
			Logger.getLogger("org.eclipse.paho.client.mqttv3.internal.CommsSender").setLevel(Level.FINEST);
			Logger.getLogger("org.eclipse.paho.client.mqttv3.internal.CommsCallback").setLevel(Level.FINEST);
			Logger.getLogger("org.eclipse.paho.client.mqttv3.internal.CommsReceiver").setLevel(Level.FINEST);
			
		} catch(MqttException me) {
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		}
	}

}