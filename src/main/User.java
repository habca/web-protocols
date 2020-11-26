package main;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import ftp.*;
import imap.*;
import pop3.*;
import smtp.*;
import tftp.*;
import thread.*;

/**
 * Simple textual user interface
 * 
 * @author Harri Linna
 * @version 14.11.2020
 * @version 18.11.2020, merged Client.java
 */
public class User extends AThread {
	
	private BufferedReader reader;
	private IClient client;
	
	public User(InputStream in) {
		this.reader = new BufferedReader(new InputStreamReader(in));
		
		client = placeholder();
		client.help();
		
		setState(onreceive());
		
		new Thread(this).start();
	}
	
	@Override
	public IThread onreceive() {
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
						input.startsWith("ftp") ||
						input.startsWith("tftp")) {
					parseClient(input);
					return;
				}
				
				client.send(input); // send to client
			}
			
		};
	}
	
	private boolean parseClient(String str) throws IOException {
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
			client = new SMTPClient(addr, port);
			return true;
		}
		
		if (protocol.equals(POP3Client.PROTOCOL)) {
			client.close();
			client = new POP3Client(addr, port);
			return true;
		}
		
		if (protocol.equals(IMAPClient.PROTOCOL)) {
			client.close();
			client = new IMAPClient(addr, port);
			return true;
		}
		
		if (protocol.equals(FTPClient.PROTOCOL)) {
			client.close();
			client = new FTPClient(addr, port);
			return true;
		}
		
		if (protocol.equals(TFTPClient.PROTOCOL)) {
			int src_port = 8070; // 8069+1
			client.close();
			client = new TFTPClient(src_port, addr, port);
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
				// TODO: käytä portteja jotka saatu komentoriviltä
				Main.onmessage(
						"The following are the terminal commands:\n" +
						"smtp localhost 8025\n" +
						"pop3 localhost 8110\n" +
						"imap localhost 8143\n" +
						"ftp <host> <port>\n" +
						"tftp localhost 8069\n" +
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

	@Override
	public void onclose() throws IOException {
		Main.onmessage("User input will be received no more");
	}
	
}
