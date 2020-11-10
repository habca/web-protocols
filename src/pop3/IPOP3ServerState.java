package pop3;

/**
 * States for the POP3Server
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 10.11.2020
 */
public interface IPOP3ServerState {
	
	public String response(String str);
	
	public static final String ERROR = "-ERR command not understood";
	
	public static IPOP3ServerState stateLogin(POP3Server server) {
		return new IPOP3ServerState() {

			@Override
			public String response(String str) {
				if (str.startsWith("USER")) {
					server.setState(stateAuthorize(server));
					return "+OK send PASS";
				}
				return ERROR;
			}
			
		};
	}
	
	public static IPOP3ServerState stateAuthorize(POP3Server server) {
		return new IPOP3ServerState() {

			@Override
			public String response(String str) {
				server.setState(stateGeneral(server));
				if (str.startsWith("PASS")) {
					return "+OK welcome";
				}
				return ERROR;
			}
			
		};
	}
	
	public static IPOP3ServerState stateGeneral(POP3Server server) {
		return new IPOP3ServerState() {

			@Override
			public String response(String str) {
				if (str.matches("^QUIT")) {
					server.setState(stateLogin(server));
					return "+OK farewell";
				}
				if (str.matches("^LIST")) {
					String format = "+OK %d messages (%d bytes)";
					int total = server.getInbox().size();
					int bytes = server.getInbox().getBytes();
					
					server.setState(stateGeneral(server));
					return String.format(format, total, bytes);
				}
				return ERROR;
			}
			
		};
	}
	
}
