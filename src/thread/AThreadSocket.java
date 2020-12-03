package thread;

import java.io.*;
import java.net.*;

import main.*;

/**
 * Thread for receiving TCP-packets
 * 
 * Huom! Konstruktorin viimeinen rivi:
 *       new Thread(this).start();
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 18.11.2020
 */
public abstract class AThreadSocket extends AThread {

	private Socket socket;
	
	public AThreadSocket(InetAddress addr, int port) throws IOException {
		this.socket = new Socket(addr, port);
	}
	
	public AThreadSocket(Socket socket) throws IOException {
		this.socket = socket;
	}
	
	@Override
	public final void onclose() throws IOException {
		socket.close();
		Main.onmessage("Socket was closed");
	}
	
	public final void tcpSend(String str) {
		try {
			Thread.sleep((long) 100); // sync delay
			OutputStream out = socket.getOutputStream();
			PrintWriter printer = new PrintWriter(out, true);
			printer.println(str);
		} catch (IOException | InterruptedException e) {
			Main.onerror(e);
		}
	}
	
	public final String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
	}
	
	public final InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}
	
}
