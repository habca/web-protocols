package imap;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class IMAPClient extends AThreadTCP implements Client.IClient {

	public static String PROTOCOL = "imap";
	
	public IMAPClient(Socket socket) {
		super(socket);
		
		setState(onreceive());
	}
	
	@Override
	public void send(String str) {
		try {
			tcpSend(str);
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	private IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				Main.onmessage(str);
			}
		
		};
	}

	@Override
	public void help() {
		// TODO Auto-generated method stub
	}

}
