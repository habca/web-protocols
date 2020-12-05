package tftp;

import thread.*;
import java.io.*;
import java.net.*;

import main.*;

public class TFTPServer extends AThreadDatagramSocket {

	private DatagramPacket previous;
	private FileReaderWriter manager;
	
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
				
				// File manager for reading or writing a file
				// 512 bytes RFC - 1 byte CRC8 = 511 bytes
				manager = new FileReaderWriter(packet.getFileName(), 511); // RFF 1350
				
				// received UDP-packet for RRQ
				if (packet.getOpcode() == 1) {
					setState(onrrq());
					udpSend(previous = TFTPPacket.make_ack(packet));
				}
				
				// received UDP-packet for WRQ
				if (packet.getOpcode() == 2) {
					setState(onwrq());
					udpSend(previous = TFTPPacket.make_ack(packet));
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				
				if (!manager.hasNext()) {
					setState(onreceive());
					return;
				}
				
				TFTPPacket prev = new TFTPPacket(previous);
				byte[] block = prev.nextBlock();
				byte[] data = manager.next();
				
				udpSend(previous = TFTPPacket.make_data(data, block,
						previous.getAddress(), previous.getPort()
				));
				TFTPPacket packet = new TFTPPacket(udpReceive());
			}
			
		};
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				// received UDP-packet for DATA
				if (packet.getOpcode() == 3) {
					FileReaderWriter.write(manager.getName(), packet.toString());
					udpSend(previous = TFTPPacket.make_ack(packet));
				}
				
				// last packet was received, close connection
				if (packet.getLength() < TFTPPacket.MAX_SIZE) {
					setState(onreceive());
				}
			}
			
		};
	}

}
