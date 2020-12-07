package thread;

import java.io.*;

import main.*;

/**
 * Changes behaviour of a thread dynamically
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 9.11.2020
 * @version 10.11.2020, removed udp socket
 */
public abstract class AThread implements Runnable {

	private boolean close;
	private IThread runner;
	
	@Override
	public final void run() {
		setState(onreceive());
		try {
			while (!isClosed()) {
				getState().run();
			}
			onclose();
		} catch (IOException e) {
			Main.onerror(e);
		}
		
	}
	
	public abstract IThread onreceive();
	
	public abstract void onclose() throws IOException;
	
	public final void setState(IThread state) {
		runner = state;
	}
	
	public final IThread getState() {
		return runner;
	}
	
	public final void close() {
		close = true;
	}
	
	public final boolean isClosed() {
		return close;
	}
	
	public final void sleep(int milliseconds) {
		try {
			Thread.sleep((long) milliseconds);
		} catch (InterruptedException e) {
			Main.onerror(e);
		}
	}
}
