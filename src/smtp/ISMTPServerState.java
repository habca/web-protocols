package smtp;

/**
 * States for the SMTPServer
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 4.11.2020
 * @version 5.11.2020
 */
public interface ISMTPServerState {
	
	public String response(String str);	 
	
	public static ISMTPServerState stateInitial(SMTPServerReceiver server) {
		return new ISMTPServerState() {

			@Override
			public String response(String str) {
				if (str.matches("^CONNECTION ESTABLISHMENT")) {
					server.setState(stateGeneral(server));
					return server.getStatus("220");
				}
				return server.getStatus("421");
			}
			
		};
	}
	
	public static ISMTPServerState stateGeneral(SMTPServerReceiver server) {
		return new ISMTPServerState() {

			@Override
			public String response(String str) {		
				if (str.matches("^MAIL")) {
					server.processCommand(str, "^MAIL");
					return server.getStatus("250");
				}
				if (str.matches("^RCPT")) {
					server.processCommand(str, "^RCPT");
					return server.getStatus("250");
				}
				if (str.matches("^DATA")) {
					server.setState(stateData(server));
					return server.getStatus("354");
				}
				if (str.matches("^QUIT")) {
					server.setState(stateInitial(server));
					return server.getStatus("221");
				}
				if (str.matches("RSET")) {
					return server.getStatus("250");
				}
				if (str.matches("^HELO|NOOP")) {
					return server.getStatus("250");
				}
				return server.getStatus("421");
			}
			
		};
	}
	
	public static ISMTPServerState stateData(SMTPServerReceiver server) {
		return new ISMTPServerState() {

			@Override
			public String response(String str) {
				if (str.equals(".")) {
					server.setState(stateGeneral(server));
					return server.getStatus("250");
				}
				return server.getStatus("421");
			}
			
		};
	}
	
}
