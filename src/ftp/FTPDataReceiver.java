package ftp;

import java.io.*;
import java.net.*;

import main.Main;
import thread.*;

public class FTPDataReceiver extends AThread {
	
	public FTPDataReceiver(InetAddress addr, int port) {
		
		setState(new IThread() {

			@Override
			public void run() throws IOException {
				dataTcp(addr, port);
			}
			
		});
	}
	
	public void dataTcp(InetAddress addr, int port) throws IOException {
		Socket data_socket = new Socket(addr, port);
		InputStream in = new DataInputStream(
				new BufferedInputStream(
						data_socket.getInputStream()
		));
		
		int count;
		byte[] buffer = new byte[4096]; // TODO: ???
		while ((count = in.read(buffer)) > 0)
		{
			Main.onmessage(buffer, 0, count);
		}
		
		data_socket.close();
	}
	
}
