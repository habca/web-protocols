package imap;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class IMAPClient extends AThreadTCP implements IClient {

	public static final String PROTOCOL = "imap";
	
	public static IMAPClient create(InetAddress addr, int port) {
		try {
			IMAPClient client = new IMAPClient(addr, port);
			new Thread(client).start();
			return client;
		} catch (Exception e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private IMAPClient(InetAddress addr, int port) throws IOException {
		super(addr, port);
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				Main.onmessage(str);
			}
		
		};
	}
	
	@Override
	public void send(String str) {
		tcpSend(str);
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the IMAP commands:\n" +
				"LOGIN <username> <password>\n" +
				"LIST [<pathname>]\n" +
				"LOGOUT"
		);
	}

}
