package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import main.*;
import thread.*;

public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private static final int SIZE = 512; // RFC 1350
	
	private InetAddress addr;
	private int port;
	
	public TFTPClient(InetAddress src_addr, InetAddress dst_addr,
			int src_port, int dst_port) throws SocketException {
		super(src_addr, src_port, SIZE);
		this.addr = dst_addr;
		this.port = dst_port;
		new Thread(this).start();
		
		String format = "Client using UDP operates on %s:%d";
		Main.onmessage(String.format(format, src_addr.getHostAddress(), src_port));
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				byte[] arr = udpReceive();
				Main.onmessage(new String(arr));
				// Tässä pitäisi tehdä mitä?
			}
			
		};
	}

	@Override
	public void send(String str) throws IOException {
		if (str.startsWith("RRQ")) {
			setState(onrrq());
			udpSend(rrq(str), addr, port);
		}
		
		if (str.startsWith("WRQ")) {
			setState(onwrq());
			udpSend(wrq(str), addr, port);
		}
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				//"RRQ <filename> <mode>\n" +
				"RRQ <filename> octet\n" +
				"WRQ <filename>"
		);
	}
	
	public static byte[] rrq(String cmd) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder(cmd);
		
		String str = Mjonot.erota(sb); // RRQ
		out.write((byte) 0); // opcode
		out.write((byte) 1); // opcode
		
		str = Mjonot.erota(sb); // filename
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		str = Mjonot.erota(sb); // mode
		out.write(str.getBytes());
		out.write((byte) 0); // delimiter
		
		return out.toByteArray();
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				byte[] arr = udpReceive();
				Main.onmessage(new String(arr));
				udpSend(ack(), addr, port);
				
				// last packet received
				if (arr.length < SIZE) {
					close();
				}
			}
			
		};
	}
	
	private static byte[] ack() {
		return null; // TODO: muodosta ack paketti
	}

	private static byte[] wrq(String cmd) {
		return null; // TODO: muodosta wrq paketti
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				// TODO: lähetä tiedosto
			}
			
		};
	}
	
}
