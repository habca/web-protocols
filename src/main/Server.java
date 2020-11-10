package main;

import java.io.*;
import java.net.*;

import pop3.*;
import smtp.*;

/**
 * Server provides services
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public class Server {

	private Inbox inbox;
	
	public Server() {
		inbox = new Inbox();
	}
	
	public void serviceSMTP(InetAddress addr, int sport, int size) throws SocketException {
		DatagramSocket ssocket = new DatagramSocket(sport, addr);
		Runnable server = new SMTPServer(ssocket, size, inbox);
		new Thread(server).start();
	}
		
	public void servicePOP3(int tport) throws IOException {
		Runnable server = new POP3Server(tport, inbox);
		new Thread(server).start();
	}
}
