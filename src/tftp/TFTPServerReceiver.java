package tftp;

import thread.*;
import java.io.*;
import java.net.*;

import main.*;
import packet.*;

public class TFTPServerReceiver extends AThreadDatagramSocket {

	private TFTPPacket previous;
	private FileReaderWriter manager;
	private ThreadScheduled schedule;
	
	private TFTPPacket packet;
	
	public TFTPServerReceiver(InetAddress src_addr, int src_port, int size) throws SocketException, UnknownHostException {
		super(src_addr, src_port, size);
		
		// schedule runs a task at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		
		// start listening socket
		new Thread(this).start();
	}
	
	public TFTPServerReceiver(DatagramSocketError socket, TFTPPacket packet, int size) {
		super(socket, size);
		
		// schedule runs a task at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		
		this.packet = packet;
		
		// start listening socket
		new Thread(this).start();
	}
	
	private synchronized void onschedule(TFTPPacket packet) {
		// include CRC8 checksum to all the departing packets
		APacketError error = APacketError.convertToCRC8(packet.getDatagramPacket());
		
		// schedule an event with a fixed interval
		schedule.setTask(new Runnable() {
			
			@Override
			public synchronized void run() {
				udpSend(error.getDatagramPacket());
			}
			
		});
	}
	
	@Override
	public synchronized void onclose() throws IOException {
		schedule.cancelTask();
		schedule.close();
		super.onclose();
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				//APacketError response = new APacketError(udpReceive());
				APacketError response = new APacketError(packet.getDatagramPacket());
				TFTPPacket packet = new TFTPPacket(response);
				
				if (!response.isCorrupted()) {
					// File manager for reading or writing a file
					manager = new FileReaderWriter(packet.getFileName(), TFTPPacket.MAX_DATA); // RFF 1350
					
					// RRQ received
					if (packet.isRRQ()) {
						setState(onrrq());
						onschedule(previous = TFTPPacket.make_ack(packet));
					}
					
					// WRQ received
					if (packet.isWRQ()) {
						setState(onwrq());
						onschedule(previous = TFTPPacket.make_ack(packet));
					}
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				// last packet sent, close connection
				if (!manager.hasNext()) {
					close(); // close connection
					return;
				}
				
				// Create next DATA packet to send
				TFTPPacket curr = TFTPPacket.make_data(
						manager.next(), previous.nextBlock(),
						previous.getAddress(), previous.getPort()
				);
				
				// Send next DATA packet
				onschedule(previous = curr);
				
				while (true) {
					APacketError response = new APacketError(udpReceive());
					TFTPPacket packet = new TFTPPacket(response);
					
					// ACK received, continue to send
					if (!response.isCorrupted() && packet.isACK(curr)) {
						return; // continue to send
					}
				}
			}
			
		};
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				// DATA received, continue unless last packet
				if (!response.isCorrupted()) {
					FileReaderWriter.write(manager.getName(), packet.toString());
					onschedule(previous = TFTPPacket.make_ack(packet));
					
					// last packet received, close connection
					if (packet.getLength() < TFTPPacket.MAX_SIZE) {
						close(); // close connection
					}
				}
			}
			
		};
	}

}
