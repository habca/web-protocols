package smtp;

import java.io.*;
import java.net.*;

import mail.*;
import main.*;
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
public class SMTPServer extends AThread {
	
	private Inbox inbox;
	private ServerSocket socket;
	
	public SMTPServer(int port, Inbox inbox) throws IOException {
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
				new SMTPServerReceiver(client, inbox);
			}
			
		};
	}
	
}
