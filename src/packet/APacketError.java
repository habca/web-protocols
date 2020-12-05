package packet;

import java.net.*;
import java.util.*;

import static org.junit.Assert.*;

import org.junit.*;

/**
 * UDP packet that implements CRC8 checksum
 * @author Harri Linna
 * @version 5.12.2020
 */
public abstract class APacketError extends APacket {

	private static final int[] mask = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 };
	
	public APacketError(DatagramPacket packet) {
		super(convertToCRC8(packet));
	}
	
	// PUBLIC METHODS
	
	public final boolean isCorrupted() {
		return checksum(getData()) != 0;
	}
	
	@Override
	public String toString() {
		return new String(getData(), 0, getLength() - 1); // remove CRC8
	}
	
	// PRIVATE METHODS
	
	private static DatagramPacket convertToCRC8(DatagramPacket packet) {
		byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
		data = encode(data); // add CRC8
		return new DatagramPacket(data, data.length,
				packet.getAddress(), packet.getPort());
	}
	
	private static byte[] encode(byte[] arr) {
		int size = arr.length;
		byte[] data = new byte[size + 1];

		data[size] = checksum(arr);
		for (int i = 0; i < size; i++) {
			data[i] = arr[i];
		}

		return data;
	}
	
	private static byte checksum(byte[] data) {
		byte register = 0;

		for (int i = 0; i < data.length; i++) {
			register = generateByte(register, data[i]);
		}

		return generateByte(register, (byte) 0);
	}
	
	private static byte generateByte(byte init, byte data) {
		byte register = init;
		int last = mask.length - 1;

		for (int i = last; i >= 0; i--) {
			byte previous = register;
			register = (byte) (register << 1);

			byte left = (byte) ((previous & mask[last]) >>> last);

			byte right = (byte) ((previous & mask[1]) >>> 1);
			byte bit = (byte) ((left ^ right) << 2);
			register = (byte) (register & ~mask[2]);
			register = (byte) (register | bit);

			right = (byte) ((previous & mask[0]) >>> 0);
			bit = (byte) ((left ^ right) << 1);
			register = (byte) (register & ~mask[1]);
			register = (byte) (register | bit);

			right = (byte) ((data & mask[i]) >>> i);
			bit = (byte) ((left ^ right) << 0);
			register = (byte) (register & ~mask[0]);
			register = (byte) (register | bit);
		}

		return register;
	}
	
	// JUNIT TESTS
	
	public static class TestAPacketError {
		
		// tarvitaan vain testeihin siksi private
		private static boolean corrupt(byte[] data) {
			return checksum(data) != 0; 
		}
		
		@Test
		public void testChecksum() {
			String error = "Tarkistussumma väärin: ";
			
			byte input = 97;
			byte[] test = { input };
			byte output = checksum(test);

			assertEquals(error, 97, input); // 01100001 bin, 97 dec
			assertEquals(error, 32, output); // 00100000 bin, 32 dec
		}
		
		@Test
		public void testIsCorrupted() {
			String error = "Oikeellisuustarkistus väärin: ";
			
			byte input = 97;
			byte[] test = { input };
			byte output = checksum(test);

			byte[] test2 = { input, output };
			boolean result = !corrupt(test2);

			byte[] test3 = { input, 33 }; // 00100001 bin, dec 33
			boolean result2 = corrupt(test3);

			assertTrue(error, result); // true
			assertTrue(error, result2); // true

			byte seq = 5;
			byte[] test4 = { seq, input };
			byte output2 = checksum(test4);

			byte[] test5 = { seq, input, output2 };
			boolean result3 = !corrupt(test5);

			assertTrue(error, result3); // true
		}	
	}
	
}
