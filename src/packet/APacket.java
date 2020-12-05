package packet;

import java.net.*;

/**
 * A wrapper class since DatagramPacket cannot be extended
 * @author Harri Linna
 * @version 26.11.2020
 */
public abstract class APacket {

	private DatagramPacket packet;
	
	public APacket(DatagramPacket packet) {
		this.packet = packet;
	}
	
	/**
	 * Tarvitaan kun lähetetään sokettiin dataa.
	 * @return paketti jota luokka edustaa
	 */
	public final DatagramPacket getDatagramPacket() {
		return packet;
	}
	
	public final InetAddress getAddress() {
		return packet.getAddress();
	}
	
	public final int getPort() {
		return packet.getPort();
	}
	
	public final byte[] getData() {
		return packet.getData();
	}
	
	public final int getLength() {
		return packet.getLength();
	}
	
	@Override
	public String toString() {
		return new String(getData(), 0, getLength());
	}
	
}
