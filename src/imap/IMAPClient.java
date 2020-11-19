package imap;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class IMAPClient extends AThreadSocket implements IClient {

	public static final String PROTOCOL = "imap";
	
	public IMAPClient(InetAddress addr, int port) throws IOException {
		super(addr, port);
		new Thread(this).start();
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
