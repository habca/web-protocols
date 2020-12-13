package http2;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple HTTP/2 Client for TIES323
 * @author Harri Linna
 * @version 13.12.2020
 */
public class HTTP2Client extends AThreadSocket implements IClient {
	
	public static final String PROTOCOL = "http";

	public HTTP2Client(InetAddress addr, int port) throws IOException {
		super(addr, port);
		new Thread(this).start();
	}

	@Override
	public void send(String input) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void help() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IThread onreceive() {
		// TODO Auto-generated method stub
		return null;
	}

}
