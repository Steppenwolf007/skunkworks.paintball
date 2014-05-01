package liberty_paintball.game.events;

import java.util.concurrent.TimeUnit;

import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 * 
 * Represents an in-game Event and provides methods to access the data about it.
 *
 */
public abstract class Event {

	public enum EventType {
		PLAYER_HIT, PLAYER_OUTGUNNED, PLAYER_MISS, LOCATION_UPDATE,
		// Entries below unused currently, just there for completeness.
		PLAYER_JOIN, PLAYER_LEAVE
	}

	protected long		timeOccurred;
	protected EventType	type;


	protected Event(EventType type) {
		timeOccurred = System.currentTimeMillis();
		this.type = type;
	}


	public long getTimeOccurred() {
		return timeOccurred;
	}


	public EventType getEventType() {
		return type;
	}


	public boolean isOlderThanMinutes(int minimumAgeMinutes) {
		long minimumAgeMillis = TimeUnit.MINUTES.toMillis(minimumAgeMinutes);
		return timeOccurred + minimumAgeMillis < System.currentTimeMillis();
	}


	@Override
	public String toString() {
		switch (type) {
		case PLAYER_JOIN:
			return "A new player has joined the game.";
		case PLAYER_LEAVE:
			return "A player has left the game.";
		case PLAYER_HIT:
			return "A player attacked and hit.";
		case PLAYER_MISS:
			return "A player made an attack but missed.";
		case PLAYER_OUTGUNNED:
			return "A player made an attack but was outgunned.";
		default:
			return "INVALID EVENT";

		}
	}


	public abstract MapPoint toMapPoint();

	// public abstract MapPoint generateMapPoint();

}
