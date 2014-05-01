package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_ATTACK;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import liberty_paintball.game.Game;
import liberty_paintball.json.requests.AttackRequest;
import liberty_paintball.json.responses.AttackResponse;

/**
 * @author Dave Waddling
 *
 * AttackEndpoint consumes an AttackRequest object and returns an AttackResponse object.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_ATTACK)
public class AttackEndpoint {

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public AttackResponse attack(AttackRequest request) {
		 AttackResponse response = Game.getInstance().handleAttackRequest(request);
		 return response;
	}

}
