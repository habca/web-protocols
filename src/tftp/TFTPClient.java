package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import main.*;
import thread.*;

public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private DatagramPacket previous;
	private FileReaderWriter manager;
	
	private InetAddress addr;
	private int port;
	
	public TFTPClient(int src_port, InetAddress dst_addr, int dst_port)
			throws SocketException, UnknownHostException {
		super(InetAddress.getByName("localhost"), src_port, TFTPPacket.MAX_SIZE);
		addr = dst_addr;
		port = dst_port;
		new Thread(this).start();
		
		String format = "Client using UDP operates on %s:%d";
		Main.onmessage(String.format(
				format, InetAddress.getByName("localhost"), src_port));
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				// RRQ or WRQ was accepted
				if (packet.getOpcode() == 4) {
					
					// File manager for reading or writing a file
					TFTPPacket prev = new TFTPPacket(previous);
					manager = new FileReaderWriter(prev.getFileName(), 512); // RFC 1350
					
					// RRQ was accepted
					if (prev.getOpcode() == 1) {
						setState(onrrq());
					}
					
					// WRQ was accepted
					if (prev.getOpcode() == 2) {
						setState(onwrq());
					}
					
				}
				
				// RRQ or WRQ was rejected
				if (packet.getOpcode() == 5) {
					// TODO: parsi virheteksti
					Main.onmessage("error text here");
				}
			}
			
		};
	}

	@Override
	public void send(String str) throws IOException {
		StringBuilder sb = new StringBuilder(str);
		
		// parse arguments for RRQ or WRQ
		String command = Mjonot.erota(sb);
		String filename = Mjonot.erota(sb);
		String mode = Mjonot.erota(sb);
		
		// send UDP-packet for RRQ 
		if (command.equals("RRQ")) {
			udpSend(previous = TFTPPacket.rrq(filename, mode, addr, port));
		}
		
		// send UDP-packet for WRQ
		if (command.equals("WRQ")) {
			udpSend(previous = TFTPPacket.wrq(filename, mode, addr, port));
		}
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				"RRQ <filename> octet\n" +
				"WRQ <filename> octet"
		);
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				// received UDP-packet for DATA
				if (packet.getOpcode() == 3) {
					FileReaderWriter.write(manager.getName(), packet.getFileData());
					udpSend(previous = packet.ack());
				}
				
				// last packet was received, close connection
				if (packet.getLength() < TFTPPacket.MAX_SIZE) {
					setState(onreceive());
				}
			}
			
		};
	}
	
	private IThread onwrq() {
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
				
				udpSend(previous = TFTPPacket.data(data, block,
						previous.getAddress(), previous.getPort()
				));
				TFTPPacket packet = new TFTPPacket(udpReceive());
			}
			
		};
	}
	
}
