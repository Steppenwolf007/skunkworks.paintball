package liberty_paintball.json.requests;

/**
 * @author Dave Waddling
 *
 */
public class LeaveGameRequest {

	private String	username;
	
	
	public LeaveGameRequest() {
		
	}


	public LeaveGameRequest(String username) {
		this.username = username;
	}


	public String getUsername() {
		return this.username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

}
