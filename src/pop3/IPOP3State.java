package pop3;

public interface IPOP3State {
	
	public String response(String str);
	
	public static final String ERROR = "-ERR command not understood";
	
	public static IPOP3State stateLogin(POP3Server server) {
		return new IPOP3State() {

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
	
	public static IPOP3State stateAuthorize(POP3Server server) {
		return new IPOP3State() {

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
	
	public static IPOP3State stateGeneral(POP3Server server) {
		return new IPOP3State() {

			@Override
			public String response(String str) {
				if (str.matches("^QUIT")) {
					server.setState(stateLogin(server));
					return "+OK farewell";
				}
				if (str.matches("^LIST")) {
					return "+OK TODO: tulosta inboxin sisältö";
				}
				return ERROR;
			}
			
		};
	}
	
}
