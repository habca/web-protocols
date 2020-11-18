package main;

import java.io.*;

// TODO: http://commons.apache.org/proper/commons-cli/
// TODO: parsi porttinumerot komentoriviltä

/**
 * Simple Mail Transfer Protocol
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020
 * @version 10.11.2020, Server.java provides services
 * @version 13.11.2020, Client.java provides services
 * @version 14.11.2020, User.java reads user input
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		int smtp_port = 8025, pop3_port = 8110, imap_port = 8143;
		
		/*
		DatagramSocket ssocket = new DatagramSocket(sport, addr);
		Runnable server = new SMTPServer(ssocket, size);
		new Thread(server).start();
		
		DatagramSocket csocket = new DatagramSocket(cport, addr);
		Runnable client = new SMTPClient(csocket, size, sport, addr);
		new Thread(client).start();
		
		Socket csocket = new Socket(addr, tport);
		Runnable client = new POP3Client(csocket);
		new Thread(client).start();
		*/
		
		Server server = new Server();
		//server.serviceSMTP(addr, sport, size);
		server.serviceSMTP(smtp_port);
		server.servicePOP3(pop3_port);
		server.serviceIMAP(imap_port);
		
		Client client = new Client();
		User user = new User(System.in, client);
		new Thread(user).start();
	}

	public static void onmessage(String str) {
		System.out.println(str);
	}
	
	public static void onmessage(byte[] buffer, int start, int count) {
		System.out.write(buffer, start, count);
	}
	
	public static void onerror(Exception e) {
		System.err.println(e.getMessage());
	}
	
}
