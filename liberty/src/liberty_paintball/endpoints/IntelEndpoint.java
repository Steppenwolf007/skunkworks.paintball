package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_INTEL;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import liberty_paintball.game.Game;
import liberty_paintball.json.responses.IntelResponse;

/**
 * @author Dave Waddling
 *
 * IntelEndpoint consumes nothing and returns an IntelRespones object.
 * 
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_INTEL)
public class IntelEndpoint {

	@GET
	@Produces(APPLICATION_JSON)
	public IntelResponse attack() {
		 return Game.getInstance().handleIntelRequest();
	}

}
