package liberty_paintball.game.events;

import static liberty_paintball.game.events.Event.EventType.PLAYER_OUTGUNNED;

import java.util.List;

import liberty_paintball.game.Location;
import liberty_paintball.game.Player;
import liberty_paintball.game.PlayerIdentity;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 *
 */
public class PlayerOutgunnedEvent extends Event {
	
	private static final String OUTGUNNED_MESSAGE = " was outgunned by: ";

	private Player			attacker;
	private List<Player>	outgunners;
	private Location		location;


	public PlayerOutgunnedEvent(Player attacker, List<Player> outgunners) {
		super(PLAYER_OUTGUNNED);
		this.attacker = attacker;
		this.outgunners = outgunners;
		// Store the location at the time the event occurred.
		this.location = attacker.getLocation().clone();
	}


	public Player getAttacker() {
		return attacker;
	}


	public List<Player> getOutgunners() {
		return outgunners;
	}


	public Location getLocation() {
		return location;
	}


	@Override
	public MapPoint toMapPoint() {
		PlayerIdentity[] outgunnerIdentities = new PlayerIdentity[outgunners.size()];
		for (int i = 0; i < outgunners.size(); i++) {
			outgunnerIdentities[i] = outgunners.get(i).getIdentity();
		}
		return new MapPoint(type, timeOccurred, location, attacker.getIdentity(), outgunnerIdentities, OUTGUNNED_MESSAGE);
	}

}
