package com.svatah.client;

import com.svatah.bean.MqttSettings;
import com.svatah.core.MQTTBase;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class TMessengerGUI {

    private static final JPanel panel = new JPanel();
    private static JTextArea messageDisplayBox = new JTextArea();
    private static MqttClient client;
    private static MqttSettings mqttSettings;

    public static void main(String[] args) {
        TMessengerGUI window = new TMessengerGUI();
        window.firstWindow(window);
    }

    private void firstWindow(final TMessengerGUI instance){
        final JTextField broker;
        final JTextField topic;

        final JFrame frame = new JFrame("T Messenger");
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

        panel.setLayout(boxlayout);

        panel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));

        broker = new JTextField();
        broker.setColumns(20);
        broker.setText("tcp://localhost:1883");

        topic = new JTextField();
        topic.setColumns(20);

        JButton startMessenger = new JButton("OK");
        startMessenger.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(topic.getText()!=null && !topic.getText().isEmpty() && broker.getText()!=null && !broker.getText().isEmpty()) {
                    mqttSettings = new MqttSettings(topic.getText(), broker.getText());
                    panel.setVisible(false);
                    frame.setVisible(false);
                    messenger(instance);
                }
                else{
                    JDialog jDialog = new JDialog(frame, "Alert", true);
                    jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
                    jDialog.add(new JLabel("Can't start with null data."));
                    jDialog.setSize(400, 100);
                    jDialog.setVisible(true);
                }
            }
        });

        panel.add(new JLabel("Provide broker details :"));
        panel.add(broker);
        panel.add(new JLabel("Provide topic to subscribe :"));
        panel.add(topic);
        panel.add(startMessenger);
        frame.setSize(400, 300);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void messenger(final TMessengerGUI instance){
        Runnable r = new Runnable() {
            public void run() {
                final JFrame frame = new JFrame("T Messenger");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                final JPanel gui = new JPanel(new BorderLayout(5, 5));
                gui.setBorder(new TitledBorder("Start Conversation"));

                //JToolBar tb = new JToolBar();
                JPanel plafComponents = new JPanel(
                        new FlowLayout(FlowLayout.LEFT, 3, 3));
                plafComponents.setBorder(new TitledBorder("Configuration"));

                final UIManager.LookAndFeelInfo[] plafInfos =
                        UIManager.getInstalledLookAndFeels();
                String[] plafNames = new String[plafInfos.length];
                for (int ii = 0; ii < plafInfos.length; ii++) {
                    plafNames[ii] = plafInfos[ii].getName();
                }

                final JComboBox plafChooser = new JComboBox(plafNames);
                plafComponents.add(plafChooser);

                final JLabel statusLabel = new JLabel("Status : ");
                statusLabel.setMinimumSize(new Dimension(50, 20));
                plafComponents.add(statusLabel);

                final JTextField username = new JTextField();
                username.setColumns(15);
                username.setText("enter username");
                plafComponents.add(username);

                final JButton userButton = new JButton("Set User");
                plafComponents.add(userButton);

                userButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            MQTTBase send = new MQTTBase();
                            String user = username.getText();
                            client = send.connectClient(user, mqttSettings.getBroker());
                            statusLabel.setText("Status : Connected");
                            username.setEnabled(false);
                            userButton.setEnabled(false);
                            send.subscribeClient(client, mqttSettings.getTopic(), instance);
                            send.senderPublish(client, mqttSettings.getTopic(), "came online", mqttSettings.getQos());

                        } catch (MqttException e1) {
                            JDialog jDialog = new JDialog(frame, "Alert", true);
                            jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
                            jDialog.add(new JLabel(e1.getMessage()));
                            jDialog.setSize(400, 100);
                            jDialog.setVisible(true);
                        }
                    }
                });

                plafChooser.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        int index = plafChooser.getSelectedIndex();
                        try {
                            UIManager.setLookAndFeel(
                                    plafInfos[index].getClassName());
                            SwingUtilities.updateComponentTreeUI(frame);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                gui.add(plafComponents, BorderLayout.NORTH);

                messageDisplayBox.setRows(20);
                messageDisplayBox.setDisabledTextColor(Color.BLUE);
                JScrollPane displayPaneScroll = new JScrollPane(messageDisplayBox);
                Dimension displayPreferred = displayPaneScroll.getPreferredSize();
                displayPaneScroll.setPreferredSize(
                        new Dimension(displayPreferred.width, displayPreferred.height));
                messageDisplayBox.setEditable(false);
                messageDisplayBox.setLineWrap(true);
                messageDisplayBox.setWrapStyleWord(true);


                final JTextArea messageWriteBox = new JTextArea();
                messageWriteBox.setRows(5);
                messageWriteBox.setLineWrap(true);
                messageWriteBox.setWrapStyleWord(true);
                JScrollPane tableScroll = new JScrollPane(messageWriteBox);
                Dimension tablePreferred = tableScroll.getPreferredSize();
                tableScroll.setPreferredSize(
                        new Dimension(tablePreferred.width, tablePreferred.height));

                JButton sendButton = new JButton("send message");
                JButton disconnectButton = new JButton("disconnect");

                JSplitPane splitPane = new JSplitPane(
                        JSplitPane.VERTICAL_SPLIT, displayPaneScroll,
                        tableScroll);

                sendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MQTTBase send = new MQTTBase();
                        String text = messageWriteBox.getText();
                        messageWriteBox.setText("");
                        try {
                            send.senderPublish(client, mqttSettings.getTopic(), text, 2);
                        } catch (MqttException | NullPointerException ex) {
                            JDialog jDialog = new JDialog(frame, "Alert", true);
                            jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
                            jDialog.add(new JLabel("Exception occurred.\n "+ex.getMessage()));
                            jDialog.setSize(400, 100);
                            jDialog.setVisible(true);
                        }
                    }
                });

                disconnectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MQTTBase send = new MQTTBase();
                        try {
                            send.senderPublish(client, mqttSettings.getTopic(), "went offline", mqttSettings.getQos());
                            send.disconnect(client);
                            statusLabel.setText("Status : disconnected");
                            username.setEnabled(true);
                            userButton.setEnabled(true);
                        } catch (MqttException | NullPointerException e1) {
                            JDialog jDialog = new JDialog(frame, "Alert", true);
                            jDialog.setLayout(new FlowLayout(FlowLayout.CENTER));
                            jDialog.add(new JLabel("Exception occurred.\n "+e1.getMessage()));
                            jDialog.setSize(400, 100);
                            jDialog.setVisible(true);
                        }
                    }
                });
                gui.add(splitPane, BorderLayout.CENTER);

                JPanel sendComponents = new JPanel();
                sendComponents.setBorder(new TitledBorder(""));
                sendComponents.add(sendButton);
                sendComponents.add(disconnectButton);
                gui.add(sendComponents, BorderLayout.SOUTH);

                frame.setContentPane(gui);

                frame.pack();

                frame.setLocationRelativeTo(null);
                try {
                    // 1.6+
                    frame.setLocationByPlatform(true);
                    frame.setMinimumSize(frame.getSize());
                } catch (Throwable ignoreAndContinue) {
                }

                frame.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(r);
    }

    public void messageSetter(int msgId, String msg) {
        System.out.println("msg Id : " + msgId);
        String msgStr = "";
        String[] msgList = messageDisplayBox.getText().split("\\r?\\n");
        int displayCount = msgList.length - 10;
        if (displayCount > 0) {
            for (int index = displayCount; index < msgList.length; index++) {
                msgStr = msgStr + msgList[index] + "\n";
            }
            messageDisplayBox.setText(msgStr + msg);
        } else
            messageDisplayBox.setText(messageDisplayBox.getText() + "\n" + msg);
    }
}
