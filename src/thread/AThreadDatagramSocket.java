package thread;

import java.io.*;
import java.net.*;

import main.*;
import packet.DatagramSocketError;

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

	private DatagramSocketError socket;
	private int size;
	
	public AThreadDatagramSocket(InetAddress src_addr, int src_port, int size) throws SocketException {
		socket = new DatagramSocketError(src_port, src_addr);
		this.size = size;
	}
	
	public final void setErrorRates(double drop, double error, int delay) {
		socket.setErrorRates(drop, error, delay);
	}
	
	public AThreadDatagramSocket(DatagramSocketError socket, int size) {
		this.socket = socket;
		this.size = size;
	}
	
	@Override
	public final void onclose() throws IOException {
		socket.close();
		Main.onmessage("DatagramSocket was closed");
	}
	
	/**
	 * Kaikki lähtevät UDP-paketit kulkevat tätä kautta.
	 * @param packet Lähtevä UDP-paketti
	 */
	public final void udpSend(DatagramPacket packet) {
		try {
			Thread.sleep((long) 100); // sync delay
			socket.send(packet); // save to previous packet
		} catch (IOException | InterruptedException e) {
			Main.onerror(e);
		}
	}
	
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
