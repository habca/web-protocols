package pop3;

import java.io.*;
import java.net.*;

import mail.*;
import thread.*;

/**
 * Simple POP3 Server for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public class POP3Server extends AThreadServerSocket {
	
	private Inbox inbox;
	
	public POP3Server(int port, Inbox inbox) throws IOException {
		super(port);
		this.inbox = inbox;
		new Thread(this).start();
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				Socket client = accept();
				new POP3ServerReceiver(client, inbox);
			}
			
		};
	}
	
}
