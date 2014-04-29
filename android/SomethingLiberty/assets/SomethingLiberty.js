MAP_CENTER_LONGITUDE = -1.397173;
MAP_CENTER_LATITUDE = 51.026145;
DEFAULT_ZOOM_LEVEL = 18;

var map;
var markerLayer;

initPage = function()
{
	map = new OpenLayers.Map("map");
	var osmLayer= new OpenLayers.Layer.OSM("OSM");
	markerLayer = new OpenLayers.Layer.Markers("Markers");

	map.addLayer(markerLayer);
	map.addLayer(osmLayer);

	var centerOfMap = makeLonLat(MAP_CENTER_LONGITUDE,MAP_CENTER_LATITUDE);
	map.setCenter(centerOfMap,DEFAULT_ZOOM_LEVEL);
}

makeLonLat = function(longitude,latitude)
{
	var lonlat = new OpenLayers.LonLat(longitude,latitude);
	lonlat.transform(
		new OpenLayers.Projection("EPSG:4326"),
		map.getProjectionObject() //EPSG:900913
	);

	return lonlat;
}

displayEvent = function(message)
{
}

placeMarker = function(imgSrc,lonLat)
{
	var size = new OpenLayers.Size(21,25);
	var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
	var icon = new OpenLayers.Icon(imgSrc, size, offset);
	var marker = new OpenLayers.Marker(lonLat,icon);

	markerLayer.addMarker(marker);
}



addEvent = function(imgSrc,longitude,latitude)
{
	var lonlat = makeLonLat(longitude,latitude);
	placeMarker(imgSrc,lonlat);
}

removeAllEvents = function()
{
    markerLayer.clearMarkers();
}
