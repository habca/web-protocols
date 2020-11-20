package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import main.*;
import thread.*;

public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private static final int size = 512;
	
	private InetAddress addr;
	private int port;
	
	public TFTPClient(InetAddress src_addr, InetAddress dst_addr,
			int src_port, int dst_port) throws SocketException {
		super(src_addr, src_port, size);
		this.addr = dst_addr;
		this.port = dst_port;
		new Thread(this).start();
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = udpReceive();
				Main.onmessage(str);
			}
			
		};
		
	}

	@Override
	public void send(String str) throws IOException {
		if (str.startsWith("RRQ")) {
			udpSend(rrq(str), addr, port);
		}
		//udpSend(rrq(str), addr, port);
	}

	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				"RRQ <filename> <mode>\n" +
				"WRQ <filename>\n"
		);
	}
	
	// TODO: jatka tästä
	public static byte[] rrq(String cmd) {
		StringBuilder sb = new StringBuilder(cmd);
		
		Mjonot.erota(sb);
		String file = Mjonot.erota(sb);
		String mode = Mjonot.erota(sb);
		
		byte[] byte_file = file.getBytes();
		byte[] byte_mode = mode.getBytes();
		
		int size = byte_file.length;
		size += byte_mode.length;
		size += 4;
		
		byte[] arr = new byte[size];
		int count = 0;
		
		arr[count++] = (byte) 0;
		arr[count++] = (byte) 1;
		
		
		for (int i = 0; i < byte_file.length; i++) {
			arr[count++] = byte_file[i];
		}
		
		arr[count++] = (byte) 0;
		
		for (int j = 0; j < byte_mode.length; j++) {
			arr[count++] = byte_mode[j];
		}
		
		arr[count++] = (byte) 0;
		
		return arr;
		
		/*
		Mjonot.erota(sb);
		try {
			String str;
			out.write((byte) 0);
			out.write((byte) 1);
			
			str = Mjonot.erota(sb);
			out.write(str.getBytes());
			out.write((byte) 0);
			
			str = Mjonot.erota(sb);
			out.write(str.getBytes());
			out.write((byte) 0);
			
			return out.toString();
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
		*/
	}

}
