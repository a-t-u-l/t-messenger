package com.svatah.core;

import com.svatah.bean.MqttSettings;
import com.svatah.client.TMessengerGUI;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Scanner;

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class MQTTBase {

    public MqttClient connectClient(String clientId, String broker) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient sender = new MqttClient(broker, clientId, persistence);
        MqttConnectOptions connOpts1 = new MqttConnectOptions();
        connOpts1.setCleanSession(false);
        connOpts1.setKeepAliveInterval(60);
        System.out.println("Connecting to broker : " + broker);
        sender.connect(connOpts1);
        System.out.println("Connected");
        return sender;
    }

    public void subscribeClient(MqttClient client, String topic) throws MqttException {
        client.subscribe(topic, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String messageTxt = new String(message.getPayload());
                System.out.println(topic + " : " + messageTxt);
            }
        });
    }

    public void subscribeClient(MqttClient client, String topic, final TMessengerGUI instance) throws MqttException {
        client.subscribe(topic, new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String messageTxt = new String(message.getPayload());
                System.out.println(topic + " : " + messageTxt);
                instance.messageSetter(message.getId(), messageTxt);
            }
        });
    }

    public void senderPublish(MqttClient sender, String topic, String content, int qos) throws MqttException, MqttPersistenceException {
        System.out.println("Publishing message: " + content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(qos);
        sender.publish(topic, message);
        System.out.println("Message published");
    }

    public void disconnect(MqttClient sender) throws MqttException {
        sender.disconnect();
        System.out.println("Disconnected");
    }

//    public static void main(String[] args) {
//        Thread t1 = new Thread() {
//            @Override
//            public void run() {
//                Scanner sc = new Scanner(System.in);
//                MQTTBase send = new MQTTBase();
//                MqttSettings mqttSettings = new MqttSettings("topic1", "tcp://localhost:1883","user1");
//                while (true) {
//                    System.out.println("Enter message: ");
//                    String msg = sc.nextLine();
//                    if (msg.equals("$exit$")) {
//                        sc.close();
//                        System.exit(0);
//                    }
//                    try {
//                        MqttClient client = send.connectClient(mqttSettings.getClientId(), mqttSettings.getBroker());
//                        send.subscribeClient(client, mqttSettings.getTopic());
//                        send.senderPublish(client, mqttSettings.getTopic(), msg, mqttSettings.getQos());
//                    } catch (MqttException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        t1.start();
//
//        Thread t2 = new Thread() {
//            @Override
//            public void run() {
//                MQTTBase receive = new MQTTBase();
//                MqttClient client;
//                try {
//                    MqttSettings mqttSettings = new MqttSettings("topic2", "tcp://localhost:1883","user2");
//                    client = receive.connectClient(mqttSettings.getClientId(), mqttSettings.getBroker());
//                    receive.subscribeClient(client, mqttSettings.getTopic());
//                } catch (MqttException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        t2.start();
//
//    }

}
