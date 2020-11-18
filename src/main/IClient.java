package main;

public interface IClient {

	public void send(String input);
	public void help();
	public void close();
	public boolean isClosed();
	
}
