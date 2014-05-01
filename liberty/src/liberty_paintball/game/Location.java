package liberty_paintball.game;

import static java.lang.Math.cos;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

/**
 * @author Dave Waddling
 * 
 * Stores a player's location as decimal latitude/longitude coordinates along with the time
 * of the last update. The distanceBetweenLocationsMeters() utility method can be used to calculate
 * the distance between a pair of locations but it's fairly approximate due to using a single value
 * for the radius of the Earth when in reality it varies due to the planet not being perfectly spherical.
 *
 */
public class Location {

	private static final double	EARTH_RADIUS_METERS	= 6371100;

	private double				latitude;
	private double				longitude;
	private long				updatedTimeMillis;


	public Location() {
	}


	@Override
	public Location clone() {
		return new Location(latitude, longitude, updatedTimeMillis);
	}


	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.updatedTimeMillis = System.currentTimeMillis();
	}


	private Location(double latitude, double longitude, long updatedTimeMillis) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.updatedTimeMillis = updatedTimeMillis;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public long getUpdatedTimeMillis() {
		return updatedTimeMillis;
	}


	public void setUpdatedTimeMillis(long updatedTimeMillis) {
		this.updatedTimeMillis = updatedTimeMillis;
	}

	
	public static double distanceBetweenLocationsMeters(Location locationA, Location locationB) {

		double deltaLatitudeRadians = toRadians(locationB.latitude - locationA.latitude);
		double deltaLongitudeRadians = toRadians(locationB.longitude - locationA.longitude);

		double cosLatitudeA = cos(toRadians(locationA.latitude));
		double cosLatitudeB = cos(toRadians(locationB.latitude));

		double a = haversin(deltaLatitudeRadians) + haversin(deltaLongitudeRadians) * cosLatitudeA * cosLatitudeB;
		double c = 2 * Math.asin(sqrt(a));

		return EARTH_RADIUS_METERS * c;
	}


	private static double haversin(double delta) {
		return (1.0d - cos(delta)) / 2.0d;
	}


	@Override
	public String toString() {
		return latitude + "," + longitude;
	}
}
