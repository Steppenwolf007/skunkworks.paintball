package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_UPDATE_LOCATION;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import liberty_paintball.game.Game;
import liberty_paintball.json.requests.UpdateLocationRequest;

/**
 * @author Dave Waddling
 * 
 * UpdateLocationEndpoint consumes an UpdateLocationRequest object and returns nothing.
 * 
 * Updates players geographic locations.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_UPDATE_LOCATION)
public class UpdateLocationEndpoint {

	@POST
	@Consumes(APPLICATION_JSON)
	public void updateLocation(UpdateLocationRequest request) {
		Game.getInstance().handleUpdateLocationRequest(request);
		Response.ok().build();
	}

}
