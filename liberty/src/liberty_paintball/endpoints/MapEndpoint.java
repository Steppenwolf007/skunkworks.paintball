package liberty_paintball.endpoints;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static liberty_paintball.Config.APPLICATION_NAME;
import static liberty_paintball.Config.ENDPOINT_PATH_MAP;

import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import liberty_paintball.game.Game;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 * 
 * MapEndpoint consumes nothing and returns a list of MapPoint objects.
 *
 * This endpoint will return a map of game events but will purposely omit the latest events 
 * so that players cannot exploit it. The delay is configured in Timeline.java.
 *
 */
@ApplicationPath(APPLICATION_NAME)
@Path(ENDPOINT_PATH_MAP)
public class MapEndpoint extends Application {

	@GET
	@Produces(APPLICATION_JSON)
	public List<MapPoint> getMap() {
		return Game.getInstance().getMapData(false);
	}
}
