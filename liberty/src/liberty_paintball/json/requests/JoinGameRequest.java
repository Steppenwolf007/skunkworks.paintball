package liberty_paintball.json.requests;

/**
 * @author Dave Waddling
 *
 */
public class JoinGameRequest {

	private String	username;
	

	public JoinGameRequest() {
		
	}

	public JoinGameRequest(String username) {
		this.username = username;
	}


	public String getUsername() {
		return this.username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

}
