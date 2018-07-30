package com.svatah;

import javax.swing.*;  
import java.awt.*;  
import java.awt.event.*;  

/**************************************
 * Swing based MQTT client Emulator
 * @author atulsharma
 * ***********************************/
public class StartApp {  
	private static JDialog jDialog;
	private JTextField broker;
	private JTextField topic;

	private StartApp() {
		JFrame jFrame= new JFrame("T Messenger");
		jDialog = new JDialog(jFrame , "Alert", true);
		jDialog.setLayout( new FlowLayout(FlowLayout.CENTER) );
		
		broker=new JTextField();
		broker.setBounds(45, 24, 147, 40);
		broker.setText("set broker");
		
		topic=new JTextField();
		topic.setBounds(90, 48, 147, 40);
		topic.setText("set topic");
		
		JButton startMessenger = new JButton ("OK");
		startMessenger.addActionListener ( new ActionListener()
		{  
			public void actionPerformed( ActionEvent e )  
			{
				MQTTBase mqtt = new MQTTBase();
				String brokerId = broker.getText();
				mqtt.setBroker(brokerId);
				String topicId = topic.getText();
				mqtt.setTopic(topicId);
				StartApp.jDialog.setVisible(false);
				new JavaClient();
			}  
		});  
		jDialog.add( new JLabel ("Welcome to MQTT client. Please connect to broker."));
		jDialog.add(broker);
		jDialog.add(topic);
		jDialog.add(startMessenger);
		jDialog.setSize(400,100);
		jDialog.setVisible(true);
	}

	public static void main(String args[])  
	{  
		new StartApp();
	}

}  