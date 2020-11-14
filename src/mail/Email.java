package mail;

import java.util.*;
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
	
	private List<Field> fields;
	
	public Email() {
		bytes = ThreadLocalRandom.current().nextInt(MIN,MAX+1);
		fields = new ArrayList<Field>();
	}
	
	public void setField(String key, String value) {
		fields.add(new Field(key, value));
	}
	
	@Override
	public String toString() {
		//return String.format("%d", bytes);
		StringBuilder sb = new StringBuilder();
		Iterator<Field> iterator = fields.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next().toString());
		}
		return sb.toString();
	}
	
	public int getBytes() {
		return bytes;
	}
}
