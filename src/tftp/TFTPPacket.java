package tftp;

import java.io.*;
import java.net.*;
import java.util.*;

import ftp.*;
import main.*;
import packet.*;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * UDP-packet for TFTP-protocol
 * @author Harri Linna
 * @version 5.12.2020
 */
public class TFTPPacket extends APacket {
	
	// 516 bytes RFC - 4 bytes HEADER - 1 byte CRC8 = 511 bytes DATA
	public static final int MAX_SIZE = 516; // RFC 1350
	public static final int MAX_DATA = 511; // RFC 1350
	
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
	
	public TFTPPacket(APacketError packet) {
		super(packet.removeCRC8());
	}
	
	// PUBLIC METHODS
	
	public int getBlock() {
		if (getOpcode() == 3 || getOpcode() == 4 || getOpcode() == 5) {
			return Static.bytesToInt(Arrays.copyOfRange(getData(), 2, 4));
		}
		return 0; // default block
	}
	
	public int nextBlock() {
		return getBlock() + 1;
	}
	
	@Override
	public String toString() {
		assert getOpcode() == 3; // should be DATA
		return new String(getData(), 4, getLength()-4);
	}
	
	public String getFileName() {
		assert getOpcode() == 1 || getOpcode() == 2; // should be RRQ or WRQ
		for (int i = 2; i < getLength(); i++) {
			if (getData()[i] == (byte) 0) {
				return new String(getData(), 2, i-2);
			}
		}
		return "";
	}
	
	public int getOpcode() {
		return FTPClient.calcPort(getData()[0], getData()[1]);
	}
	
	// TYPE OF RESPONSE
	
	public boolean isRRQ() { 
		return getOpcode() == 1;
	}
	
	public boolean isWRQ() { 
		return getOpcode() == 2;
	}
	
	public boolean isDATA() {
		return getOpcode() == 3;
	}
	
	public boolean isDATA(TFTPPacket packet) {
		return getOpcode() == 3 && getBlock() == packet.nextBlock();
	}
	
	public boolean isACK(TFTPPacket packet) {
		return getOpcode() == 4 && getBlock() == packet.getBlock();
	}
	
	public boolean isERROR() {
		return getOpcode() == 5;
	}
	
	// FACTORY METHODS
	
	public static TFTPPacket make_rrq(String filename, String mode, InetAddress addr, int port) throws IOException {
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
		));
	}
	
	public static TFTPPacket make_wrq(String filename, String mode, InetAddress addr, int port) throws IOException {
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
		));
	}
	
	public static TFTPPacket make_data(byte[] data, int block, 
			InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 3); // opcode
		
		out.write(Static.intToBytes(block, 2)); // block
		out.write(data); // data
		
		byte[] arr = out.toByteArray();
		assert arr.length <= MAX_SIZE; // RFC 1350
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		));
	}
	
	public static TFTPPacket make_ack(TFTPPacket packet) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 4); // opcode
		
		out.write(Static.intToBytes(packet.getBlock(), 2)); // block
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, packet.getAddress(), packet.getPort()
		));
	}
	
	public static TFTPPacket make_err(int err, InetAddress addr, int port) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		out.write((byte) 0); // opcode
		out.write((byte) 5); // opcode
		
		out.write((byte) 0); // error code
		out.write((byte) err); // error code
		
		out.write(ERROR[err].getBytes()); // error message
		out.write((byte) 0); // delimiter
		
		byte[] arr = out.toByteArray();
		return new TFTPPacket(new DatagramPacket(
				arr, arr.length, addr, port
		));
	}
	
	// JUNIT TESTS
	
	public static class TestTFTPPacket {
		
		@Test
		public void testTFTPPacket() throws IOException {
			TFTPPacket send1 = make_data("testdata".getBytes(), 1, null, 0);
			APacketError send2 = APacketError.convertToCRC8(send1.getDatagramPacket());
			APacketError receive2 = new APacketError(send2.getDatagramPacket());
			TFTPPacket receive1 = new TFTPPacket(receive2.removeCRC8());
			
			assertTrue(send1.getLength() == send2.getLength() - 1);
			assertFalse(Arrays.equals(send1.getData(), send2.getData()));
			
			assertTrue(send1.getLength() == receive1.getLength());
			assertTrue(Arrays.equals(send1.getData(), receive1.getData()));
			
			assertTrue(send2.getLength() == receive2.getLength());
			assertTrue(Arrays.equals(send2.getData(), receive2.getData()));
			
			assertTrue(receive1.getLength() == receive2.getLength() - 1);
			assertFalse(Arrays.equals(receive1.getData(), receive2.getData()));
		}
		
		@Test
		public void testNextBlock() throws IOException {
			TFTPPacket rrq = make_rrq("filename", "octet", null, 0);
			TFTPPacket ack1 = make_ack(rrq);
			
			TFTPPacket data = make_data("testdata".getBytes(), rrq.nextBlock(), null, 0);
			TFTPPacket ack2 = make_ack(data);
			
			assertTrue(ack1.getBlock() == rrq.getBlock());
			assertTrue(ack1.isACK(rrq));
			
			assertTrue(data.getBlock() == ack1.getBlock() + 1);
			assertTrue(data.getBlock() == ack1.nextBlock());
			
			assertTrue(ack2.getBlock() == data.getBlock());
			assertTrue(ack2.isACK(data));
			
			assertTrue(ack2.getBlock() == rrq.getBlock() + 1);
			assertFalse(ack2.isACK(rrq));
		}
		
		@Test
		public void testMake_rrq() throws IOException {
			String filename = "/home/user/file";
			String mode = "octet";
			
			TFTPPacket rrq = make_rrq(filename, mode, null, 0);
				
			assertTrue(rrq.getOpcode() == 1);
			assertTrue(rrq.isRRQ());
			assertEquals(filename, rrq.getFileName());				
			assertEquals(4 + filename.length() + mode.length(), rrq.getLength());
		}
		
		@Test
		public void testMake_wrq() throws IOException {
			String filename = "/home/user/file";
			String mode = "octet";
			
			TFTPPacket wrq = make_wrq(filename, mode, null, 0);
				
			assertTrue(wrq.getOpcode() == 2);
			assertTrue(wrq.isWRQ());
			assertEquals(filename, wrq.getFileName());				
			assertEquals(4 + filename.length() + mode.length(), wrq.getLength());
		}
		
		@Test
		public void testMake_data() throws IOException {
			for (int i = 0; i < ERROR.length; i++) {
				TFTPPacket data = make_data(ERROR[i].getBytes(), i, null, 0);
				
				assertTrue(data.getOpcode() == 3);
				assertTrue(data.isDATA());
				assertTrue(data.getBlock() == i);
				assertEquals(4 + ERROR[i].length(), data.getLength());
				assertEquals(ERROR[i], data.toString());
				
				byte[] prefix = Arrays.copyOfRange(data.getData(), 0, 4);
				byte[] suffix = Arrays.copyOfRange(data.getData(), 4, data.getLength());
				
				assertTrue(Arrays.equals(new byte[] {0,3,0,(byte) i}, prefix));
				assertTrue(Arrays.equals(ERROR[i].getBytes(), suffix));
			}
			
			TFTPPacket test1 = make_data(new byte[MAX_DATA], 0, null, 0);
			APacketError test2 = APacketError.convertToCRC8(test1.getDatagramPacket());
			TFTPPacket test3 = new TFTPPacket(test2.removeCRC8());
			TFTPPacket test4 = new TFTPPacket(test2);
			
			assertTrue(test1.getLength() == MAX_SIZE - 1);
			assertTrue(test2.getLength() == MAX_SIZE);
			assertTrue(test3.getLength() == MAX_SIZE - 1);
		
			assertFalse(Arrays.equals(test1.getData(), test2.getData()));
			assertTrue(Arrays.equals(test1.getData(), test3.getData()));
			assertTrue(Arrays.equals(test3.getData(), test4.getData()));
		}
				
		@Test
		public void testMake_ack() throws IOException {
			List<TFTPPacket> test = new ArrayList<TFTPPacket>();
			test.add(make_rrq("filename", "octet", null, 0));
			test.add(make_wrq("filename", "octet", null, 0));
			test.add(make_data(new byte[0], 1, null, 0));
			test.add(make_data(new byte[0], test.get(test.size()-1).nextBlock(), null, 0));
			
			for (TFTPPacket rec : test) {
				TFTPPacket ack = make_ack(rec);
				
				assertEquals(rec.getData().length, rec.getLength());
				assertEquals(ack.getData().length, ack.getLength());
				assertTrue(ack.getLength() == 4);
				
				assertTrue(rec.getOpcode() != ack.getOpcode());
				assertTrue(ack.getOpcode() == 4);
				assertTrue(ack.isACK(rec));
				
				assertTrue(rec.getBlock() == ack.getBlock());
				
				byte[] prefix = Arrays.copyOfRange(ack.getData(), 0, 2);
				byte[] suffix = Arrays.copyOfRange(ack.getData(), 2, ack.getLength());
				
				assertTrue(Arrays.equals(new byte[] {0,4}, prefix));
				assertTrue(Arrays.equals(Static.intToBytes(rec.getBlock(), 2), suffix));
				assertTrue(rec.getBlock() == Static.bytesToInt(suffix));
			}
		}
		
		@Test
		public void testMake_err() throws IOException {	
			for (int i = 0; i < ERROR.length; i++) {
				TFTPPacket packet = make_err(i, null, 0);
				
				assertEquals(packet.getData().length, packet.getLength());
				assertEquals(5 + ERROR[i].length(), packet.getLength());
				
				assertTrue(packet.getOpcode() == 5);
				assertTrue(packet.isERROR());
				assertTrue(packet.getBlock() == i);
				
				byte[] prefix = Arrays.copyOfRange(packet.getData(), 0, 4);
				byte[] suffix = Arrays.copyOfRange(packet.getData(), packet.getLength()-1, packet.getLength());
				byte[] middle = Arrays.copyOfRange(packet.getData(), 4, packet.getLength()-1);
				
				assertTrue(Arrays.equals(new byte[] {0,5,0,(byte) i}, prefix));
				assertTrue(Arrays.equals(new byte[] {0}, suffix));
				assertTrue(ERROR[i].equals(new String(middle)));
			}
		}
		
	}
	
}
