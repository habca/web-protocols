package pop3;

public interface IPOP3State {
	
	public String response(String str);
	
	public static IPOP3State stateLogin(POP3Server server) {
		return new IPOP3State() {

			@Override
			public String response(String str) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
}
