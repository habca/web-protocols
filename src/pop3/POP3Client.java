package pop3;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple POP3 Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 * @version 14.11.2020, removed extra states
 */
public class POP3Client extends AThreadTCP implements IClient {
	
	public static final String PROTOCOL = "pop3";
	
	public static POP3Client create(InetAddress addr, int port) {
		try {
			POP3Client client = new POP3Client(addr, port);
			new Thread(client).start();
			return client;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private POP3Client(InetAddress addr, int port) throws IOException {
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
				"The following are the POP3 commands:\n\n" +
				"USER <SP> <username> <CRLF>\n" +
				"PASS <SP> <password> <CRLF>\n" +
				"LIST [<SP> <pathname>] <CRLF>\n" +
				"QUIT <CRLF>"
		);
	}

	/*
	public IThread sendCommand(POP3Client client) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				
				if (Objects.nonNull(resp)) {
					
					tcpSend(resp);
					
					if (resp.startsWith("LIST")) {
						setState(receiveList(client));
						return; // change state now
					}
					
					String data = tcpReceive();
					
					Main.onmessage(data);
				}
			}
			
		};
		
	}
	
	public IThread receiveList(POP3Client client) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				
				String data = tcpReceive();
				
				if (Objects.nonNull(data)) {
				
					if (data.equals(".")) {
						setState(sendCommand(client));
						return; // do not print (.)
					}
					
					Main.onmessage(data);
				}
			}
			
		};
	}
	*/
	
}
