package tftp;

import java.io.*;
import java.net.*;

import ftp.FTPClient;
import main.*;

public class TFTPPacket extends APacket {
	
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
	
	private int size() {
		return getData().length;
	}
	
	public TFTPPacket(byte[] arr, InetAddress addr, int port) {
		super(new DatagramPacket(arr, arr.length, addr, port));
	}
	
	public byte[] getBlock() {
		int opcode = getOpcode();
		if (opcode == 3 || opcode == 4) {
			return new byte[] {getData()[2], getData()[3]};
		}
		return new byte[2];
	}
	
	public int getOpcode() {
		return FTPClient.calcPort(getData()[0], getData()[1]);
	}
	
	public String getFileName() {
		final int start = 2;
		int count = 0;
		for (int i = start; i < size(); i++) {
			if (getData()[i] == (byte) 0) {
				break;
			}
			count++;
		}
		return new String(getData(), start, count);
	}
	
	public String getFileData() {
		final int start = 4;
		return new String(getData(), start, size()-start);
	}
	
	public static DatagramPacket rrq(String filename, String mode, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 1); // opcode
		
		out.write(filename.getBytes()); // filename
		out.write((byte) 0); // delimiter
		
		out.write(mode.getBytes()); // mode
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	}
	
	public static DatagramPacket wrq(String filename, String mode, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 2); // opcode
		
		out.write(filename.getBytes()); // filename
		out.write((byte) 0); // delimiter
		
		out.write(mode.getBytes()); // mode
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	}
	
	public static DatagramPacket data(byte[] data, byte[] block, 
			InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 3); // opcode
		
		out.write(block); // block
		
		out.write(data); // data
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	}
	
	public DatagramPacket ack() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 4); // opcode
		
		out.write(getBlock()); // block
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, getAddress(), getPort());
	}
	
	public static DatagramPacket err(int err, String str, 
			InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 5); // opcode
		
		out.write((byte) 0); // error code
		out.write((byte) err); // error code
		
		out.write(str.getBytes()); // error message
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	} 
	
	public byte[] nextBlock() {
		int current = Static.bytesToInt(getBlock());
		return Static.intToBytes(current + 1);
	}
	
}
