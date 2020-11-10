package main;

import java.io.*;
import java.net.*;

import pop3.*;
import smtp.*;

/**
 * Simple Mail Transfer Protocol
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020
 * @version 10.11.2020, server provides services
 */
public class Main {

	public static void main(String[] args) throws IOException {
		InetAddress addr = InetAddress.getByName("localhost");
		int sport = 8080, cport = 8081, tport = 8082, size = 256;
		
		/*
		DatagramSocket ssocket = new DatagramSocket(sport, addr);
		Runnable server = new SMTPServer(ssocket, size);
		new Thread(server).start();
		*/
		
		Server server = new Server();
		server.serviceSMTP(addr, sport, size);
		server.servicePOP3(tport);
		
		/*
		DatagramSocket csocket = new DatagramSocket(cport, addr);
		Runnable client = new SMTPClient(csocket, size, sport, addr);
		new Thread(client).start();
		*/
		
		Socket csocket = new Socket(addr, tport);
		Runnable client = new POP3Client(csocket);
		new Thread(client).start();
	}

	public static void onmessage(String str) {
		System.out.println(str);
	}
	
	public static void onerror(Exception e) {
		System.err.println(e.getMessage());
		System.exit(1);
	}
}
