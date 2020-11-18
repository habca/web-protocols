package pop3;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import main.*;
import thread.*;

public class POP3ServerReceiver extends AThreadTCP {

	private Inbox inbox;
	private IPOP3ServerState state;
	
	public static POP3ServerReceiver create(Socket socket, Inbox inbox) {
		try {
			POP3ServerReceiver server = new POP3ServerReceiver(socket, inbox);
			new Thread(server).start();
			return server;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private POP3ServerReceiver(Socket socket, Inbox inbox) throws IOException {
		super(socket);
		
		this.inbox = inbox;
		setState(IPOP3ServerState.stateLogin(this));
	}
	
	public void setState(IPOP3ServerState state) {
		this.state = state;
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				tcpSend(state.response(data));
				
				if (data.startsWith("LIST")) {
					int counter = 0;
					Iterator<Email> it = inbox.iterator();
					while (it.hasNext()) {
						String next = it.next().toString();
						tcpSend(String.format("%d %s", ++counter, next));
					}
					tcpSend(".");
				}
			}
			
		};
	}
	
	// TODO: private
	public String printLIST() {
		String format = "+OK %d messages (%d bytes)";
		int total = inbox.size();
		int bytes = inbox.getBytes();
		return String.format(format, total, bytes);
	}
	
}
