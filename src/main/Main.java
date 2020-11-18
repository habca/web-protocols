package main;

import java.io.*;

import imap.IMAPServer;
import mail.*;
import pop3.POP3Server;
import smtp.SMTPServer;

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
 * @version 18.11.2020, Server.java and Client.java removed
 */
public class Main {
	
	public static void main(String[] args) {
		
		Inbox inbox = new Inbox();
		SMTPServer.create(8025, inbox);
		POP3Server.create(8110, inbox);
		IMAPServer.create(8143, inbox);
		
		//Client client = new Client();
		//User.create(System.in, client);
		User.create(System.in);
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
	
	public static void onquit() {
		System.exit(0);
	}
	
}
