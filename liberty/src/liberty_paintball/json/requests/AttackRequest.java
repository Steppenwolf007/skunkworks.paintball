package liberty_paintball.json.requests;

/**
 * @author Dave Waddling
 *
 */
public class AttackRequest extends GameActionRequest {

	public AttackRequest() {
		
	}

	public AttackRequest(String username, double latitude, double longitude) {
		super(username, latitude, longitude);
	}

}
