package thread;

import java.io.*;
import java.net.*;

import main.*;

/**
 * @author Harri Linna
 * @author Ville Paju
 * @version 17.1.2020
 */
public abstract class AThreadTCP extends AThread {

	private Socket socket;
	
	public AThreadTCP(InetAddress addr, int port) throws IOException {
		this.socket = new Socket(addr, port);
	}

	@Override
	public void run() {
		setState(onreceive());
		try {
			while (!isClosed()) {
				getState().run();
			}
			socket.close();
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public abstract IThread onreceive();
	
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
	
}
