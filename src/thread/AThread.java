package thread;

/**
 * Changes behaviour of a thread dynamically
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 9.11.2020
 * @version 10.11.2020, removed udp socket
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
