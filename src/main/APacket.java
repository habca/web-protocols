package main;

import java.net.*;

/**
 * A wrapper class since DatagramPacket can't be extended
 * 
 * @author Harri Linna
 * @version 26.11.2020
 */
public abstract class APacket {

	private DatagramPacket packet;
	
	public APacket(DatagramPacket packet) {
		this.packet = packet;
	}

	@Override
	public final String toString() {
		return new String(packet.getData(), 0, packet.getLength());
	}
	
	public final DatagramPacket getDatagramPacket() {
		return packet;
	}
	
	public final int getPort() {
		return packet.getPort();
	}
	
	public final InetAddress getAddress() {
		return packet.getAddress();
	}
	
	public final byte[] getData() {
		return packet.getData();
	}
	
	public final int getLength() {
		return packet.getLength();
	}
	
}
