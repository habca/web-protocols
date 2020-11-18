package main;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import ftp.*;
import imap.*;
import pop3.*;
import smtp.*;
import thread.*;

/**
 * Simple textual user interface
 * 
 * @author Harri Linna
 * @version 14.11.2020
 */
public class User extends AThread {
	
	private BufferedReader reader;
	private IClient client;
	
	public static User create(InputStream in) {
		User user = new User(in);
		new Thread(user).start();
		return user;
	}
	
	private User(InputStream in) {
		this.reader = new BufferedReader(new InputStreamReader(in));
		
		client = placeholder();
		client.help();
		
		setState(onreceive());
	}
	
	private IThread onreceive() {
		return new IThread() {
			
			@Override
			public void run() throws IOException {
				String input = reader.readLine();
				
				if (input.equals("quit")) {
					Main.onquit();
					return;
				}
				if (input.equals("help")) {
					client.help();
					return;
				}
				if (input.startsWith("smtp") ||
						input.startsWith("pop3") ||
						input.startsWith("imap") ||
						input.startsWith("ftp")) {
					parseClient(input);
					return;
				}
				
				client.send(input); // send to client
			}
			
		};
	}
	
	private boolean parseClient(String str) {
		String protocol;
		InetAddress addr;
		int port;
		
		try {
			StringBuilder sb = new StringBuilder(str);
			protocol = Mjonot.erota(sb);
			addr = InetAddress.getByName(Mjonot.erota(sb));
			port = Integer.parseInt(Mjonot.erota(sb));
		} catch (Exception e) {
			return false;
		}
		
		if (protocol.equals(SMTPClient.PROTOCOL)) {
			client.close();
			client = SMTPClient.create(addr, port);
			return true;
		}
		
		if (protocol.equals(POP3Client.PROTOCOL)) {
			client.close();
			client = POP3Client.create(addr, port);
			return true;
		}
		
		if (protocol.equals(IMAPClient.PROTOCOL)) {
			client.close();
			client = IMAPClient.create(addr, port);
			return true;
		}
		
		if (protocol.equals(FTPClient.PROTOCOL)) {
			client.close();
			client = FTPClient.create(addr, port);
			return true;
		}
		
		return false;
	}
	
	private static IClient placeholder() {
		return new IClient() { // placeholder

			@Override
			public void send(String input) {
				Main.onmessage("client was not found");
			}

			@Override
			public void help() {
				Main.onmessage(
						"The following are the terminal commands:\n" +
						"smtp <host> <port>\n" +
						"pop3 <host> <port>\n" +
						"imap <host> <port>\n" +
						"ftp <host> <port>\n" +
						"help\n" +
						"quit"
				);
			}

			@Override
			public void close() {
				
			}

			@Override
			public boolean isClosed() {
				return true;
			}
			
		};
	}
	
}
