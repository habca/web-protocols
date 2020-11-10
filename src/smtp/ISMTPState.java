package smtp;

/**
 * States for the SMTPServer
 * 
 * @author Harri Linna
 * @author Ville Paju
 * @version 4.11.2020
 * @version 5.11.2020
 */
public interface ISMTPState {
	
	public String response(String str);	 
	
	public static ISMTPState stateInitial(SMTPServer server) {
		return new ISMTPState() {

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
	
	public static ISMTPState stateGeneral(SMTPServer server) {
		return new ISMTPState() {

			@Override
			public String response(String str) {				
				if (str.matches("^HELO|MAIL|RCPT")) {
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
				return server.getStatus("421");
			}
			
		};
	}
	
	public static ISMTPState stateData(SMTPServer server) {
		return new ISMTPState() {

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
