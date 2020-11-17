package imap;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class IMAPClient extends AThreadTCP implements IClient {

	public IMAPClient(Socket socket) {
		super(socket);
		
		setState(onreceive());
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
		try {
			tcpSend(str);
		} catch (IOException e) {
			Main.onerror(e);
		}
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the IMAP commands:\n\n" +
				"LOGIN <SP> <username> <SP> <password> <CRLF>\n" +
				"LIST [<SP> <pathname>] <CRLF>\n" +
				"LOGOUT <CRLF>"
		);
	}
	
	@Override
	public String protocol() {
		return "imap";
	}

}
