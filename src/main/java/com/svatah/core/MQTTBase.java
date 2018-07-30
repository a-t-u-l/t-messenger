package com.svatah.core;

import com.svatah.client.JavaClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class MQTTBase {

	private static String topic = "MQTT";
	private static String broker = "tcp://localhost:1883";

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		MQTTBase.topic = topic;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		MQTTBase.broker = broker;
	}

	public MqttClient connectClient(String clientId, int qos) throws MqttException {
		MemoryPersistence persistence = new MemoryPersistence();
		MqttClient sender = new MqttClient(broker, clientId, persistence);
		MqttConnectOptions connOpts1 = new MqttConnectOptions();
		connOpts1.setCleanSession(false);
		connOpts1.setKeepAliveInterval(60);
		System.out.println("Connecting to broker: "+broker);
		sender.connect(connOpts1);
		System.out.println("Connected");
		return sender;
	}

	public void subscribeClient(MqttClient client, final JavaClient clientCall) throws MqttException{
		client.subscribe(topic, new IMqttMessageListener() {
			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				String messageTxt = new String(message.getPayload());
				System.out.println(topic + "> " + messageTxt);
				clientCall.messageListener(message.getId(), messageTxt);
				return;
			}
		});
	}

	public void senderPublish(MqttClient sender, String content, int qos) throws MqttPersistenceException, MqttException {
		System.out.println("Publishing message: "+content);
		MqttMessage message = new MqttMessage(content.getBytes());
		message.setQos(qos);
		sender.publish(topic, message);
		System.out.println("Message published");
	}

	public void disconnect(MqttClient sender) throws MqttException{
		sender.disconnect();
		System.out.println("Disconnected");
	}
	
	public static void main(String [] args){
		Thread t1=new Thread(){
			@Override
			public void run(){
				Scanner sc=new Scanner(System.in);
				MQTTBase send=new MQTTBase();
				while(true){
					System.out.println("Enter message: ");
					String msg=sc.nextLine();
					if(msg.equals("$exit$")){
						sc.close();
						System.exit(0);
					}
					try {
						MqttClient client=send.connectClient("atulsharma",2);
						send.subscribeClient(client, null);
						send.senderPublish(client, msg, 2);
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t1.start();

		Thread t2=new Thread(){
			@Override
			public void run(){
				MQTTBase receive=new MQTTBase();
				MqttClient client;
				try {
					client = receive.connectClient("atulsharma",2);
					receive.subscribeClient(client, null);
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t2.start();
	}

}
