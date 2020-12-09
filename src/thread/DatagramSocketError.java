package thread;

import static org.junit.Assert.*;

import org.junit.*;

import java.io.*;
import java.net.*;
import java.util.*;

import main.*;

/**
 * Soketti gerenoi satunnasia virheitä. 
 * Oletuksena virheetön tiedonsiirto.
 * @author Harri Linna
 * @version 4.12.2020
 */
public class DatagramSocketError extends DatagramSocket {
	
	private double dropRate = 0.0;
	private double errorRate = 0.0;
	private int minDelay = 0; // ms unit
	private int maxDelay = 0;

	// CONSTRUCTORS
	
	public DatagramSocketError(int port, InetAddress addr) throws SocketException {
		super(port, addr);
	}
	
	public DatagramSocketError(InetSocketAddress addr) throws SocketException {
		super(addr);
	}
	
	// PUBLIC METHODS
	
	public final void setErrorRates(double dropRate, double errorRate, int minDelay, int maxDelay) {
		this.dropRate = dropRate;
		this.errorRate = errorRate;
		this.minDelay = minDelay*1000; // seconds to milliseconds
		this.maxDelay = maxDelay*1000; // seconds to milliseconds
	}

	@Override
	public void receive(DatagramPacket packet) throws IOException {
		Random rand = new Random();

		while (true) {
			super.receive(packet);

			// vastaanoton satunnainen viive
			if (maxDelay > 0) {
				if (maxDelay - minDelay > 0) {
					Static.sleep(intRange(minDelay, maxDelay));
				} else {
					Static.sleep(maxDelay);
				}
			}
			
			// pudotetaan satunnainen paketti
			if (rand.nextDouble() < dropRate) {
				Main.onmessage("Dropped packet");
				continue;
			}

			// asetetaan satunnainen bittivirhe
			if (rand.nextDouble() < errorRate) {
				Main.onmessage("Bit error");
				generateError(packet);
			}

			return;
		}
	}
	
	public static void generateError(DatagramPacket packet) {
		if (packet.getLength() == 1) {
			byte[] data = packet.getData();
			int errbit = intRange(0, 7); // byte
			data[0] = generateError(data[0], errbit);
		}
		
		if (packet.getLength() > 1) {
			byte[] data = packet.getData();
			int errbyte = intRange(0, packet.getLength() - 1); // CRC8
			int errbit = intRange(0, 7); // byte
			data[errbyte] = generateError(data[errbyte], errbit);
		}
	}
	
	// PRIVATE METHODS
	
	private static int intRange(int min, int max) {
		Random rand = new Random();
		return rand.nextInt(Math.abs(max - min)) + Math.min(min,max);
	}

	private static byte generateError(byte errbyte, int errbit) {
		return (byte) (errbyte ^ 1 << errbit & 0xFF);
	}
	
	// JUNIT TESTS
	
	public static class TestDatagramSocketError {
		
		@Test
		public void testIntRange() {
			for (int i = 1; i <= 100; i++) {
				int rand = intRange(0,7);
				assertTrue(rand >= 0 && rand <= 7);
			}
		}
		
		@Test
		public void testGenerateError() {
			
			//  97 DECIMAL 01100001 SIGNED 8-BIT
			//  96 DECIMAL 01100000 SIGNED 8-BIT
			//  99 DECIMAL 01100011 SIGNED 8-BIT
			// 101 DECIMAL 01100101 SIGNED 8-BIT
			// 105 DECIMAL 01101001 SIGNED 8-BIT
			// 113 DECIMAL 01110001 SIGNED 8-BIT
			//  65 DECIMAL 01000001 SIGNED 8-BIT
			//  33 DECIMAL 00100001 SIGNED 8-BIT
			// -31 DECIMAL 11100001 SIGNED 8-BIT
			
			byte[] result = { 96, 99, 101, 105, 113, 65, 33, -31 };
			for (int i = 0; i < result.length; i ++) {
				assertEquals(result[i], generateError((byte) 97, i));
			}
		}
		
		@Test
		public void testDatagramSocketError() {
			List<byte[]> test = new ArrayList<byte[]>();
			test.add(new byte[] { 97, 96 });
			for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
				test.add(new byte[] {(byte) i });
			}
			
			for (byte[] arr : test) {
				byte[] tmp = Arrays.copyOf(arr, arr.length);
				
				DatagramPacket first = new DatagramPacket(arr, arr.length);
				DatagramPacket second = new DatagramPacket(tmp, tmp.length);
				generateError(second);
				
				assertTrue(first.getData() != second.getData());
				assertTrue(first.getLength() == second.getLength());
				assertFalse(Arrays.equals(first.getData(), second.getData()));
			}
			
			byte[] exception = new byte[] { };
			byte[] tmp = Arrays.copyOf(exception, exception.length);
			
			DatagramPacket first = new DatagramPacket(exception, exception.length);
			DatagramPacket second = new DatagramPacket(tmp, tmp.length);
			generateError(second);
			
			assertTrue(first.getData() != second.getData());
			assertTrue(first.getLength() == second.getLength());
			assertTrue(Arrays.equals(first.getData(), second.getData()));
		}
		
	}
	
}
