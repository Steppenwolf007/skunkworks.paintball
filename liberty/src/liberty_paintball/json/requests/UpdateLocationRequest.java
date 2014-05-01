package liberty_paintball.json.requests;

/**
 * @author Dave Waddling
 *
 */
public class UpdateLocationRequest extends GameActionRequest {
		
	public UpdateLocationRequest() {
		super (null,0,0);
	}

	public UpdateLocationRequest(String username, double latitude, double longitude) {
		super(username, latitude, longitude);
	}

}
