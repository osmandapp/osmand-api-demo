package net.osmand.library.sample;

import net.osmand.map.ITileSource;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.resources.SQLiteTileSource;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.settings.backend.preferences.CommonPreference;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.layers.MapTextLayer;
import net.osmand.plus.views.layers.MapTileLayer;
import net.osmand.plus.views.layers.MapVectorLayer;
import net.osmand.plus.views.layers.PointLocationLayer;
import net.osmand.plus.views.mapwidgets.MapWidgetRegistry;

public class MapLayers {

	private MainActivity activity;
	private MapWidgetRegistry mapWidgetRegistry;

	private MapTextLayer mapTextLayer;
	private MapTileLayer mapTileLayer;
	private MapVectorLayer mapVectorLayer;
	private PointLocationLayer locationLayer;
	private MapControlsLayer mapControlsLayer;

	public MapLayers(MainActivity activity) {
		this.activity = activity;
		this.mapWidgetRegistry = new MapWidgetRegistry(getApplication());
	}

	public MapWidgetRegistry getMapWidgetRegistry() {
		return mapWidgetRegistry;
	}

	public SampleApplication getApplication() {
		return (SampleApplication) activity.getApplication();
	}

	public void createLayers(OsmandMapTileView mapView) {

		OsmandApplication app = getApplication();
		RoutingHelper routingHelper = app.getRoutingHelper();
		// first create to make accessible
		mapTextLayer = new MapTextLayer(activity);
		// 5.95 all labels
		mapView.addLayer(mapTextLayer, 5.95f);

		mapTileLayer = new MapTileLayer(activity, true);
		mapView.addLayer(mapTileLayer, 0.0f);
		mapView.setMainLayer(mapTileLayer);

		// 0.5 layer
		mapVectorLayer = new MapVectorLayer(activity);
		mapView.addLayer(mapVectorLayer, 0.5f);

		// 6. point location layer
		locationLayer = new PointLocationLayer(activity);
		mapView.addLayer(locationLayer, 6);

		// 11. map controls layer
		mapControlsLayer = new MapControlsLayer(activity);
		mapView.addLayer(mapControlsLayer, 11);
	}

	public void updateLayers(OsmandMapTileView mapView) {
		OsmandSettings settings = getApplication().getSettings();
		updateMapSource(mapView, settings.MAP_TILE_SOURCES);
	}

	public void updateMapSource(OsmandMapTileView mapView, CommonPreference<String> settingsToWarnAboutMap) {
		OsmandSettings settings = getApplication().getSettings();

		// update transparency
		int mapTransparency = settings.MAP_UNDERLAY.get() == null ? 255 : settings.MAP_TRANSPARENCY.get();
		mapTileLayer.setAlpha(mapTransparency);
		mapVectorLayer.setAlpha(mapTransparency);

		ITileSource newSource = settings.getMapTileSource(settings.MAP_TILE_SOURCES == settingsToWarnAboutMap);
		ITileSource oldMap = mapTileLayer.getMap();
		if (newSource != oldMap) {
			if (oldMap instanceof SQLiteTileSource) {
				((SQLiteTileSource) oldMap).closeDB();
			}
			mapTileLayer.setMap(newSource);
		}

		boolean vectorData = !settings.MAP_ONLINE_DATA.get();
		mapTileLayer.setVisible(!vectorData);
		mapVectorLayer.setVisible(vectorData);
		if (vectorData) {
			mapView.setMainLayer(mapVectorLayer);
		} else {
			mapView.setMainLayer(mapTileLayer);
		}
	}

	public MapTextLayer getMapTextLayer() {
		return mapTextLayer;
	}

	public MapTileLayer getMapTileLayer() {
		return mapTileLayer;
	}

	public MapVectorLayer getMapVectorLayer() {
		return mapVectorLayer;
	}

	public PointLocationLayer getLocationLayer() {
		return locationLayer;
	}

	public MapControlsLayer getMapControlsLayer() {
		return mapControlsLayer;
	}
}
