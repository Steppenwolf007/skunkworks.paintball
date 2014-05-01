package liberty_paintball.game.events;

import static liberty_paintball.game.events.Event.EventType.PLAYER_MISS;
import liberty_paintball.game.Location;
import liberty_paintball.game.Player;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 *
 */
public class PlayerMissEvent extends Event {
	
	private static final String MISS_MESSAGE = " missed.";

	private Player		attacker;
	private Location	location;


	public PlayerMissEvent(Player attacker) {
		super(PLAYER_MISS);
		this.attacker = attacker;
		// Store the location at the time the event occurred.
		this.location = attacker.getLocation().clone();
	}


	public Player getAttacker() {
		return attacker;
	}


	public Location getLocation() {
		return location;
	}


	@Override
	public MapPoint toMapPoint() {
		return new MapPoint(type, timeOccurred, location, attacker.getIdentity(), null, MISS_MESSAGE);
	}

}
