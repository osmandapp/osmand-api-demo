package main.java.net.osmand.osmandapidemo;

public interface OsmAndCustomizationConstants {

	// Navigation Drawer:

	String DRAWER_ITEM_ID_SCHEME = "drawer.action.";
	String DRAWER_DASHBOARD_ID = DRAWER_ITEM_ID_SCHEME + "dashboard";
	String DRAWER_MAP_MARKERS_ID = DRAWER_ITEM_ID_SCHEME + "map_markers";
	String DRAWER_MY_PLACES_ID = DRAWER_ITEM_ID_SCHEME + "my_places";
	String DRAWER_SEARCH_ID = DRAWER_ITEM_ID_SCHEME + "search";
	String DRAWER_DIRECTIONS_ID = DRAWER_ITEM_ID_SCHEME + "directions";
	String DRAWER_CONFIGURE_MAP_ID = DRAWER_ITEM_ID_SCHEME + "configure_map";
	String DRAWER_DOWNLOAD_MAPS_ID = DRAWER_ITEM_ID_SCHEME + "download_maps";
	String DRAWER_OSMAND_LIVE_ID = DRAWER_ITEM_ID_SCHEME + "osmand_live";
	String DRAWER_TRAVEL_GUIDES_ID = DRAWER_ITEM_ID_SCHEME + "travel_guides";
	String DRAWER_MEASURE_DISTANCE_ID = DRAWER_ITEM_ID_SCHEME + "measure_distance";
	String DRAWER_CONFIGURE_SCREEN_ID = DRAWER_ITEM_ID_SCHEME + "configure_screen";
	String DRAWER_PLUGINS_ID = DRAWER_ITEM_ID_SCHEME + "plugins";
	String DRAWER_SETTINGS_ID = DRAWER_ITEM_ID_SCHEME + "settings";
	String DRAWER_HELP_ID = DRAWER_ITEM_ID_SCHEME + "help";
	String DRAWER_BUILDS_ID = DRAWER_ITEM_ID_SCHEME + "builds";
	String DRAWER_DIVIDER_ID = DRAWER_ITEM_ID_SCHEME + "divider";

	// Configure Map:

	String CONFIGURE_MAP_ITEM_ID_SCHEME = "map.configure.";
	String SHOW_ITEMS_ID_SCHEME = CONFIGURE_MAP_ITEM_ID_SCHEME + "show.";
	String RENDERING_ITEMS_ID_SCHEME = CONFIGURE_MAP_ITEM_ID_SCHEME + "rendering.";
	String CUSTOM_RENDERING_ITEMS_ID_SCHEME = RENDERING_ITEMS_ID_SCHEME + "custom.";

	String APP_PROFILES_ID = CONFIGURE_MAP_ITEM_ID_SCHEME + "app_profiles";

	String SHOW_CATEGORY_ID = SHOW_ITEMS_ID_SCHEME + "category";
	String FAVORITES_ID = SHOW_ITEMS_ID_SCHEME + "favorites";
	String POI_OVERLAY_ID = SHOW_ITEMS_ID_SCHEME + "poi_overlay";
	String POI_OVERLAY_LABELS_ID = SHOW_ITEMS_ID_SCHEME + "poi_overlay_labels";
	String TRANSPORT_ID = SHOW_ITEMS_ID_SCHEME + "transport";
	String GPX_FILES_ID = SHOW_ITEMS_ID_SCHEME + "gpx_files";
	String MAP_MARKERS_ID = SHOW_ITEMS_ID_SCHEME + "map_markers";
	String MAP_SOURCE_ID = SHOW_ITEMS_ID_SCHEME + "map_source";
	String RECORDING_LAYER = SHOW_ITEMS_ID_SCHEME + "recording_layer";
	String MAPILLARY = SHOW_ITEMS_ID_SCHEME + "mapillary";
	String OSM_NOTES = SHOW_ITEMS_ID_SCHEME + "osm_notes";
	String OVERLAY_MAP = SHOW_ITEMS_ID_SCHEME + "overlay_map";
	String UNDERLAY_MAP = SHOW_ITEMS_ID_SCHEME + "underlay_map";
	String CONTOUR_LINES = SHOW_ITEMS_ID_SCHEME + "contour_lines";
	String HILLSHADE_LAYER = SHOW_ITEMS_ID_SCHEME + "hillshade_layer";

	String MAP_RENDERING_CATEGORY_ID = RENDERING_ITEMS_ID_SCHEME + "category";
	String MAP_STYLE_ID = RENDERING_ITEMS_ID_SCHEME + "map_style";
	String MAP_MODE_ID = RENDERING_ITEMS_ID_SCHEME + "map_mode";
	String MAP_MAGNIFIER_ID = RENDERING_ITEMS_ID_SCHEME + "map_marnifier";
	String ROAD_STYLE_ID = RENDERING_ITEMS_ID_SCHEME + "road_style";
	String TEXT_SIZE_ID = RENDERING_ITEMS_ID_SCHEME + "text_size";
	String MAP_LANGUAGE_ID = RENDERING_ITEMS_ID_SCHEME + "map_language";
	String TRANSPORT_RENDERING_ID = RENDERING_ITEMS_ID_SCHEME + "transport";
	String DETAILS_ID = RENDERING_ITEMS_ID_SCHEME + "details";
	String HIDE_ID = RENDERING_ITEMS_ID_SCHEME + "hide";
	String ROUTES_ID = RENDERING_ITEMS_ID_SCHEME + "routes";

	// Map Controls:

	String HUD_BTN_ID_SCHEME = "map.view.";
	String LAYERS_HUD_ID = HUD_BTN_ID_SCHEME + "layers";
	String COMPASS_HUD_ID = HUD_BTN_ID_SCHEME + "compass";
	String QUICK_SEARCH_HUD_ID = HUD_BTN_ID_SCHEME + "quick_search";
	String BACK_TO_LOC_HUD_ID = HUD_BTN_ID_SCHEME + "back_to_loc";
	String MENU_HUD_ID = HUD_BTN_ID_SCHEME + "menu";
	String ROUTE_PLANNING_HUD_ID = HUD_BTN_ID_SCHEME + "route_planning";
	String ZOOM_IN_HUD_ID = HUD_BTN_ID_SCHEME + "zoom_id";
	String ZOOM_OUT_HUD_ID = HUD_BTN_ID_SCHEME + "zoom_out";

	//Map Context Menu Actions:
	String MAP_CONTEXT_MENU_ACTIONS = "point.actions.";
	String MAP_CONTEXT_MENU_DIRECTIONS_FROM_ID = MAP_CONTEXT_MENU_ACTIONS + "directions_from";
	String MAP_CONTEXT_MENU_SEARCH_NEARBY = MAP_CONTEXT_MENU_ACTIONS + "search_nearby";
	String MAP_CONTEXT_MENU_CHANGE_MARKER_POSITION = MAP_CONTEXT_MENU_ACTIONS + "change_m_position";
	String MAP_CONTEXT_MENU_MARK_AS_PARKING_LOC = MAP_CONTEXT_MENU_ACTIONS + "mark_as_parking";
	String MAP_CONTEXT_MENU_MEASURE_DISTANCE = MAP_CONTEXT_MENU_ACTIONS + "measure_distance";
	String MAP_CONTEXT_MENU_EDIT_GPX_WP = MAP_CONTEXT_MENU_ACTIONS + "edit_gpx_waypoint";
	String MAP_CONTEXT_MENU_ADD_GPX_WAYPOINT = MAP_CONTEXT_MENU_ACTIONS + "add_gpx_waypoint";
	String MAP_CONTEXT_MENU_UPDATE_MAP = MAP_CONTEXT_MENU_ACTIONS + "update_map";
	String MAP_CONTEXT_MENU_DOWNLOAD_MAP = MAP_CONTEXT_MENU_ACTIONS + "download_map";
	String MAP_CONTEXT_MENU_MODIFY_POI = MAP_CONTEXT_MENU_ACTIONS + "modify_poi";
	String MAP_CONTEXT_MENU_MODIFY_OSM_CHANGE = MAP_CONTEXT_MENU_ACTIONS + "modify_osm_change";
	String MAP_CONTEXT_MENU_CREATE_POI = MAP_CONTEXT_MENU_ACTIONS + "create_poi";
	String MAP_CONTEXT_MENU_MODIFY_OSM_NOTE = MAP_CONTEXT_MENU_ACTIONS + "modify_osm_note";
	String MAP_CONTEXT_MENU_OPEN_OSM_NOTE = MAP_CONTEXT_MENU_ACTIONS + "open_osm_note";

	//Plug-in's IDs:
	String PLUGIN_OSMAND_MONITOR = "osmand.monitoring";
	String PLUGIN_MAPILLARY = "osmand.mapillary";
	String PLUGIN_OSMAND_DEV = "osmand.development";
	String PLUGIN_AUDIO_VIDEO_NOTES = "osmand.audionotes";
	String PLUGIN_NAUTICAL = "nauticalPlugin.plugin";
	String PLUGIN_OSMAND_EDITING = "osm.editing";
	String PLUGIN_PARKING_POSITION = "osmand.parking.position";
	String PLUGIN_RASTER_MAPS = "osmand.rastermaps";
	String PLUGIN_SKI_MAPS = "skimaps.plugin";
	String PLUGIN_SRTM = "osmand.srtm";

	//Setting IDs
	String SETTING_AVAILBALE_APPLICATION_MODE = "available_application_modes";
	String SETTING_APPLICATION_MODE = "application_mode";
	String SETTING_DEFAULT_APPLICATION_MODE_STRING = "default_application_mode_string";
	String SETTING_DRIVING_REGION_AUTOMATIC = "driving_region_automatic";
	String SETTING_DEFAULT_METRIC_SYSTEM = "default_metric_system";
	String SETTING_DEFAULT_SPEED_SYSTEM = "default_speed_system";

	//Widgets IDs
	//left widgets
	String WIDGET_NEXT_TURN = "next_turn";
	String WIDGET_NEXT_TURN_SMALL = "next_turn_small";
	String WIDGET_NEXT_NEXT_TURN = "next_next_turn";

	//right widgets
	String WIDGET_INTERMEDIATE_DISTANCE = "intermediate_distance";
	String WIDGET_DISTANCE = "distance";
	String WIDGET_TIME = "time";
	String WIDGET_INTERMEDIATE_TIME = "intermediate_time";
	String WIDGET_SPEED = "speed";
	String WIDGET_MAX_SPEED = "max_speed";
	String WIDGET_ALTITUDE = "altitude";
	String WIDGET_GPS_INFO = "gps_info";
	String WIDGET_BEARING = "bearing";

	//top widgets
	String WIDGET_CONFIG = "config";
	String WIDGET_LAYERS = "layers";
	String WIDGET_COMPASS = "compass";
	String WIDGET_STREET_NAME = "street_name";
	String WIDGET_BACK_TO_LOCATION = "back_to_location";
	String WIDGET_MONITORING_SERVICES = "monitoring_services";
	String WIDGET_BGSERVICE = "bgService";


	//OsmAnd app modes
	String APP_MODE_CAR = "car";
	String APP_MODE_PEDESTRIAN = "pedestrian";
	String APP_MODE_BICYCLE = "bicycle";
	String APP_MODE_BOAT = "boat";
	String APP_MODE_AIRCRAFT = "aircraft";
	String APP_MODE_BUS = "bus";
	String APP_MODE_TRAIN = "train";


	//OsmAnd speed and metric constants for settings:
	String SPEED_CONST_KILOMETERS_PER_HOUR = "KILOMETERS_PER_HOUR";
	String SPEED_CONST_MILES_PER_HOUR = "MILES_PER_HOUR";
	String SPEED_CONST_METERS_PER_SECOND = "METERS_PER_SECOND";
	String SPEED_CONST_MINUTES_PER_MILE = "MINUTES_PER_MILE";
	String SPEED_CONST_MINUTES_PER_KILOMETER = "MINUTES_PER_KILOMETER";
	String SPEED_CONST_NAUTICALMILES_PER_HOUR = "NAUTICALMILES_PER_HOUR";
	String METRIC_CONST_KILOMETERS_AND_METERS = "KILOMETERS_AND_METERS";
	String METRIC_CONST_MILES_AND_FEET = "MILES_AND_FEET";
	String METRIC_CONST_MILES_AND_METERS = "MILES_AND_METERS";
	String METRIC_CONST_MILES_AND_YARDS = "MILES_AND_YARDS";
	String METRIC_CONST_NAUTICAL_MILES = "NAUTICAL_MILES";

}
