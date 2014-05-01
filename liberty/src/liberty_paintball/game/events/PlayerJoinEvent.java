package liberty_paintball.game.events;

import static liberty_paintball.game.events.Event.EventType.PLAYER_JOIN;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 *
 */
public class PlayerJoinEvent extends Event {

	private String	username;


	public PlayerJoinEvent(String username) {
		super(PLAYER_JOIN);
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
