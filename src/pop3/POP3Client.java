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
public class POP3Client extends AThread implements Client.IClient {

	public static String PROTOCOL = "pop3";
	
	private Socket socket;
	
	public POP3Client(Socket socket) {
		this.socket = socket;
		
		//setState(sendCommand(this));
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
	
	private void tcpSend(String str) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter printer = new PrintWriter(out, true);
		printer.println(str);
	}
	
	private String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
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
