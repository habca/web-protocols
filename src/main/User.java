package main;

import java.io.*;

import thread.*;

/**
 * Simple textual user interface
 * 
 * @author Harri Linna
 * @version 14.11.2020
 */
public class User extends AThread {
	
	private BufferedReader reader;
	private Client client;
	
	public User(InputStream input, Client client) {
		
		
		this.reader = new BufferedReader(new InputStreamReader(input));
		this.client = client;
		
		setState(onreceive());
		
		// TODO: tulosta käyttöohje
		Main.onmessage("close with 'exit' or 'quit'");
		Main.onmessage("select 'smtp' or 'pop3' or 'imap'");
	}
	
	private IThread onreceive() {
		return new IThread() {
			
			@Override
			public void run() throws IOException {
				String input = reader.readLine();
				
				if (input.matches("exit|quit")) {
					System.exit(0);
				}
				
				client.send(input);
			}
			
		};
	}
	
}
