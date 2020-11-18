package imap;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import main.*;
import thread.*;

public class IMAPServer extends AThread {
	
	private Inbox inbox;
	private Socket socket;
	private int port;
	
	private IIMAPServerState state;

	public IMAPServer(int port, Inbox inbox) {
		this.inbox = inbox;
		this.port = port;
		
		setState(sendCommand(this));
		setState(IIMAPServerState.stateLogin(this));
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
	
	@Override
	public void run() {
		try (ServerSocket ssocket = new ServerSocket(port)) {
			socket = ssocket.accept();
			while (!isClosed()) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}

	public void setState(IIMAPServerState state) {
		this.state = state;
	}
	
	public IThread sendCommand(IMAPServer server) {
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
						tcpSend(String.format("%d %s", ++counter, next)); // maybe dropped
					}
					tcpSend("."); // maybe dropped
				}
			}
			
		};
	}
	
	/*
	public IThread sendCommand(IMAPServer server) {
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
	
	public IThread sendList(IMAPServer server) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				
				setState(sendCommand(server));
				
				int counter = 0;
				Iterator<Email> it = inbox.iterator();
				while (it.hasNext()) {
					String next = it.next().toString();
					tcpSend(String.format("%d %s", ++counter, next));
				}
				
				tcpSend("."); // end of transaction
			}
			
		};
	}
	*/
	
}
