package tftp;

import java.io.*;
import java.net.*;

import fi.jyu.mit.ohj2.Mjonot;
import main.*;
import packet.*;
import thread.*;

/**
 * Huom! Älä erehdy käyttämään DatagramSocket.connect() metodia.
 * Aiheuttaa vain harmia ja UDP muutenkin yhteydetön protokolla.
 * @author Harri Linna
 * @version 9.12.2020
 */
public class TFTPClient extends AThreadDatagramSocket implements IClient {
	
	public static final String PROTOCOL = "tftp";
	
	private final InetSocketAddress server;
	
	private TFTPPacket previous;
	private FileReaderWriter manager;
	private ThreadScheduled schedule;
	
	public TFTPClient(int src_port, InetAddress dst_addr, int dst_port)
			throws SocketException, UnknownHostException {
		super(InetAddress.getByName("localhost"), src_port, TFTPPacket.MAX_SIZE);
		
		server = new InetSocketAddress(dst_addr, dst_port);
		
		// schedule runs tasks at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		
		// start thread
		new Thread(this).start();
		
		String format = "Client using UDP operates on %s:%d";
		Main.onmessage(String.format(
				format, InetAddress.getByName("localhost"), src_port)
		);
		help();
	}
	
	private void onschedule(TFTPPacket packet) {
		// include CRC8 checksum to all the departing packets
		APacketError error = APacketError.convertToCRC8(packet.getDatagramPacket());
		udpSend(error.getDatagramPacket());
		
		schedule.setTask(new Runnable() {
			
			@Override
			public void run() {
				udpSend(error.getDatagramPacket());
			}
		
		});
	}
	
	@Override
	public void onclose() throws IOException {
		schedule.cancelTask();
		schedule.close();
		super.onclose();
	}
	
	@Override
	public void help() {
		Main.onmessage(
				"The following are the TFTP commands:\n" +
				"RRQ </home/user/file> [octet]\n" +
				"WRQ </home/user/file> [octet]"
		);
	}
	
	@Override
	public void send(String str) throws IOException {
		StringBuilder sb = new StringBuilder(str);
		
		// parse arguments for RRQ or WRQ
		String command = Mjonot.erota(sb);
		String filename = Mjonot.erota(sb);
		String mode = Mjonot.erota(sb);
		
		// send RRQ
		if (command.equals("RRQ")) {
			setState(onrrq());
			onschedule(previous = TFTPPacket.make_rrq(
					filename, mode, server
			));
		}
		
		// send WRQ
		if (command.equals("WRQ")) {
			setState(onwrq());
			onschedule(previous = TFTPPacket.make_wrq(
					filename, mode, server
			));
		}
	}

	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				// received ACK
				if (!response.isCorrupted() && packet.isACK(previous)) {
					// read from or write to a file
					manager = new FileReaderWriter(
							previous.getFileName(), TFTPPacket.MAX_DATA
					);
					
					// send ACK
					onschedule(previous = TFTPPacket.make_ack(packet));
				}
			}
			
		};
	}
	
	private IThread onrrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				// received DATA
				if (!response.isCorrupted() && packet.isDATA(previous)) {
					FileReaderWriter.write(manager.getName(), packet.toString());
					onschedule(previous = TFTPPacket.make_ack(packet));
					
					// received last DATA
					if (response.getLength() < TFTPPacket.MAX_SIZE) {
						Main.onmessage("RRQ completed");
						schedule.cancelTask();
						setState(onreceive());
						//close();
					}
				}
			}
			
		};
	}
	
	private IThread onwrq() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				APacketError response = new APacketError(udpReceive());
				TFTPPacket packet = new TFTPPacket(response);
				
				// received ACK
				if (!response.isCorrupted() && packet.isACK(packet)) {
					
					// sent last DATA
					if (!manager.hasNext()) {
						Main.onmessage("WRQ completed");
						schedule.cancelTask();
						setState(onreceive());
						//close();
					}
					
					// send DATA
					if (manager.hasNext()) {
						InetSocketAddress addr = new InetSocketAddress(
								packet.getAddress(), packet.getPort()
						);
						onschedule(previous = TFTPPacket.make_data(
								manager.next(), previous.nextBlock(), addr
						));
					}	
				}
			}
			
		};
	}
	
}
