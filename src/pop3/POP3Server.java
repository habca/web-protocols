package pop3;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class POP3Server extends AThread {
	
	private Socket socket;
	private int port;
	
	private IPOP3State state;

	public POP3Server(int port) {
		this.port = port;
		
		setState(receiveCommand());
		setState(IPOP3State.stateLogin(this));
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
	
	@Override
	public void run() {
		try (ServerSocket ssocket = new ServerSocket(port)) {
			socket = ssocket.accept();
			while (true) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}

	public void setState(IPOP3State state) {
		this.state = state;
	}
	
	public IThread receiveCommand() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				tcpSend(state.response(data));
			}
			
		};
	}
}
