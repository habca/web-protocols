package main;

import java.io.*;
import java.net.*;
import java.util.Objects;

import imap.IMAPClient;
import pop3.*;
import smtp.*;

/**
 * @author Harri Linna
 * @version 14.11.2020
 */
public class Client {
	
	private IClient clientSMTP; // TODO: poista attribuutti
	private IClient clientPOP3; // TODO: poista attribuutti
	private IClient clientIMAP; // TODO: poista attribuutti
	
	private IClient state; // current client
	
	public interface IClient {
		public void send(String input);
	}
	
	public void serviceSMTP(int cport, InetAddress addr, int size, int sport) throws SocketException {
		DatagramSocket csocket = new DatagramSocket(cport, addr);
		SMTPClient client = new SMTPClient(csocket, size, sport, addr);
		new Thread(client).start();
		clientSMTP = client;
	}
		
	public void servicePOP3(InetAddress addr, int tport) throws IOException {
		Socket csocket = new Socket(addr, tport);
		POP3Client client = new POP3Client(csocket);
		new Thread(client).start();
		clientPOP3 = client;
	}
	
	public void serviceIMAP(InetAddress addr, int tport) throws IOException {
		Socket csocket = new Socket(addr, tport);
		IMAPClient client = new IMAPClient(csocket);
		new Thread(client).start();
		clientIMAP = client;
	}
	
	public void send(String input) {	
		if (input.matches(SMTPClient.PROTOCOL)) {
			Main.onmessage(String.format("%s client ready", input));
			state = setClientSMTP();
			return; // do not send
		}
		if (input.matches(POP3Client.PROTOCOL)) {
			state = setClientPOP3();
			Main.onmessage(String.format("%s client ready", input));
			return; // do not send
		}
		if (input.matches(IMAPClient.PROTOCOL)) {
			state = setClientIMAP();
			Main.onmessage(String.format("%s client ready", input));
			return; // do not send
		}
		if (Objects.isNull(state)) {
			Main.onmessage("client was not found");
			return; // do nothing
		}
		state.send(input); // send to client
	}
	
	private IClient setClientSMTP() {
		return new IClient() {

			@Override
			public void send(String str) {
				clientSMTP.send(str);
			}
			
		};
	}
	
	private IClient setClientPOP3() {
		return new IClient() {

			@Override
			public void send(String str) {
				clientPOP3.send(str);
			}
			
		};
	}
	
	private IClient setClientIMAP() {
		return new IClient() {

			@Override
			public void send(String str) {
				clientIMAP.send(str);
			}
			
		};
	}
	
}
