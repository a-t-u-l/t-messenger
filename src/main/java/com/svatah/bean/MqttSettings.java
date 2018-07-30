package com.svatah.bean;

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class MqttSettings {

    private String topic;
    private String broker;
    //by default QoS is 1
    private int qos;
    private String clientId;

    public MqttSettings(String topic, String broker) {
        this.topic = topic;
        this.broker = broker;
        this.qos = 1;
    }

    public MqttSettings(String topic, String broker, String clientId) {
        this.topic = topic;
        this.broker = broker;
        this.clientId = clientId;
        this.qos = 1;
    }

    public MqttSettings(String topic, String broker,String clientId, int qos) {
        this.topic = topic;
        this.broker = broker;
        this.clientId = clientId;
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }


    public String getBroker() {
        return broker;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }
}
