package liberty_paintball.game;

/**
 * 
 * @author Dave Waddling
 * 
 * A player in Liberty Paintball is primarily composed of an identity, location and whether
 * or not they've been eliminated. Score is also tracked but currently unused as there's no
 * end condition to the game.
 *
 */
public class Player {

	private PlayerIdentity	identity;
	private Location		location;
	private int				score;
	private boolean			isEliminated;


	public Player(PlayerIdentity identity) {
		this.identity = identity;
		this.score = 0;
		this.location = null;
		this.isEliminated = false;
	}


	public PlayerIdentity getIdentity() {
		return identity;
	}


	public String getUsername() {
		return identity.getUsername();
	}


	public String getGeneratedName() {
		return identity.getGeneratedName();
	}


	public String getColorAsHexString() {
		return identity.getColorAsHexString();
	}


	public void incrementScore() {
		score++;
	}


	public int getScore() {
		return score;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	public Location getLocation() {
		return location;
	}


	public boolean isEliminated() {
		return isEliminated;
	}


	public void eliminate() {
		isEliminated = true;
	}

}
