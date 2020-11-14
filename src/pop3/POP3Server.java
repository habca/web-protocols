package pop3;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.Email;
import mail.Inbox;
import main.*;
import thread.*;

/**
 * Simple POP3 Server for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public class POP3Server extends AThread {
	
	private Inbox inbox;
	private Socket socket;
	private int port;
	
	private IPOP3ServerState state;

	public POP3Server(int port, Inbox inbox) {
		this.inbox = inbox;
		this.port = port;
		
		setState(sendCommand(this));
		setState(IPOP3ServerState.stateLogin(this));
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
		// TODO: siirrä socketin avaaminen muualle
		// TODO: siirrä run-metodi AThread-luokkaan
		try (ServerSocket ssocket = new ServerSocket(port)) {
			socket = ssocket.accept();
			while (getContinue()) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public Inbox getInbox() {
		return inbox;
	}

	public void setState(IPOP3ServerState state) {
		this.state = state;
	}
	
	public IThread sendCommand(POP3Server server) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				if (data.startsWith("LIST")) {
					setState(sendList(server));
				}
				
				tcpSend(state.response(data));
			}
			
		};
	}
	
	
	public IThread sendList(POP3Server server) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				
				setState(sendCommand(server));
				
				int counter = 0;
				Iterator<Email> it = server.inbox.iterator();
				while (it.hasNext()) {
					String next = it.next().toString();
					tcpSend(String.format("%d %s", ++counter, next));
					try {
						Thread.sleep((long) 100); // delay (ms)
					} catch (InterruptedException e) {
						Main.onerror(e);
					}
				}
				
				tcpSend("."); // end of transaction
			}
			
		};
	}
}
