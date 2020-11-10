package thread;

import java.io.*;
import java.net.*;

/**
 * Similar attributes and methods for SMTP
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 9.11.2020
 */
public abstract class AThread implements Runnable {

	private IThread runner;
	
	@Override
	public abstract void run();

	public final void setState(IThread state) {
		runner = state;
	}
	
	public final IThread getState() {
		return runner;
	}
	
}
