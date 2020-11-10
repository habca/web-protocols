package thread;

import java.io.*;

/**
 * Intended to be used with runnable threads
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 5.11.2020
 */
public interface IThread {

	public void run() throws IOException;
	
}
