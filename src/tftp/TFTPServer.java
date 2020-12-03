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
				manager = new FileReaderWriter(packet.getFileName());
				
				// received UDP-packet for RRQ
				if (packet.getOpcode() == 1) {
					setState(onrrq());
					udpSend(previous = packet.ack());
				}
				
				// received UDP-packet for WRQ
				if (packet.getOpcode() == 2) {
					setState(onwrq());
					udpSend(previous = packet.ack());
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				/*
				if (!manager.hasNext()) {
					setState(onreceive());
					return;
				}
				
				TFTPPacket prev = new TFTPPacket(previous);
				byte[] block = prev.nextBlock();
				byte[] arr = manager.read(512); // RFC 1350
				udpSend(previous = TFTPPacket.data(arr, block));
				TFTPPacket packet = new TFTPPacket(udpReceive());
				*/
				
				TFTPPacket prev = new TFTPPacket(previous);
				byte[] block = prev.nextBlock();
				byte[] arr = manager.read();
				udpSend(previous = TFTPPacket.data(arr, block,
						previous.getAddress(), previous.getPort()
				));
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				setState(onreceive());
			}
			
		};
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				// TODO Auto-generated method stub
			}
			
		};
	}

}
