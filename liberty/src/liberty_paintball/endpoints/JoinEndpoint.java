package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_JOIN;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import liberty_paintball.game.Game;
import liberty_paintball.json.requests.JoinGameRequest;
import liberty_paintball.json.responses.JoinResponse;

/**
 * @author Dave Waddling
 * 
 * JoinEndpoint consumes a JoinGameRequest object and returns a JoinResponse object.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_JOIN)
public class JoinEndpoint {

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public JoinResponse join(JoinGameRequest request) {
		return Game.getInstance().handleJoinGameRequest(request);
	}

}
