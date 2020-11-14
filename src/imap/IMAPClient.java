package imap;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class IMAPClient extends AThread implements Client.IClient {

	public static String PROTOCOL = "imap";
	
	private Socket socket;
	
	public IMAPClient(Socket socket) {
		this.socket = socket;
		
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

}
