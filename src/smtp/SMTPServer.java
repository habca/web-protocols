package smtp;

import java.io.*;
import java.net.*;
import java.util.*;

import mail.Inbox;
import thread.*;

/**
 * Simple SMTP Server for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, uses IThread
 * @version 9.11.2020, uses AThread
 */
public class SMTPServer extends AThread {
	
	private Inbox inbox;
	private DatagramSocket socket;
	private int size;
	
	private HashMap<String, String> map;
	private ISMTPServerState state;
	
	public SMTPServer(DatagramSocket socket, int size, Inbox inbox) {
		this.inbox = inbox;
		this.socket = socket;
		this.size = size;
		
		initHashMap();
		setState(receiveCommand(this));
		setState(ISMTPServerState.stateInitial(this));
	}
	
	public DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	
	public void udpSend(String str, InetAddress addr, int port) throws IOException {
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		socket.send(packet);
	}
	
	public final void setState(ISMTPServerState state) {
		this.state = state;
	}
	
	public final void initHashMap() {
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
	
	public final String getStatus(String key) {
		return map.getOrDefault(key, "554 Transaction failed");
	}
		
	public IThread receiveCommand(SMTPServer server) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				DatagramPacket packet = udpReceive();
				
				String data = new String(packet.getData(), 0, packet.getLength());
				
				if (data.startsWith("DATA")) {
					setState(receiveData(server));
				}
				
				String resp = state.response(data);
				
				udpSend(resp, packet.getAddress(), packet.getPort());
			}
			
		};
	}
	
	public IThread receiveData(SMTPServer server) {
		return new IThread() {

			@Override
			public void run() throws IOException {
				DatagramPacket packet = udpReceive();
				
				String data = new String(packet.getData(), 0, packet.getLength());
				
				if (data.equals(".")) {
					setState(receiveCommand(server));
					String resp = state.response(data);
					udpSend(resp, packet.getAddress(), packet.getPort());
					inbox.send();
					return; // do not process
				}
				
				processData(data);
			}
			
		};
		
	}
	
	public void processData(String data) {
		inbox.setField("DATA", data);
	}
	
	public void processCommand(String command, String key) {
		String cmd = splitCommand(command, key);
		inbox.setField(key, cmd);
	}
	
	public static String splitCommand(String command, String key) {
		return command.split(key)[1].trim(); // TODO: indeksivirhe
	}
	
}
