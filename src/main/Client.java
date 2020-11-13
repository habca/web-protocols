package main;

import java.io.*;
import java.net.*;
import java.util.*;

import pop3.*;
import smtp.*;
import thread.*;

/**
 * TODO: MAIN:   Pääohjelma vain parsii komentorivi-parametrit ja käynnistelee ohjelman tarvitsemat säikeet.
 * TODO: USER:   Tee Reader-luokka joka lukee käyttäjältä (user) syötettä (käyttöliittymä) omassa säikeessään.
 * TODO: CLIENT: Lähetä käyttäjän (user) syötteet asiakkaille (client), jotka kuuntelevat säikeessä vastausta palvelimelta (server),
 *               jolloin ei tarvitse jäädä odottamaan vastausta palvelimelta. (Tarvitseeko asiakas tilaa?)
 *               + Ei tarvitse koska palvelimen vastauksia ei jäädä odottamaan!
 *               + Toisaalta asiakkaat eroavat vain sillä mihin porttiin viestejä lähetetään.
 *               + Asiakkaat toki vastaanottavat (ja käsittelevät) viestejä
 * TODO: SERVER: Palvelimia ei tarvitse muuttaa, vastailevat vain saapuviin viesteihin ja käsittelevät inboxia
 *
 * TODO: Pitäisi perustua ajatukseen ettei vastaanotettuja viestejä jäädä odottamaan (user <-> client <-> server)
 * TODO: Muutosten jälkeen käyttäjä (user) voi vaihdella asiakkaiden (clients) välillä, koska asiakkaat eivät odota käyttäjältä syötettä.
 * 
 * @author Harri Linna
 * @version 14.11.2020
 */
public class Client {
	
	private Main main;
	private BufferedReader reader;
	private AThread current;
	private DatagramSocket csocket; // to recycle
	private Socket tsocket;
	
	// TODO: poista main
	public Client(Main main, BufferedReader reader) {
		this.main = main;
		this.reader = reader;
		
		try {
			this.csocket = new DatagramSocket(main.cport, main.addr);
			tsocket = new Socket(main.addr, main.tport);
		} catch (SocketException e) {
			Main.onerror(e);
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public void closeCurrent() {
		if (Objects.nonNull(current)) {
			current.setClose();
		}
	}
	
	public void clientSMTP() throws SocketException {
		closeCurrent();
		//DatagramSocket csocket = new DatagramSocket(main.cport, main.addr);
		AThread client = new SMTPClient(csocket, main.size, main.sport, main.addr, this);
		new Thread(client).start();
		current = client;
	}
	
	
	public void clientPOP3() throws IOException {
		closeCurrent();
		//Socket csocket = new Socket(main.addr, main.tport);
		AThread client = new POP3Client(tsocket, this);
		new Thread(client).start();
		current = client;
	}

	public String readLine() throws IOException {
		String str = reader.readLine();
		
		if (str.matches(SMTPClient.PROTOCOL)) {
			clientSMTP();
			return null;
		}
		if (str.matches(POP3Client.PROTOCOL)) {
			clientPOP3();
			return null;
		}
		
		return str;
	}
}
