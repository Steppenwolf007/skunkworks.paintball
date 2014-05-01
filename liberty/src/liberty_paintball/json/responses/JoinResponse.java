package liberty_paintball.json.responses;

import liberty_paintball.game.PlayerIdentity;

/**
 * @author Dave Waddling
 *
 */
public class JoinResponse {

	private PlayerIdentity	identity;


	public JoinResponse() {

	}


	public JoinResponse(PlayerIdentity identity) {
		this.identity = identity;
	}


	public PlayerIdentity getPlayerIdentity() {
		return identity;
	}


	public void setPlayerIdentity(PlayerIdentity identity) {
		this.identity = identity;
	}

}
