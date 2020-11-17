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
	public void run() {
		try {
			while (getContinue()) {
				getState().run();
			}
		} catch (IOException e) {
			Main.onerror(e);
		}
	}
	
	public final void setState(IThread state) {
		runner = state;
	}
	
	public final IThread getState() {
		return runner;
	}
	
	public final void setClose() {
		close = true;
	}
	
	public final boolean getContinue() {
		return !close;
	}
}
