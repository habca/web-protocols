package tftp;

import thread.*;
import java.io.*;
import java.net.*;

import main.Main;

public class TFTPServer extends AThreadDatagramSocket {

	private DatagramPacket previous;
	
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
				
				Main.onmessage("Server: request arrived");
				
				if (packet.getOpcode() == 1) {
					// TODO: tarkista voiko lukea
					Main.onmessage("Server: response sent");
					
					udpSend(previous = packet.ack());
					setState(onrrq()); // opcode was rrq
				}
				
				if (packet.getOpcode() == 2) {
					// TODO: tarkista voiko kirjoittaa
					Main.onmessage("Server: response sent");
					
					udpSend(previous = packet.ack());
					setState(onwrq()); // opcode was wrq
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				// TODO Auto-generated method stub
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
