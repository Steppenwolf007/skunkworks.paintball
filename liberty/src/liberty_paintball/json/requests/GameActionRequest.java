package liberty_paintball.json.requests;

/**
 * @author Dave Waddling
 *
 */
public abstract class GameActionRequest {

	private String	username;
	private double	latitude;
	private double	longitude;


	public GameActionRequest() {
		
	}
	
	public GameActionRequest(String username, double latitude, double longitude) {
		this.username = username;
		this.latitude = latitude;
		this.longitude = longitude;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

}
