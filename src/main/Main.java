package main;

import java.io.*;

import http2.*;
import imap.*;
import mail.*;
import pop3.*;
import smtp.*;
import tftp.*;

// TODO: http://commons.apache.org/proper/commons-cli/
// TODO: parsi portit komentoriviltä

/**
 * Simple Mail Transfer Protocol
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
		try {
			Inbox inbox = new Inbox();
			new SMTPServer(8025, inbox);
			new POP3Server(8110, inbox);
			new IMAPServer(8143, inbox);
			new TFTPServer(8069);
			new HTTP2Server(8080);
			new User(System.in);
		} catch (IOException e) {
			Main.onerror(e);
		}
	}

	public static void onmessage(String str) {
		System.out.println(str);
	}
	
	public static void onmessage(byte[] buffer, int start, int count) {
		System.out.write(buffer, start, count);
	}
	
	public static void onerror(Exception e) {
		System.err.println(e.getMessage());
		e.printStackTrace();
	}
	
	public static void onquit() {
		System.exit(0);
	}
	
}
