package imap;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import thread.*;

public class IMAPServerReceiver extends AThreadSocket {

	private Inbox inbox;
	private IIMAPServerState state;
	
	public IMAPServerReceiver(Socket socket, Inbox inbox) throws IOException {
		super(socket);
		this.inbox = inbox;
		setState(IIMAPServerState.stateLogin(this));
		new Thread(this).start();
		
		String format = "OK imap ready for requests from %s";
		tcpSend(String.format(format, socket.getLocalSocketAddress()));
	}
	
	public void setState(IIMAPServerState state) {
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
	
}
