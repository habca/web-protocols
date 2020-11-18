package pop3;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import thread.*;

public class POP3ServerReceiver extends AThreadTCP {

	private Inbox inbox;
	private IPOP3ServerState state;
	
	public POP3ServerReceiver(Socket socket, Inbox inbox) throws IOException {
		super(socket);
		
		this.inbox = inbox;
		setState(IPOP3ServerState.stateLogin(this));
		
		new Thread(this).start();
		
		tcpSend("+OK this is pop3 greeting");
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
	
	public String printLIST() {
		String format = "+OK %d messages (%d bytes)";
		int total = inbox.size();
		int bytes = inbox.getBytes();
		return String.format(format, total, bytes);
	}
	
}
