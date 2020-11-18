package pop3;

import java.io.*;
import java.net.*;

import mail.*;
import main.*;
import thread.*;

/**
 * Simple POP3 Server for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public class POP3Server extends AThread {
	
	private Inbox inbox;
	private ServerSocket socket;
	
	public POP3Server(int port, Inbox inbox) throws IOException {
		this.inbox = inbox;
		
		socket = new ServerSocket(port);
		setState(onreceive());
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			while (!isClosed()) {
				getState().run();
			}
			socket.close();
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				Socket client = socket.accept();
				new POP3ServerReceiver(client, inbox);
			}
			
		};
	}
	
}
