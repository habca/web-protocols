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
public class SMTPClient extends AThread implements Client.IClient {
	
	public static String PROTOCOL = "smtp";
	
	private DatagramSocket socket;
	
	private int size;
	private int port;
	private InetAddress addr;
	
	public SMTPClient(DatagramSocket socket, int size, int port, InetAddress addr) {
		this.socket = socket;
		
		this.size = size;
		this.port = port;
		this.addr = addr;
		
		//setState(sendCommand(this));
		setState(onreceive());
	}
	
	@Override
	public void run() {
		try {
			while (getContinue()) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	@Override
	public void send(String str) {
		try {
			udpSend(str, addr, port);
		} catch (IOException e) {
			Main.onerror(e);
		}	
	}
	
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
	
	/*
	public DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	*/
	
	private IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = udpReceive();
				Main.onmessage(str);
			}
			
		};
		
	}
	
	/*
	public IThread sendCommand(SMTPClient client) {
		return new IThread() {
				
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				
				if (Objects.nonNull(resp)) {
					udpSend(resp, addr, port);
					
					DatagramPacket packet = udpReceive();
					String data = new String(packet.getData(), 0, packet.getLength());
					
					if (data.startsWith("354")) {
						setState(sendData(client));
					}
					
					Main.onmessage(data);
				}
			}
		};
	}
	
	public IThread sendData(SMTPClient client) {
		return new IThread() {
			
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				
				if (Objects.nonNull(resp)) {
					
					udpSend(resp, addr, port);
					
					if (resp.equals(".")) {
						setState(sendCommand(client));
						
						DatagramPacket packet = udpReceive();
						String data = new String(packet.getData(), 0, packet.getLength());
						
						Main.onmessage(data);
					}
				}
			}
		};
		
	}
	*/
	
}
