package imap;

import java.io.*;
import java.net.*;

import mail.*;
import main.*;
import thread.*;

public class IMAPServer extends AThread {
	
	private Inbox inbox;
	private ServerSocket socket;

	public IMAPServer(int port, Inbox inbox) throws IOException {
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
				IMAPServerReceiver.create(client, inbox);
			}
			
		};
	}
	
}
