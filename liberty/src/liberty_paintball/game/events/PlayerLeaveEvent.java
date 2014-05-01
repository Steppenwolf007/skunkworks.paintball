package liberty_paintball.game.events;

import static liberty_paintball.game.events.Event.EventType.PLAYER_LEAVE;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 *
 */
public class PlayerLeaveEvent extends Event {

	private String	username;


	public PlayerLeaveEvent(String username) {
		super(PLAYER_LEAVE);
		this.username = username;
	}


	public String getUsername() {
		return username;
	}


	@Override
	public MapPoint toMapPoint() {
		return null;
	}


}
