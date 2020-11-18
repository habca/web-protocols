package smtp;

// TODO: korjaa palvelimen vastaukset

import java.io.*;
import java.net.*;
import java.util.*;

import mail.*;
import thread.*;

public class SMTPServerReceiver extends AThreadTCP {

	private Inbox inbox;
	private ISMTPServerState state;
	private HashMap<String, String> map;
	
	public SMTPServerReceiver(Socket socket, Inbox inbox) throws IOException {
		super(socket);
		
		this.inbox = inbox;
		setState(ISMTPServerState.stateInitial(this));
		initHashMap();
		
		new Thread(this).start();
		
		String format = "220 %s Service ready";
		tcpSend(String.format(format, socket.getLocalSocketAddress()));
	}

	public final void setState(ISMTPServerState state) {
		this.state = state;
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				if (data.startsWith("DATA")) {
					setState(onreceiveData());
				}
				
				String resp = state.response(data);
				tcpSend(resp);
			}
			
		};
	}
	
	public IThread onreceiveData() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String data = tcpReceive();
				
				if (data.equals(".")) {
					setState(onreceive());
					String resp = state.response(data);
					
					tcpSend(resp);
					inbox.send();
					return; // do not process
				}
				
				processData(data);
			}
			
		};
	}
	
	public void initHashMap() {
		map = new HashMap<String, String>();
		
		map.put("211", "211 System status, or system help reply");
        map.put("214", "214 Help message");
        map.put("220", "220 <domain> Service ready");
        map.put("221", "221 <domain> Service closing transmission channel");
        map.put("250", "250 Requested mail action okay, completed");
        map.put("251", "251 User not local; will forward to <forward-path>");
        map.put("354", "354 Start mail input; end with <CRLF>.<CRLF>");
        map.put("421", "421 <domain> Service not available,\n" + 
        		"closing transmission channel");
        map.put("450", "450 Requested mail action not taken: mailbox unavailable");
        map.put("451", "451 Requested action aborted: local error in processing");
        map.put("452", "452 Requested action not taken: insufficient system storage");
        map.put("500", "500 Syntax error, command unrecognized");
        map.put("501", "501 Syntax error in parameters or arguments");
        map.put("502", "502 Command not implemented");
        map.put("503", "503 Bad sequence of commands");
        map.put("504", "504 Command parameter not implemented");
        map.put("550", "550 Requested action not taken: mailbox unavailable");
        map.put("551", "551 User not local; please try <forward-path>");
        map.put("552", "552 Requested mail action aborted: exceeded storage allocation");
        map.put("553", "553 Requested action not taken: mailbox name not allowed");
        map.put("554", "554 Transaction failed");
	}
	
	public String getStatus(String key) {
		return map.getOrDefault(key, "554 Transaction failed");
	}
	
	public void processData(String data) {
		inbox.setField("DATA", data);
	}
	
	public void processCommand(String command, String key) {
		String cmd = splitCommand(command, key);
		inbox.setField(key, cmd);
	}
	
	public static String splitCommand(String command, String key) {
		return command.split(key)[1].trim(); // TODO: indexoutofbounds
	}
	
}
