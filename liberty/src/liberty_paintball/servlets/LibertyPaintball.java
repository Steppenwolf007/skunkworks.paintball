package liberty_paintball.servlets;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import liberty_paintball.demo.DemoEndpoint;
import liberty_paintball.endpoints.AttackEndpoint;
import liberty_paintball.endpoints.RequestIDEndpoint;
import liberty_paintball.endpoints.IntelEndpoint;
import liberty_paintball.endpoints.JoinEndpoint;
import liberty_paintball.endpoints.LeaveEndpoint;
import liberty_paintball.endpoints.MapEndpoint;
import liberty_paintball.endpoints.OmniscienceEndpoint;
import liberty_paintball.endpoints.ResetEndpoint;
import liberty_paintball.endpoints.UpdateLocationEndpoint;

/**
 * @author Dave Waddling
 * 
 * Liberty Paintball servlet. Defines all the REST endpoint classes.
 *
 */
public class LibertyPaintball extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		
		System.out.println("INFO Loaded REST endpoint classes.");
		
		Set<Class<?>> classes = new HashSet<Class<?>>();
		
		classes.add(AttackEndpoint.class);
		classes.add(IntelEndpoint.class);
		classes.add(JoinEndpoint.class);
		classes.add(LeaveEndpoint.class);
		classes.add(MapEndpoint.class);
		classes.add(OmniscienceEndpoint.class);
		classes.add(UpdateLocationEndpoint.class);
		classes.add(RequestIDEndpoint.class);
		classes.add(ResetEndpoint.class);
		classes.add(DemoEndpoint.class);
		
		return classes;
	}

}
