package mail;

import java.util.*;

/**
 * List of Emails
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public class Inbox extends ArrayList<Email> {
	
	private static final long serialVersionUID = 1L;

	public Inbox() {
		add(new Email());
		add(new Email());
		add(new Email());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		
		Iterator<Email> it = iterator();
		while (it.hasNext()) {
			String email = it.next().toString();
			sb.append(String.format("%d %s\n", ++counter, email));
		}
		
		return sb.toString();
	}
	
	public int getBytes() {
		int bytes = 0;
		
		Iterator<Email> it = iterator();
		while (it.hasNext()) {
			bytes += it.next().getBytes();
		}
		
		return bytes;
	}
	
}
