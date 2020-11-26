package tftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private DatagramPacket previous;
	
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
				
				Main.onmessage("Client: response arrived");
				// Tässä pitäisi tehdä mitä?
			}
			
		};
	}

	@Override
	public void send(String str) throws IOException {
		
		Main.onmessage("Client: request sent");
		
		if (str.startsWith("RRQ")) {
			udpSend(previous = TFTPPacket.rrq(str, addr, port));
			setState(onrrq());
		}
		
		if (str.startsWith("WRQ")) {
			udpSend(previous = TFTPPacket.wrq(str, addr, port));
			setState(onwrq());
		}
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				"RRQ <filename> octet\n" +
				"WRQ <filename>"
		);
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				TFTPPacket packet = new TFTPPacket(udpReceive());
				
				// TODO: kirjoita tiedoston loppuun
				
				udpSend(packet.ack());
				
				if (packet.getLength() < TFTPPacket.MAX_SIZE) {
					close(); // last packet received
				}
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
	
	private void udpResend() {
		udpSend(previous);
	}
	
}
