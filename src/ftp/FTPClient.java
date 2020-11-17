package ftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class FTPClient extends AThreadTCP implements IClient {
	
	private InetAddress data_host;
	private int data_port;
	
	public FTPClient(Socket socket) {
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
				
				if (str.startsWith("221")) { // QUIT
					closeSocket(); // close tcp
					setClose(); // stop thread
				}
				if (str.startsWith("227")) { // PASV
					String str_addr = Static.extractAddress(str);
					data_host = InetAddress.getByName(str_addr);
					data_port = Static.extractPort(str);
				}
			}
		
		};
	}
	
	@Override
	public void send(String str) {
		try {
			if (str.matches("LIST|RETR")) {
				Runnable thread = new FTPDataReceiver(data_host, data_port);
				new Thread(thread).start();
			}
			tcpSend(str);
		} catch (IOException e) {
			Main.onerror(e);
		}
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
	
	@Override
	public String protocol() {
		return "ftp";
	}
	    
}
