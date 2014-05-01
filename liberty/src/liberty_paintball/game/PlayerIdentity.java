package liberty_paintball.game;

/**
 * @author Dave Waddling
 * 
 * A player's identity comprises of their Twitter username and their generated in-game name
 * and its corresponding color.
 *
 */
public class PlayerIdentity {

	private String	username;
	private String	generatedName;
	private String	colorAsHexString;

	public PlayerIdentity() {

	}


	public PlayerIdentity(String username, String generatedName, String colorAsHexString) {
		this.username = username;
		this.generatedName = generatedName;
		this.colorAsHexString = colorAsHexString;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getGeneratedName() {
		return generatedName;
	}


	public void setGeneratedName(String generatedName) {
		this.generatedName = generatedName;
	}


	public String getColorAsHexString() {
		return colorAsHexString;
	}


	public void setColorAsHexString(String colorAsHexString) {
		this.colorAsHexString = colorAsHexString;
	}

}