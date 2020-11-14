package mail;

import java.util.concurrent.*;

/**
 * Email for later expansion
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 *
 */
public class Email {
	
	public static final int MIN = 1000;
	public static final int MAX = 9999;
	
	private int bytes;
	
	public Email() {
		bytes = ThreadLocalRandom.current().nextInt(MIN,MAX+1);
	}
	
	@Override
	public String toString() {
		return String.format("%d", bytes);
	}
	
	public int getBytes() {
		return bytes;
	}
}
