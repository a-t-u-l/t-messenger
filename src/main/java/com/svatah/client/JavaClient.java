package com.svatah.client;

import com.svatah.core.MQTTBase;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class JavaClient extends JFrame implements ActionListener {

    private static final long serialVersionUID = -8320395422339014302L;
    private MqttClient client;
    int chatCounter = 0;
    private JTextField username;
    private JLabel messageLabel, statusLabel;
    private JTextArea messageWriteBox;
    private JTextArea messageDisplayBox;
    private JButton sendButton, disconnectButton, userButton;

    JavaClient() {
        super("T Messenger");

        username = new JTextField();
        username.setBounds(45, 24, 147, 20);
        username.setText("set user");

        userButton = new JButton("Set User");
        userButton.setBounds(210, 20, 146, 30);//x,y,w,h
        userButton.addActionListener(this);

        statusLabel = new JLabel("Status : ");
        statusLabel.setBounds(50, 50, 200, 20);

        messageLabel = new JLabel("Messages");
        messageLabel.setBounds(50, 80, 100, 20);

        messageWriteBox = new JTextArea();
        messageWriteBox.setBounds(50, 320, 300, 40);
        messageWriteBox.setLineWrap(true);
        messageWriteBox.setWrapStyleWord(true);
        JScrollPane scrollWriteBox = new JScrollPane(messageWriteBox,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        messageDisplayBox = new JTextArea();
        messageDisplayBox.setBounds(50, 110, 300, 200);
        messageDisplayBox.setEditable(false);
        messageDisplayBox.setLineWrap(true);
        messageDisplayBox.setWrapStyleWord(true);
        JScrollPane scrollDisplayBox = new JScrollPane(messageDisplayBox,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        sendButton = new JButton("send");
        sendButton.setBounds(40, 375, 147, 30);//x,y,w,h
        sendButton.addActionListener(this);

        disconnectButton = new JButton("disconnect");
        disconnectButton.setBounds(210, 375, 147, 30);//x,y,w,h
        disconnectButton.addActionListener(this);

        add(messageLabel);
        add(statusLabel);
        add(username);
        add(messageWriteBox);
        add(sendButton);
        add(disconnectButton);
        add(userButton);
        add(messageDisplayBox);
        add(scrollWriteBox);
        add(scrollDisplayBox);


        setSize(400, 440);
        setResizable(false);
        setLayout(null);//using no layout manager
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void messageListener(int msgId, String msg) {
        System.out.println("msg Id : " + msgId);
        String str = messageDisplayBox.getText();
        String msgStr = "";
        String[] msgList = str.split("\\r?\\n");
        int displayCount = msgList.length - 10;
        if (displayCount > 0) {
            for (int index = displayCount; index < msgList.length; index++) {
                msgStr = msgStr + msgList[index] + "\n";
            }
            messageDisplayBox.setText(msgStr + msg);
        } else
            messageDisplayBox.setText(messageDisplayBox.getText() + "\n" + msg);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            MQTTBase send = new MQTTBase();
            if (e.getSource() == userButton || userButton.isEnabled() == false) {
                if (userButton.isEnabled() == true) {
                    String user = username.getText();
                    client = send.connectClient(user, 2);
                    statusLabel.setText("Status : Connected");
                    messageDisplayBox.setText(null);
                    username.setEnabled(false);
                    userButton.setEnabled(false);
                    send.subscribeClient(client, this);
                    send.senderPublish(client, "came online", 2);
                }
                if (e.getSource() == sendButton) {
                    String text = messageWriteBox.getText();
                    messageWriteBox.setText("");
                    try {
                        send.senderPublish(client, text, 2);
                    } catch (MqttException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                } else if (e.getSource() == disconnectButton) {
                    try {
                        send.senderPublish(client, "went offline", 2);
                        send.disconnect(client);
                        statusLabel.setText("Status : disonnected");
                        username.setEnabled(true);
                        userButton.setEnabled(true);
                    } catch (MqttException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }

        } catch (MqttException e1) {
            // TODO Auto-generated catch block
            MQTTBase mqtt = new MQTTBase();
            e1.printStackTrace();
            messageDisplayBox.setText("broker : " + mqtt.getBroker() + "\nTopic : " + mqtt.getTopic() + "\n" + e1.getCause().toString());
        }
    }
}  