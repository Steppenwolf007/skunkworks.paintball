package liberty_paintball.mapping;

import liberty_paintball.game.Location;
import liberty_paintball.game.PlayerIdentity;
import liberty_paintball.game.events.Event.EventType;

/**
 * @author Dave Waddling
 * 
 * Stores all of the data about an event for display on the game map. 
 *
 */
public class MapPoint {

	private EventType			type;
	private long				timeOccurred;
	private Location			location;
	private PlayerIdentity		attackerIdentity;
	private PlayerIdentity[]	participantIdentities;
	private String				message;


	public MapPoint() {
	}


	public MapPoint(EventType type, long timeOccurred, Location location, PlayerIdentity attackerIdentity, PlayerIdentity[] participantIdentities, String message) {
		this.type = type;
		this.timeOccurred = timeOccurred;
		this.location = location;
		this.attackerIdentity = attackerIdentity;
		this.participantIdentities = participantIdentities;
		this.message = message;
	}


	public EventType getType() {
		return type;
	}


	public void setType(EventType type) {
		this.type = type;
	}


	public long getTimeOccurred() {
		return timeOccurred;
	}


	public void setTimeOccurred(long timeOccurred) {
		this.timeOccurred = timeOccurred;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	public PlayerIdentity getAttackerIdentity() {
		return attackerIdentity;
	}


	public void setAttackerIdentity(PlayerIdentity attackerIdentity) {
		this.attackerIdentity = attackerIdentity;
	}


	public PlayerIdentity[] getParticipantIdentities() {
		return participantIdentities;
	}


	public void setParticipantIdentities(PlayerIdentity[] participantIdentities) {
		this.participantIdentities = participantIdentities;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

}
