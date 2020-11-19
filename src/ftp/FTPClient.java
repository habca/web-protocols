package ftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class FTPClient extends AThreadSocket implements IClient {
	
	public static final String PROTOCOL = "ftp";
	
	private InetAddress addr;
	private int port;
	
	public FTPClient(InetAddress addr, int port) throws IOException {
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
	public void send(String str) throws IOException {
		if (str.startsWith("LIST") || 
				str.startsWith("RETR")) {
			new FTPClientReceiver(addr, port);
		}
		tcpSend(str);
	}
	
	@Override
	public void help() {
		Main.onmessage(
				"The following are the FTP commands:\n" +
				"USER <username>\n" +
				"PASS <password>\n" +
				"PASV\n" +
				"LIST [<pathname>]\n" +
				"RETR <pathname> \n" +
				"QUIT"
		);
	}
	    
}
