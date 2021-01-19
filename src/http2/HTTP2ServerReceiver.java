package http2;

import java.io.*;
import java.net.*;

import thread.*;

/**
 * Simple HTTP/2 Server for TIES323
 * @author Harri Linna
 * @version 13.12.2020, 1h RFC aloittaminen, serverin ja asiakkaan runko
 */
public class HTTP2ServerReceiver extends AThreadSocket {

	public HTTP2ServerReceiver(Socket socket) throws IOException {
		super(socket);
		new Thread(this).start();
	}

	@Override
	public IThread onreceive() {
		// TODO Auto-generated method stub
		return null;
	}

}
