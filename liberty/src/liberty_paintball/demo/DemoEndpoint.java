package liberty_paintball.demo;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_DEMO;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import liberty_paintball.game.Game;

/**
 * @author Dave Waddling
 * 
 * DemoEndpoint steps through the demo. It doesn't consume anything and it will 
 * return a plain-text response describing what just happened in the demo.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_DEMO)
public class DemoEndpoint {

	@GET
	@Produces(TEXT_PLAIN)
	public String demo() {
		return Game.getInstance().handleDemo();
	}

}
