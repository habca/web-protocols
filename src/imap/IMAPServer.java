package imap;

import java.io.*;
import java.net.*;

import mail.*;
import thread.*;

public class IMAPServer extends AThreadServerSocket {
	
	private Inbox inbox;
	
	public IMAPServer(int port, Inbox inbox) throws IOException {
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
				new IMAPServerReceiver(client, inbox);
			}
			
		};
	}
	
}
