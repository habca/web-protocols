package smtp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple SMTP Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, interface for runnable threads
 * @version 9.11.2020, uses abstract class
 * @version 14.11.2020, removed extra states
 */
public class SMTPClient extends AThreadTCP implements IClient {

	public static final String PROTOCOL = "smtp";
	
	//private DatagramSocket socket;
	
	//private int size;
	//private int port;
	//private InetAddress addr;
	
	/*
	public SMTPClient(DatagramSocket socket, int size, int port, InetAddress addr) {
		this.socket = socket;
		
		this.size = size;
		this.port = port;
		this.addr = addr;
		
		//setState(sendCommand(this));
		setState(onreceive());
	}
	*/
	
	public static SMTPClient create(InetAddress addr, int port) {
		try {
			SMTPClient client = new SMTPClient(addr, port);
			new Thread(client).start();
			return client;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
	}
	
	private SMTPClient(InetAddress addr, int port) throws IOException {
		super(addr, port);
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				//String str = udpReceive();
				String str = tcpReceive();
				Main.onmessage(str);
			}
			
		};
	}
	
	/*
	private void udpSend(String str, InetAddress addr, int port) throws IOException {
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		socket.send(packet);
	}
	
	private String udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return new String(packet.getData(), 0, packet.getLength());
	}
	
	public DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	*/
	
	@Override
	public void send(String str) {
		//udpSend(str, addr, port);
		tcpSend(str);
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the SMTP commands:\n\n" +
				"HELO <SP> <domain> <CRLF>\n" +
	            "MAIL <SP> FROM:<reverse-path> <CRLF>\n" +
	            "RCPT <SP> TO:<forward-path> <CRLF>\n" +
	            "DATA <CRLF>\n" +
	            "QUIT <CRLF>"
		);
	}
	
}
