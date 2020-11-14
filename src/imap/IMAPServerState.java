package imap;

public interface IMAPServerState {
	
public String response(String str);
	
	public static final String ERROR = "BAD - command unknown or arguments invalid";
	
	public static IMAPServerState stateLogin(IMAPServer server) {
		return new IMAPServerState() {

			@Override
			public String response(String str) {
				if (str.startsWith("LOGIN")) {
					server.setState(stateGeneral(server));
					return "OK - login completed, now in authenticated state";
				}
				return ERROR;
			}
			
		};
	}
	
	public static IMAPServerState stateGeneral(IMAPServer server) {
		return new IMAPServerState() {

			@Override
			public String response(String str) {
				if (str.startsWith("LIST")) {
					//server.setState(stateLogin(server));
					return "OK - list completed";
				}
				if (str.startsWith("LOGOUT")) {
					server.setState(stateLogin(server));
					return "OK - logout completed";
				}
				return ERROR;
			}
			
		};
	}
	
}
