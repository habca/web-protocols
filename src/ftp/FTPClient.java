package ftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class FTPClient extends AThreadTCP implements IClient {
	
	public static final String PROTOCOL = "ftp";
	
	private InetAddress addr;
	private int port;
	
	public static FTPClient create(InetAddress addr, int port) {
		try {
			FTPClient client = new FTPClient(addr, port);
			new Thread(client).start();
			return client;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private FTPClient(InetAddress addr, int port) throws IOException {
		super(addr, port);
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				
				Main.onmessage(str);
				
				if (str.startsWith("221")) { // QUIT
					close();
				}
				if (str.startsWith("227")) { // PASV
					String str_addr = Static.extractAddress(str);
					addr = InetAddress.getByName(str_addr);
					port = Static.extractPort(str);
				}
			}
		
		};
	}
	
	@Override
	public void send(String str) {
		if (str.matches("LIST|RETR")) {
			FTPDataReceiver.create(addr, port);
		}
		tcpSend(str);
	}
	
	@Override
	public void help() {
		Main.onmessage(
				"The following are the FTP commands:\n\n" +
				"USER <SP> <username> <CRLF>\n" +
				"PASS <SP> <password> <CRLF>\n" +
				"PASV <CRLF>\n" +
				"LIST [<SP> <pathname>] <CRLF>\n" +
				"RETR <SP> <pathname> <CRLF>\n" +
				"QUIT <CRLF>"
		);
	}
	    
}
