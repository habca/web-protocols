package thread;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Soketti gerenoi satunnasia virheitä. 
 * Oletuksena virheetön tiedonsiirto.
 * 
 * @author Harri Linna
 * @version 4.12.2020
 */
public class DatagramSocketError extends DatagramSocket {
	
	private static final int[] mask = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 };
	
	private double droprate = 0.0;
	private double biterror = 0.0;
	private int delayms = 0; // ms unit

	public DatagramSocketError(int port, InetAddress addr) throws SocketException {
		super(port, addr);
	}
	
	public final void setErrorRates(double drop, double error, int delay) {
		droprate = drop;
		biterror = error;
		delayms = delay;
	}

	@Override
	public void receive(DatagramPacket packet) throws IOException {
		Random rand = new Random();

		while (true) {
			super.receive(packet);

			try {
				// vastaanoton satunnainen viive
				Thread.sleep((long) intRange(0, delayms));
			} catch (InterruptedException e) {
				System.err.println("Thread.sleep was interrupted");
			}

			// pudotetaan satunnainen paketti
			if (rand.nextDouble() < droprate) {
				System.out.println("Dropped packet");
				continue;
			}

			// asetetaan satunnainen bittivirhe
			if (rand.nextDouble() < biterror) {
				byte[] data = packet.getData();
				int errbyte = intRange(0, packet.getLength() - 1); // CRC8
				int errbit = intRange(0, 7); // byte
				data[errbyte] = generateError(data[errbyte], errbit);
			}

			return;
		}
	}

	private static int intRange(int min, int max) {
		if (max - min <= 0)
			return 0;

		Random rand = new Random();
		return rand.nextInt(max - min) + min;
	}

	private static byte generateError(byte errbyte, int errbit) {
		byte left = (byte) (errbyte & ~mask[errbit]);
		byte right = (byte) (1 << 2);

		return (byte) (left ^ right); // xor
	}
}
