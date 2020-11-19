package smtp;

import java.io.*;
import java.net.*;

import mail.*;
import thread.*;

/**
 * Simple SMTP Server for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, uses IThread
 * @version 9.11.2020, uses AThread
 */
public class SMTPServer extends AThreadServerSocket {
	
	private Inbox inbox;
	
	public SMTPServer(int port, Inbox inbox) throws IOException {
		super(port);
		this.inbox = inbox;
		new Thread(this).start();
	}
	
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				Socket client = accept();
				new SMTPServerReceiver(client, inbox);
			}
			
		};
	}
	
}
