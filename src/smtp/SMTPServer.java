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
	
	public static SMTPServer create(int port, Inbox inbox) {
		try {
			SMTPServer server = new SMTPServer(port, inbox);
			new Thread(server).start();
			return server;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private SMTPServer(int port, Inbox inbox) throws IOException {
		this.inbox = inbox;
		
		socket = new ServerSocket(port);
		setState(onreceive());
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
				SMTPServerReceiver.create(client, inbox);
			}
			
		};
	}
	
}
