package tftp;

import thread.*;
import java.io.*;
import java.net.*;

import main.*;
import packet.*;

/**
 * Huom! Älä erehdy käyttämään DatagramSocket.connect() metodia.
 * Aiheuttaa vain harmia ja UDP muutenkin yhteydetön protokolla.
 * @author Harri Linna
 * @version 9.12.2020
 */
public class TFTPServerReceiver extends AThreadDatagramSocket {

	private TFTPPacket previous;
	private FileReaderWriter manager;
	private ThreadScheduled schedule;
	
	private DatagramPacket request;
	
	public TFTPServerReceiver(DatagramSocketError socket, DatagramPacket packet) {
		super(socket, TFTPPacket.MAX_SIZE);
		
		// schedule runs tasks at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		request = packet;
		
		// start the thread
		new Thread(this).start();
	}
	
	/* 
	public TFTPServerReceiver(int src_port, DatagramPacket packet) throws SocketException, UnknownHostException {
		super(InetAddress.getByName("localhost"), src_port, TFTPPacket.MAX_SIZE);
		
		// schedule runs tasks at fixed rate
		schedule = new ThreadScheduled(1, 2000);
		request = packet;
		
		// start the thread
		new Thread(this).start();
	}
	*/
	
	private void onschedule(TFTPPacket packet) {
		// include CRC8 checksum to all departing packets
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
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				//APacketError response = new APacketError(udpReceive());
				APacketError response = new APacketError(request);
				TFTPPacket packet = new TFTPPacket(response);
				
				if (!response.isCorrupted()) {
					// read from or write to a file
					manager = new FileReaderWriter(
							packet.getFileName(), TFTPPacket.MAX_DATA
					);
					
					// received RRQ
					if (packet.isRRQ()) {
						setState(onrrq());
					}
					
					// received WRQ
					if (packet.isWRQ()) {
						setState(onwrq());
					}
					
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
				
				// received ACK
				if (!response.isCorrupted() && packet.isACK(previous)) {
					
					// sent last DATA
					if (!manager.hasNext()) {
						//schedule.cancelTask();
						//setState(onreceive());
						close();
					}
					
					// send DATA
					if (manager.hasNext()) {
						InetSocketAddress addr = new InetSocketAddress(
								packet.getAddress(), packet.getPort()
						);
						onschedule(previous = TFTPPacket.make_data(
								manager.next(), packet.nextBlock(), addr
						));
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
				
				// received DATA
				if (!response.isCorrupted() && packet.isDATA(previous)) {
					FileReaderWriter.write(manager.getName(), packet.toString());
					onschedule(previous = TFTPPacket.make_ack(packet));
					
					// received last DATA
					if (response.getLength() < TFTPPacket.MAX_SIZE) {
						//schedule.cancelTask();
						//setState(onreceive());
						close();
					}
				}
			}
			
		};
	}

}
