package ftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

// TODO: AThreadTCP versio lakkaa toimimasta yhden viestin jälkeen

//public class FTPDataReceiver extends AThreadTCP {
public class FTPClientReceiver extends AThread {
	
	private InetAddress addr;
	private int port;
	
	public static FTPClientReceiver create(InetAddress addr, int port) {
		/*
		try {
			FTPDataReceiver thread = new FTPDataReceiver(addr, port);
			new Thread(thread).start();
			return thread;
		} catch (IOException e) {
			Main.onerror(e);
			return null;
		}
		*/
		
		FTPClientReceiver thread = new FTPClientReceiver(addr, port);
		new Thread(thread).start();
		return thread;
	}
	
	//private FTPDataReceiver(InetAddress addr, int port) throws IOException {
	private FTPClientReceiver(InetAddress addr, int port) {
		//super(addr, port);
		this.addr = addr;
		this.port = port;
		setState(onreceive());
	}
	
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				Socket socket = new Socket(addr, port);
				InputStream in = new DataInputStream(
						new BufferedInputStream(
								socket.getInputStream()
				));
				
				int count;
				byte[] buffer = new byte[4096]; // TODO: ???
				while ((count = in.read(buffer)) > 0)
				{
					Main.onmessage(buffer, 0, count);
				}
				
				socket.close();
			}
			
		};
	}
		
}
