package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import ftp.FTPClient;
import main.*;

public class TFTPPacket extends APacket {
	
	public static final int MAX_SIZE = 516; // RFC 1350
	
	public TFTPPacket(DatagramPacket packet) {
		super(packet);
	}
	
	public TFTPPacket(byte[] arr) {
		super(new DatagramPacket(arr, arr.length));
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
	
	public DatagramPacket ack() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 4); // opcode
		
		byte[] block = getBlock();
		
		out.write(block[0]);
		out.write(block[1]);
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, getAddress(), getPort());
	}
	
	public static DatagramPacket rrq(String filename, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder(filename);
		
		String str = Mjonot.erota(sb); // RRQ
		out.write((byte) 0); // opcode
		out.write((byte) 1); // opcode
		
		str = Mjonot.erota(sb); // filename
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		str = Mjonot.erota(sb); // mode
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	}
	
	public static DatagramPacket wrq(String filename, InetAddress addr, int port) throws IOException {
		// TODO: käytä RRQ-metodin valmista koodia
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder(filename);
		
		String str = Mjonot.erota(sb); // WRQ
		out.write((byte) 0); // opcode
		out.write((byte) 2); // opcode
		
		str = Mjonot.erota(sb); // filename
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		str = Mjonot.erota(sb); // mode
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new DatagramPacket(arr, arr.length, addr, port);
	}
	
}
