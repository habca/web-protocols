package ftp;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

public class FTPClientReceiver extends AThreadTCP {
	
	public FTPClientReceiver(InetAddress addr, int port) throws IOException {
		super(addr, port);
		
		new Thread(this).start();
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				InputStream in = new DataInputStream(
						new BufferedInputStream(
								getInputStream()
				));
				
				int count;
				byte[] buffer = new byte[4096]; // TODO: ???
				while ((count = in.read(buffer)) > 0)
				{
					Main.onmessage(buffer, 0, count);
				}
				
				close();
			}
			
		};
	}
		
}
