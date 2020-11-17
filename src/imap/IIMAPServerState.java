package imap;

public interface IIMAPServerState {
	
public String response(String str);
	
	public static final String ERROR = "BAD - command unknown or arguments invalid";
	
	public static IIMAPServerState stateLogin(IMAPServer server) {
		return new IIMAPServerState() {

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
	
	public static IIMAPServerState stateGeneral(IMAPServer server) {
		return new IIMAPServerState() {

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
