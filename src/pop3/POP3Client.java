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
 */
public class POP3Client extends AThread {

	private Socket socket;
	
	private BufferedReader reader;
	
	public POP3Client(Socket socket) {
		this.socket = socket;
		
		setState(sendCommand(this));
	}
	
	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in)) ) {
			reader = in;
			while (true) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public final String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
	}
	
	public final void tcpSend(String str) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter printer = new PrintWriter(out, true);
		printer.println(str);
	}

	public IThread sendCommand(POP3Client client) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				tcpSend(resp);
				
				if (resp.startsWith("LIST")) {
					setState(receiveList(client));
					return; // change state now
				}
				
				String data = tcpReceive();
				
				Main.onmessage(data);
			}
			
		};
		
	}
	
	public IThread receiveList(POP3Client client) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				
				String data = tcpReceive();
				
				if (data.equals(".")) {
					setState(sendCommand(client));
					return; // do not print (.)
				}
				
				Main.onmessage(data);
			}
			
		};
	}
}
