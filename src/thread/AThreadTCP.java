package thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

import main.Main;

/**
 * @author Harri Linna
 * @author Ville Paju
 * @version 17.1.2020
 */
public abstract class AThreadTCP extends AThread {

	private Socket socket;
	
	public AThreadTCP(Socket socket) {
		super();
		
		this.socket = socket;
	}

	public final void tcpSend(String str) throws IOException {
		OutputStream out = socket.getOutputStream();
		PrintWriter printer = new PrintWriter(out, true);
		printer.println(str);
	}
	
	public final String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
	}
	
	public void closeSocket() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
}
