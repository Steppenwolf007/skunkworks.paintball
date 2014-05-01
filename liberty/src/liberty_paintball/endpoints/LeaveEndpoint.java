package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_LEAVE;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import liberty_paintball.game.Game;
import liberty_paintball.json.requests.LeaveGameRequest;

/**
 * @author Dave Waddling
 * 
 * LeaveEndpoint consumes a LeaveGameRequest object and returns nothing.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_LEAVE)
public class LeaveEndpoint {

	@POST
	@Consumes(APPLICATION_JSON)
	public void leave(LeaveGameRequest request) {
		Game.getInstance().handleLeaveGameRequest(request);
		Response.ok().build();
	}

}
