package liberty_paintball.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import liberty_paintball.game.events.Event;
import liberty_paintball.mapping.MapPoint;

/**
 * @author Dave Waddling
 * 
 * Maintains a store of game events in chronological order. 
 *
 */
public class Timeline {

	// When delayed map data is requested this value determines how old
	// an event must be for it to be returned.
	private static final int	NEWS_FEED_DELAY_MINUTES	= 5;

	private List<Event>			events;


	public Timeline() {
		events = new LinkedList<Event>();
	}


	public void addEvent(Event event) {
		synchronized (events) {
			events.add(event);
		}
	}


	public List<Event> getAllEvents() {
		synchronized (events) {
			return events;
		}
	}


	public List<Event> getLatestEvents(int numEventsRequested) {
		synchronized (events) {
			// Cannot return more events than are in the list (size-1).
			int lastEventIndex = numEventsRequested < events.size() ? numEventsRequested : events.size() - 1;
			return events.subList(0, lastEventIndex);
		}
	}


	public List<MapPoint> getRealtimeMapData() {
		List<MapPoint> mapData = new ArrayList<MapPoint>();
		synchronized (events) {
			for (Event event : events) {
				mapData.add(event.toMapPoint());
			}
		}
		return mapData;
	}


	public List<MapPoint> getDelayedMapData() {
		List<MapPoint> mapData = new ArrayList<MapPoint>();
		synchronized (events) {
			for (Event event : events) {
				if (event.isOlderThanMinutes(NEWS_FEED_DELAY_MINUTES)) {
					mapData.add(event.toMapPoint());
				}
			}
			return mapData;
		}
	}

}
