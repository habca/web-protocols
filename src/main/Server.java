package main;

import java.io.*;
import java.net.*;

import pop3.*;
import smtp.*;

public class Server {

	//private Inbox inbox;
	
	public Server() {
		super(); // default constructor
	}
	
	public void serviceSMTP(InetAddress addr, int sport, int size) throws SocketException {
		DatagramSocket ssocket = new DatagramSocket(sport, addr);
		Runnable server = new SMTPServer(ssocket, size);
		new Thread(server).start();
		
		//server.setInbox(inbox);
	}
		
	public void servicePOP3(int tport) throws IOException {
		Runnable server = new POP3Server(tport);
		new Thread(server).start();
		
		//server.setInbox(inbox);
	}
	
}
