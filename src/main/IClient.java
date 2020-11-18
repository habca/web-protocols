package main;

import java.io.*;

public interface IClient {

	public void send(String input) throws IOException;
	public void help();
	public void close();
	public boolean isClosed();
	
}
