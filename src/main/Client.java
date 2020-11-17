package main;

import java.io.*;
import java.net.*;
import java.util.*;

import ftp.*;
import imap.*;
import pop3.*;
import smtp.*;

/**
 * @author Harri Linna
 * @version 14.11.2020
 * @version 17.11.2020, Attribuutit listaan
 */
public class Client implements IClient {
	
	private List<IClient> clients;
	private IClient state; // current
	
	public Client() {
		clients = new ArrayList<IClient>();
	}
	
	//public void serviceSMTP(int cport, InetAddress addr, int size, int sport) throws SocketException {
	public void serviceSMTP(InetAddress addr, int tport) throws IOException {
		//DatagramSocket csocket = new DatagramSocket(cport, addr);
		//SMTPClient client = new SMTPClient(csocket, size, sport, addr);
		Socket socket = new Socket(addr, tport);
		SMTPClient client = new SMTPClient(socket);
		new Thread(client).start();
		clients.add(client);
	}
		
	public void servicePOP3(InetAddress addr, int tport) throws IOException {
		Socket csocket = new Socket(addr, tport);
		POP3Client client = new POP3Client(csocket);
		new Thread(client).start();
		clients.add(client);
	}
	
	public void serviceIMAP(InetAddress addr, int tport) throws IOException {
		Socket csocket = new Socket(addr, tport);
		IMAPClient client = new IMAPClient(csocket);
		new Thread(client).start();
		clients.add(client);
	}
	
	public void serviceFTP(InetAddress addr, int port) throws IOException {
		Socket socket = new Socket(addr, port);
		FTPClient client = new FTPClient(socket);
		new Thread(client).start();
		clients.add(client);
	}
	
	@Override
	public void send(String input) {
		Iterator<IClient> iterator = clients.iterator();
		while (iterator.hasNext()) {
			IClient client = iterator.next();
			if (input.matches(client.protocol())) {
				state = client;
				state.help();
				return; // do not send
			}
		}
		if (Objects.nonNull(state) && input.equals("help")) {
			state.help();
			return; // do not send
		}
		if (Objects.isNull(state) && input.equals("help")) {
			help();
			return; // do not send
		}
		if (Objects.isNull(state)) {
			Main.onmessage("client was not found");
			return; // do not send
		}
		state.send(input); // send to client
	}

	@Override
	public void help() {
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

	@Override
	public String protocol() {
		Main.onerror(new Exception("Yläluokkaa ei kuuluisi kutsua!"));
		return null;
	}
	
}
