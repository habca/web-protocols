package tftp;

import java.io.*;
import java.net.*;

import thread.*;

/**
 * Huom! Älä erehdy käyttämään DatagramSocket.connect() metodia.
 * Aiheuttaa vain harmia ja UDP muutenkin yhteydetön protokolla.
 * @author Harri Linna
 * @version 9.12.2020
 */
public class TFTPServer extends AThreadDatagramSocket {
	
	public TFTPServer(int src_port) throws SocketException, UnknownHostException {
		super(InetAddress.getByName("localhost"), src_port, TFTPPacket.MAX_SIZE);
		new Thread(this).start();
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				DatagramPacket packet = udpReceive();
				
				// create socket to next available port
				InetSocketAddress addr = new InetSocketAddress(8070); // 8069+1
				DatagramSocketError socket = new DatagramSocketError(addr);
				socket.setErrorRates(0.2, 0.2, 0, 10); // generate errors
				new TFTPServerReceiver(socket, packet);
			}
			
		};
	}

}