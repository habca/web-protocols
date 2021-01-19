package http2;

import java.io.*;
import java.net.*;

import main.*;
import thread.*;

/**
 * Simple HTTP/2 Client for TIES323
 * 
 * Test client: curl --http2 <address>
 * TODO: etsi testi palvelin
 * 
 * @author Harri Linna
 * @version 13.12.2020, 1h RFC aloittaminen, serverin ja asiakkaan runko
 */
public class HTTP2Client extends AThreadSocket implements IClient {
	
	public static final String PROTOCOL = "http";
	
	public HTTP2Client(InetAddress addr, int port) throws IOException {
		super(addr, port);		
		new Thread(this).start();
	}

	@Override
	public void send(String input) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		sb.append("GET / HTTP/1.1\n");
		sb.append("Host: " + input + "\n");
		sb.append("Connection: Upgrade, HTTP2-Settings\n");
	    sb.append("Upgrade: h2c\n");
	    sb.append("HTTP2-Settings: <base64url encoding of HTTP/2 SETTINGS payload>\n");
	    //TODO: HTTP2-Settings RFC mukaisesti
	    
		tcpSend(sb.toString());
	}

	@Override
	public void help() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				Main.onmessage(str);
				//close();
			}
			
		};
	}

}
