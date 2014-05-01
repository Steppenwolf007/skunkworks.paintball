package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_REQUEST_ID;

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
 * RequestIDEndpoint consumes a JoinGameRequest object and returns a JoinResponse object.
 * 
 * Players need to be able to look up their identity based on their Twitter username and
 * this endpoint allows that. Despite using the same request and response objects as the
 * JoinEndpoint no players will be added to the game.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_REQUEST_ID)
public class RequestIDEndpoint {

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public JoinResponse id(JoinGameRequest request) {
		 JoinResponse response = Game.getInstance().handleIDRequest(request);
		 return response;
	}

}
