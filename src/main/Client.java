package main;

import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import ftp.*;
import imap.*;
import pop3.*;
import smtp.*;

/**
 * @author Harri Linna
 * @version 14.11.2020
 * @version 17.11.2020, Attribuutit listaan
 * @version 18.11.2020, Yksi clientti kerrallaan auki
 */
public class Client implements IClient {
	
	private IClient client;
	
	@Override
	public void send(String input) {
		if (input.equals("help")) {
			help();
			return;
		}
		
		if (parseClient(input)) {
			help();
			return;
		}
		
		if (client == null || isClosed()) {
			Main.onmessage("client was not found");
			return; // do not send
		}
		
		client.send(input); // send to client
	}

	@Override
	public void help() {
		if (client != null) {
			client.help();
		} else {
			Main.onmessage(
					"The following are the terminal commands:\n\n" +
					"smtp <SP> <host> <SP> <port> <CRLF>\n" +
					"pop3 <SP> <host> <SP> <port> <CRLF>\n" +
					"imap <SP> <host> <SP> <port> <CRLF>\n" +
					"ftp <SP> <host> <SP> <port> <CRLF>\n" +
					"help <CRLF>\n" +
					"quit <CRLF>"
			);
		}
	}
	
	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}
	
	private boolean parseClient(String str) {
		String protocol;
		InetAddress addr;
		int port;
		
		try {
			StringBuilder sb = new StringBuilder(str);
			protocol = Mjonot.erota(sb);
			addr = InetAddress.getByName(Mjonot.erota(sb));
			port = Integer.parseInt(Mjonot.erota(sb));
		} catch (Exception e) {
			return false;
		}
		
		// TODO: keksi parempi kuin if-lauseet
		if (protocol.equals(SMTPClient.PROTOCOL)) {
			close();
			client = SMTPClient.create(addr, port);
			return true;
		}
		
		if (protocol.equals(POP3Client.PROTOCOL)) {
			close();
			client = POP3Client.create(addr, port);
			return true;
		}
		
		if (protocol.equals(IMAPClient.PROTOCOL)) {
			close();
			client = IMAPClient.create(addr, port);
			return true;
		}
		
		if (protocol.equals(FTPClient.PROTOCOL)) {
			close();
			client = FTPClient.create(addr, port);

			return true;
		}
		
		return false;
	}

	@Override
	public boolean isClosed() {
		if (client != null) {
			return client.isClosed();
		}
		
		return false;
	}
	
}
