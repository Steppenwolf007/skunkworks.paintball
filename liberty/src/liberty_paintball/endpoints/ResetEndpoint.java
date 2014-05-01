package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_RESET;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import liberty_paintball.game.Game;

/**
 * @author Dave Waddling
 * 
 * ResetEndpoint consumes nothing and returns a plaint-text response. 
 * 
 * All game state is cleared.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_RESET)
public class ResetEndpoint {

	@GET
	@Produces(TEXT_PLAIN)
	public String reset() {
		return Game.getInstance().reset();
	}

}
