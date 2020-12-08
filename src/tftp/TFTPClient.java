package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import main.*;
import packet.*;
import thread.*;

public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private TFTPPacket previous;
	private FileReaderWriter manager;
	private ThreadScheduled schedule;
	
	private InetAddress addr;
	private int port;
	
	public TFTPClient(int src_port, InetAddress dst_addr, int dst_port)
			throws SocketException, UnknownHostException {
		super(InetAddress.getByName("localhost"), src_port, TFTPPacket.MAX_SIZE);
		
		addr = dst_addr;
		port = dst_port;
		
		// schedule runs a task at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		
		// start listening socket
		new Thread(this).start();
		
		String format = "Client using UDP operates on %s:%d";
		Main.onmessage(String.format(
				format, InetAddress.getByName("localhost"), src_port)
		);
	}
	
	private void onschedule(TFTPPacket packet) {
		// include CRC8 checksum to all the departing packets
		APacketError error = APacketError.convertToCRC8(packet.getDatagramPacket());
		
		// schedule an event with a fixed interval
		schedule.setTask(new Runnable() {
			
			@Override
			public void run() {
				udpSend(error.getDatagramPacket());
			}
		
		});
	}
	
	@Override
	public void onclose() throws IOException {
		schedule.cancelTask();
		schedule.close();
		super.onclose();
	}
	
	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				"RRQ <filename> octet\n" +
				"WRQ <filename> octet"
		);
	}
	
	@Override
	public void send(String str) throws IOException {
		StringBuilder sb = new StringBuilder(str);
		
		// parse arguments for RRQ or WRQ
		String command = Mjonot.erota(sb);
		String filename = Mjonot.erota(sb);
		String mode = Mjonot.erota(sb);
		
		// send RRQ request 
		if (command.equals("RRQ")) {
			onschedule(previous = TFTPPacket.make_rrq(
					filename, mode, addr, port
			));
		}
		
		// send WRQ request
		if (command.equals("WRQ")) {
			onschedule(previous = TFTPPacket.make_wrq(
					filename, mode, addr, port
			));
		}
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				addr = packet.getAddress();
				port = packet.getPort();
				
				// RRQ or WRQ was accepted
				if (!response.isCorrupted() && packet.isACK(previous)) {
					// File manager for reading or writing a file
					manager = new FileReaderWriter(previous.getFileName(), TFTPPacket.MAX_DATA); // RFC 1350
					
					// RRQ was accepted
					if (previous.isRRQ()) {
						setState(onrrq());
						return;
					}
					
					// WRQ was accepted
					if (previous.isWRQ()) {
						setState(onwrq());
						return;
					}
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				// DATA received, continue unless last packet
				if (!response.isCorrupted() && packet.isDATA(previous)) {
					FileReaderWriter.write(manager.getName(), packet.toString());
					onschedule(previous = TFTPPacket.make_ack(packet));
					
					// last packet received, close connection
					if (response.getLength() < TFTPPacket.MAX_SIZE) {
						close(); // close connection
						Main.onmessage("RRQ completed");
					}
				}
			}
			
		};
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				// last packet sent, close connection
				if (!manager.hasNext()) {
					Main.onmessage("WRQ completed");
					close();
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
					if (!response.isCorrupted() && packet.isACK(packet)) {
						break; // continue to send
					}
				}
			}
			
		};
	}
	
}
