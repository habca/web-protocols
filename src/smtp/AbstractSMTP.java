package smtp;

import java.io.*;
import java.net.*;

/**
 * Similar attributes and methods for SMTP
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 9.11.2020
 */
public abstract class AbstractSMTP implements Runnable {

	private DatagramSocket socket;
	private int size;
	private IThread runner;
	
	public AbstractSMTP(DatagramSocket socket, int size) {
		this.socket = socket;
		this.size = size;
	}
	
	@Override
	public abstract void run();

	public final void setState(IThread state) {
		runner = state;
	}
	
	public final IThread getState() {
		return runner;
	}
	
	public final DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	
	public final void udpSend(String str, InetAddress addr, int port) throws IOException {
		byte[] data = str.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		socket.send(packet);
	}
}
