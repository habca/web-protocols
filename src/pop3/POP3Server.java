package pop3;

import java.io.IOException;
import java.net.*;

import main.Main;

public class POP3Server implements Runnable {
	
	private Socket socket;
	private int port;

	public POP3Server(int port) {
		this.port = port;
	}
	
	@Override
	public void run() {
		try (ServerSocket ssocket = new ServerSocket(port)) {
			socket = ssocket.accept();
			while (true) {
				// TODO: do something
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}

}
