package ftp;

import static org.junit.Assert.*;

import java.io.*;
import java.net.*;

import org.junit.*;

import main.*;
import thread.*;

public class FTPClient extends AThreadSocket implements IClient {
	
	public static final String PROTOCOL = "ftp";
	
	private InetAddress addr;
	private int port;
	
	public FTPClient(InetAddress addr, int port) throws IOException {
		super(addr, port);
		new Thread(this).start();
	}
	
	@Override
	public IThread onreceive() {
		return new IThread() {

			@Override
			public void run() throws IOException {
				String str = tcpReceive();
				
				Main.onmessage(str);
				
				if (str.startsWith("221")) { // QUIT
					close();
				}
				if (str.startsWith("227")) { // PASV
					String str_addr = extractAddress(str);
					addr = InetAddress.getByName(str_addr);
					port = extractPort(str);
				}
			}
		
		};
	}
	
	@Override
	public void send(String str) throws IOException {
		if (str.startsWith("LIST") || 
				str.startsWith("RETR")) {
			new FTPClientReceiver(addr, port);
		}
		tcpSend(str);
	}
	
	@Override
	public void help() {
		Main.onmessage(
				"The following are the FTP commands:\n" +
				"USER <username>\n" +
				"PASS <password>\n" +
				"PASV\n" +
				"LIST [<pathname>]\n" +
				"RETR <pathname> \n" +
				"QUIT"
		);
	}
	
	public static String extractAddress(String str) {
		StringBuilder sb = new StringBuilder();
		Integer[] arr = Static.extractNumbers(str);
		
		Static.append(sb, arr[1]);
		Static.append(sb, '.', arr[2]);
		Static.append(sb, '.', arr[3]);
		Static.append(sb, '.', arr[4]);
		
		return sb.toString();
	}
	
	public static int extractPort(String str) {
		Integer[] arr = Static.extractNumbers(str);
		int last = arr.length - 1;
		return calcPort(arr[last-1], arr[last]);
	}

    public static int calcPort(int p1, int p2) {
        return (p1 * 256) + p2;
    }
    
    public static String extractFile(String prefix, String str) {
    	String[] arr = str.split(prefix);
    	StringBuilder sb = new StringBuilder();
    	for (int i = 1; i < arr.length; i++) {
    		sb.append(arr[i].trim());
    	}
		return sb.toString();
    }
    
    // JUNIT TESTS
    
    public static class TestFTPClient {
    	private static final  String PASV = 
    			"227 Entering Passive Mode (193,166,3,2,155,200)";
    	@Test
    	public void testCalcPort() {
    		String error = "Portti lasketaan väärin:";
    		
    		assertEquals(error, 0, calcPort(0,0));
    		assertEquals(error, 1, calcPort(0,1));
    		assertEquals(error, 256, calcPort(1,0));
    		assertEquals(error, 257, calcPort(1,1));
    	}
    	@Test
    	public void testExtractNumber() {
    		String error = "Numerot parsitaan väärin:";
    		Integer[] result = new Integer[] {227,193,166,3,2,155,200};
    		Integer[] test = Static.extractNumbers(PASV);
    		assertEquals(error, true, Static.compare(test, result));
    	}
    	@Test
    	public void testExtractPort() {
    		String error = "Portti parsitaan väärin:";
    		assertEquals(error, calcPort(155,200), extractPort(PASV));
    	}
    	@Test
    	public void testExtractAddress() {
    		String error = "IP-osoite parsitaan väärin:";
    		assertEquals(error, "193.166.3.2", extractAddress(PASV));
    	}
    	@Test
    	public void testExtractFile() {
    		String error = "Tiedostonimi parsitaan väärin:";
    		assertEquals(error, "file", extractFile("RETR", "RETR file"));
    		assertEquals(error, "", extractFile("RETR", "RETR"));
    	}
    }
	
}
