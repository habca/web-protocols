package tftp;

import java.io.*;
import java.net.*;

import packet.*;
import thread.*;

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
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				// create socket to next available port
				DatagramSocketError socket = new DatagramSocketError(8070, getAddress());
				//socket.setErrorRates(0.2, 0.2, 1); // generate errors
				//socket.connect(packet.getAddress(), packet.getPort());
				new TFTPServerReceiver(socket);
			}
			
		};
	}

}
