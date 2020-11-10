package smtp;

import java.io.*;
import java.net.*;

/**
 * Simple SMTP Client for TIES323
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 2.11.2020
 * @version 5.11.2020, interface for runnable threads
 * @version 9.11.2020, uses abstract class
 */
public class SMTPClient extends AbstractSMTP {
	
	private InetAddress addr;
	private int port;
	private BufferedReader reader;
	
	public SMTPClient(DatagramSocket socket, int size, int port, InetAddress addr) {
		super(socket, size);
		
		this.addr = addr;
		this.port = port;
		
		setState(sendCommand(this));
	}
	
	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			reader = in;
			while (true) {
				getState().run();
			}
		} catch (IOException e) {
			SMTPMain.onerror(e);
		}
	}
	
	public IThread sendCommand(SMTPClient client) {
		return new IThread() {
				
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				udpSend(resp, addr, port);
				
				DatagramPacket packet = udpReceive();
				String data = new String(packet.getData(), 0, packet.getLength());
				
				if (data.startsWith("354")) {
					setState(sendData(client));
				}
				
				SMTPMain.onmessage(data);
			}
		};
	}
	
	public IThread sendData(SMTPClient client) {
		return new IThread() {
			
			@Override
			public void run() throws IOException {
				String resp = reader.readLine();
				udpSend(resp, addr, port);
				
				if (resp.equals(".")) {
					setState(sendCommand(client));
					
					DatagramPacket packet = udpReceive();
					String data = new String(packet.getData(), 0, packet.getLength());
					
					SMTPMain.onmessage(data);
				}
			}
		};
		
	}
	
}
