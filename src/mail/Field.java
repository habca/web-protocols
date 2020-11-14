package mail;

public class Field {
	
	private String key;
	private String value;
	
	public Field(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", key, value);
	}
	
}
