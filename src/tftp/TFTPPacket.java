package tftp;

import java.io.*;
import java.net.*;
import java.util.*;

import ftp.*;
import main.*;
import packet.*;

/**
 * UDP-packet for TFTP-protocol
 * @author Harri Linna
 * @version 5.12.2020
 */
public class TFTPPacket extends APacketError {
	
	public static final int MAX_SIZE = 516; // RFC 1350
	
	public static final String[] ERROR = {
			"Not defined, see error message (if any).",
			"File not found.",
			"Access violation.",
			"Disk full or allocation exceeded.",
			"Illegal TFTP operation.",
			"Unknown transfer ID.",
			"File already exists.",
			"No such user."
	};
	
	public TFTPPacket(DatagramPacket packet) {
		super(packet);
	}
	
	public byte[] getBlock() {
		assert getOpcode() == 3 || getOpcode() == 4; // data or ack
		return new byte[] { 
				getData()[2], getData()[3] 
		};
	}
	
	public byte[] nextBlock() {
		int current = Static.bytesToInt(getBlock());
		return Static.intToBytes(current + 1);
	}
	
	public int getOpcode() {
		return FTPClient.calcPort(getData()[0], getData()[1]);
	}
	
	public boolean isACK(TFTPPacket packet) {
		return !isCorrupted() && getOpcode() == 4 && 
				Arrays.equals(getBlock(), packet.getBlock()
		);
	}
	
	public String getFileName() {
		final int start = 2;
		int end = -1;
		for (int i = start; i < getLength(); i++) {
			if (getData()[i] == (byte) 0) {
				end = i;
				break;
			}
		}
		return new String(getData(), start, end-start);
	}
	
	@Override
	public String toString() {
		assert getOpcode() == 3; // data
		// 4 bytes header + 1 byte CRC8 = 5 bytes
		return new String(getData(), 4, getLength()-6);
	}
	
	// FACTORY METHODS
	
	public static DatagramPacket make_rrq(String filename, String mode, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 1); // opcode
		
		out.write(filename.getBytes()); // filename
		out.write((byte) 0); // delimiter
		
		out.write(mode.getBytes()); // mode
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		)).getDatagramPacket();
	}
	
	public static DatagramPacket make_wrq(String filename, String mode, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 2); // opcode
		
		out.write(filename.getBytes()); // filename
		out.write((byte) 0); // delimiter
		
		out.write(mode.getBytes()); // mode
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		)).getDatagramPacket();
	}
	
	public static DatagramPacket make_data(byte[] data, byte[] block, 
			InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 3); // opcode
		
		out.write(block); // block
		out.write(data); // data
		
		byte[] arr = out.toByteArray();
		assert arr.length <= 516; // RFC 1350
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		)).getDatagramPacket();
	}
	
	public static DatagramPacket make_ack(TFTPPacket packet) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 4); // opcode
		
		out.write(packet.getBlock()); // block
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, packet.getAddress(), packet.getPort()
		)).getDatagramPacket();
	}
	
	public static DatagramPacket make_err(int err, String str, 
			InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 5); // opcode
		
		out.write((byte) 0); // error code
		out.write((byte) err); // error code
		
		out.write(str.getBytes()); // error message
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		)).getDatagramPacket();
	}
	
}
