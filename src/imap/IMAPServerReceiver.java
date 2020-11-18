package imap;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import main.*;
import thread.*;

public class IMAPServerReceiver extends AThread {

	private Inbox inbox;
	private Socket socket;
	private IIMAPServerState state;
	
	public static IMAPServerReceiver create(Socket socket, Inbox inbox) {
		IMAPServerReceiver server = new IMAPServerReceiver(socket, inbox);
		new Thread(server).start();
		return server;
	}
	
	private IMAPServerReceiver(Socket socket, Inbox inbox) {
		this.socket = socket;
		this.inbox = inbox;
		
		setState(sendCommand(this));
		setState(IIMAPServerState.stateLogin(this));
	}
	
	@Override
	public void run() {
		try {
			while (!isClosed()) {
				getState().run();
			}
			socket.close();
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public void setState(IIMAPServerState state) {
		this.state = state;
	}

	public final String tcpReceive() throws IOException {
		InputStream in = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		return reader.readLine();
	}
	
	public final void tcpSend(String str) throws IOException {
		try {
			Thread.sleep((long) 100); // delay ms
			OutputStream out = socket.getOutputStream();
			PrintWriter printer = new PrintWriter(out, true);
			printer.println(str);
		} catch (InterruptedException e) {
			Main.onerror(e);
		}
	}
	
	public IThread sendCommand(IMAPServerReceiver server) {
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
