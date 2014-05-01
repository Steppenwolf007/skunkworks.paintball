package liberty_paintball.game.events;

import static liberty_paintball.game.events.Event.EventType.PLAYER_HIT;
import liberty_paintball.game.Location;
import liberty_paintball.game.Player;
import liberty_paintball.game.PlayerIdentity;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 *
 */
public class PlayerHitEvent extends Event {

	private static final String	HIT_MESSAGE	= " hit: ";

	private Player				attacker;
	private Player				victim;
	private Location			location;


	public PlayerHitEvent(Player attacker, Player victim) {
		super(PLAYER_HIT);
		this.attacker = attacker;
		this.victim = victim;
		// Store the location at the time the event occurred.
		this.location = attacker.getLocation().clone();
	}


	public Player getAttackerUsername() {
		return attacker;
	}


	public Player getVictim() {
		return victim;
	}


	public Location getLocation() {
		return location;
	}


	@Override
	public MapPoint toMapPoint() {
		return new MapPoint(type, timeOccurred, location, attacker.getIdentity(), new PlayerIdentity[] { victim.getIdentity() }, HIT_MESSAGE);
	}

}
