package http2;

import java.io.*;

import thread.*;

/**
 * Simple HTTP/2 Server for TIES323
 * @author Harri Linna
 * @version 13.12.2020
 */
public class HTTP2Server extends AThreadServerSocket {

	public HTTP2Server(int port) throws IOException {
		super(port);
		new Thread(this).start();
	}

	@Override
	public IThread onreceive() {
		// TODO Auto-generated method stub
		return null;
	}

}
