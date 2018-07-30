package com.svatah.bean;

import com.svatah.core.MQTTBase;

/**
 * Created by AtulSharma on 30/07/18
 */
public class MqttSettings {

    private String topic;
    private String broker;
    //by default QoS is 2
    private int qos;
    private String clientId;

    public MqttSettings(String topic, String broker, String clientId) {
        this.topic = topic;
        this.broker = broker;
        this.qos = 2;
    }

    public MqttSettings(String topic, String broker,String clientId, int qos) {
        this.topic = topic;
        this.broker = broker;
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

    public String getClientId() {
        return clientId;
    }
}
