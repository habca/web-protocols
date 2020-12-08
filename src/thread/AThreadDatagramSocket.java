package thread;

import java.io.*;
import java.net.*;

import main.*;
import packet.*;

/**
 * Thread for receiving UDP-packets
 * 
 * Huom! Konstruktorin viimeinen rivi:
 *       new Thread(this).start();
 * 
 * @author Harri Linna
 * @version 20.11.2020
 *
 */
public abstract class AThreadDatagramSocket extends AThread {
	
	private static final Object lock = new Object();
	
	private DatagramSocket socket;
	private int size;
	
	// CONSTRUCTORS
	
	public AThreadDatagramSocket(InetAddress src_addr, int src_port, int size) throws SocketException {
		socket = new DatagramSocketError(src_port, src_addr);
		this.size = size;
	}
	
	public AThreadDatagramSocket(DatagramSocketError socket, int size) {
		this.socket = socket;
		this.size = size;
	}
	
	// PUBLIC METHODS
	
	public final InetAddress getAddress() {
		return socket.getInetAddress();
	}
	
	public final int getPort() {
		return socket.getPort();
	}
	
	/*
	public final void connect(InetAddress addr, int port) {
		if (socket.isConnected()) {
			socket.disconnect();
		}
		socket.connect(addr, port);
	}
	*/
	
	@Override
	public void onclose() throws IOException {
		synchronized (lock) {
			socket.close();
			Main.onmessage("DatagramSocket was closed");
		}
	}
	
	/**
	 * Kaikki lähtevät UDP-paketit kulkevat tätä kautta.
	 * @param packet Lähtevä UDP-paketti
	 */
	public void udpSend(DatagramPacket packet) {
		synchronized (lock) {
			try {
				Static.sleep(100);
				if (!socket.isClosed()) {
					socket.send(packet);
				}
			} catch (IOException e) {
				Main.onerror(e);
			}
		}
	}
	
	/*
	public void udpSend(DatagramPacket packet) {
		try {
			Thread.sleep((long) 100); // sync delay
			socket.send(packet);
		} catch (IOException | InterruptedException e) {
			Main.onerror(e);
		}
	}
	*/
	
	/**
	 * Kaikki saapuvat UDP-paketit kulkevat tätä kautta.
	 * @return Onnistuneesti vastaanotettu UDP-paketti
	 * @throws IOException Vastaanottaminen voi epäonnistua
	 */
	public DatagramPacket udpReceive() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[size], size);
		socket.receive(packet);
		return packet;
	}
	
}
