package main.java.net.osmand.osmandapidemo

object OsmandCustomizationConstants {

    // Navigation Drawer:

    const val DRAWER_ITEM_ID_SCHEME = "drawer.action."
    const val DRAWER_SWITCH_PROFILE_ID = DRAWER_ITEM_ID_SCHEME + "switch_profile";
    const val DRAWER_CONFIGURE_PROFILE_ID = DRAWER_ITEM_ID_SCHEME + "configure_profile";
    const val DRAWER_DASHBOARD_ID = DRAWER_ITEM_ID_SCHEME + "dashboard"
    const val DRAWER_MAP_MARKERS_ID = DRAWER_ITEM_ID_SCHEME + "map_markers"
    const val DRAWER_MY_PLACES_ID = DRAWER_ITEM_ID_SCHEME + "my_places"
    const val DRAWER_SEARCH_ID = DRAWER_ITEM_ID_SCHEME + "search"
    const val DRAWER_DIRECTIONS_ID = DRAWER_ITEM_ID_SCHEME + "directions"
    const val DRAWER_CONFIGURE_MAP_ID = DRAWER_ITEM_ID_SCHEME + "configure_map"
    const val DRAWER_DOWNLOAD_MAPS_ID = DRAWER_ITEM_ID_SCHEME + "download_maps"
    const val DRAWER_OSMAND_LIVE_ID = DRAWER_ITEM_ID_SCHEME + "osmand_live"
    const val DRAWER_TRAVEL_GUIDES_ID = DRAWER_ITEM_ID_SCHEME + "travel_guides"
    const val DRAWER_MEASURE_DISTANCE_ID = DRAWER_ITEM_ID_SCHEME + "measure_distance"
    const val DRAWER_CONFIGURE_SCREEN_ID = DRAWER_ITEM_ID_SCHEME + "configure_screen"
    const val DRAWER_PLUGINS_ID = DRAWER_ITEM_ID_SCHEME + "plugins"
    const val DRAWER_SETTINGS_ID = DRAWER_ITEM_ID_SCHEME + "settings"
    const val DRAWER_HELP_ID = DRAWER_ITEM_ID_SCHEME + "help"
    const val DRAWER_BUILDS_ID = DRAWER_ITEM_ID_SCHEME + "builds"
    const val DRAWER_DIVIDER_ID = DRAWER_ITEM_ID_SCHEME + "divider"

    // Configure Map:

    const val CONFIGURE_MAP_ITEM_ID_SCHEME = "map.configure."
    const val SHOW_ITEMS_ID_SCHEME = CONFIGURE_MAP_ITEM_ID_SCHEME + "show."
    const val RENDERING_ITEMS_ID_SCHEME = CONFIGURE_MAP_ITEM_ID_SCHEME + "rendering."
    const val CUSTOM_RENDERING_ITEMS_ID_SCHEME = RENDERING_ITEMS_ID_SCHEME + "custom."

    const val APP_PROFILES_ID = CONFIGURE_MAP_ITEM_ID_SCHEME + "app_profiles"

    const val SHOW_CATEGORY_ID = SHOW_ITEMS_ID_SCHEME + "category"
    const val FAVORITES_ID = SHOW_ITEMS_ID_SCHEME + "favorites"
    const val POI_OVERLAY_ID = SHOW_ITEMS_ID_SCHEME + "poi_overlay"
    const val POI_OVERLAY_LABELS_ID = SHOW_ITEMS_ID_SCHEME + "poi_overlay_labels"
    const val TRANSPORT_ID = SHOW_ITEMS_ID_SCHEME + "transport"
    const val GPX_FILES_ID = SHOW_ITEMS_ID_SCHEME + "gpx_files"
    const val MAP_MARKERS_ID = SHOW_ITEMS_ID_SCHEME + "map_markers"
    const val MAP_SOURCE_ID = SHOW_ITEMS_ID_SCHEME + "map_source"
    const val RECORDING_LAYER = SHOW_ITEMS_ID_SCHEME + "recording_layer"
    const val MAPILLARY = SHOW_ITEMS_ID_SCHEME + "mapillary"
    const val OSM_NOTES = SHOW_ITEMS_ID_SCHEME + "osm_notes"
    const val OVERLAY_MAP = SHOW_ITEMS_ID_SCHEME + "overlay_map"
    const val UNDERLAY_MAP = SHOW_ITEMS_ID_SCHEME + "underlay_map"
    const val CONTOUR_LINES = SHOW_ITEMS_ID_SCHEME + "contour_lines"
    const val HILLSHADE_LAYER = SHOW_ITEMS_ID_SCHEME + "hillshade_layer"

    const val MAP_RENDERING_CATEGORY_ID = RENDERING_ITEMS_ID_SCHEME + "category"
    const val MAP_STYLE_ID = RENDERING_ITEMS_ID_SCHEME + "map_style"
    const val MAP_MODE_ID = RENDERING_ITEMS_ID_SCHEME + "map_mode"
    const val MAP_MAGNIFIER_ID = RENDERING_ITEMS_ID_SCHEME + "map_marnifier"
    const val ROAD_STYLE_ID = RENDERING_ITEMS_ID_SCHEME + "road_style"
    const val TEXT_SIZE_ID = RENDERING_ITEMS_ID_SCHEME + "text_size"
    const val MAP_LANGUAGE_ID = RENDERING_ITEMS_ID_SCHEME + "map_language"
    const val TRANSPORT_RENDERING_ID = RENDERING_ITEMS_ID_SCHEME + "transport"
    const val DETAILS_ID = RENDERING_ITEMS_ID_SCHEME + "details"
    const val HIDE_ID = RENDERING_ITEMS_ID_SCHEME + "hide"
    const val ROUTES_ID = RENDERING_ITEMS_ID_SCHEME + "routes"

    // Map Controls:

    const val HUD_BTN_ID_SCHEME = "map.view."
    const val LAYERS_HUD_ID = HUD_BTN_ID_SCHEME + "layers"
    const val COMPASS_HUD_ID = HUD_BTN_ID_SCHEME + "compass"
    const val QUICK_SEARCH_HUD_ID = HUD_BTN_ID_SCHEME + "quick_search"
    const val BACK_TO_LOC_HUD_ID = HUD_BTN_ID_SCHEME + "back_to_loc"
    const val MENU_HUD_ID = HUD_BTN_ID_SCHEME + "menu"
    const val ROUTE_PLANNING_HUD_ID = HUD_BTN_ID_SCHEME + "route_planning"
    const val ZOOM_IN_HUD_ID = HUD_BTN_ID_SCHEME + "zoom_id"
    const val ZOOM_OUT_HUD_ID = HUD_BTN_ID_SCHEME + "zoom_out"


    //Map Context Menu Actions:
    val MAP_CONTEXT_MENU_ACTIONS = "point.actions."
    val MAP_CONTEXT_MENU_DIRECTIONS_FROM_ID = MAP_CONTEXT_MENU_ACTIONS + "directions_from"
    val MAP_CONTEXT_MENU_SEARCH_NEARBY = MAP_CONTEXT_MENU_ACTIONS + "search_nearby"
    val MAP_CONTEXT_MENU_CHANGE_MARKER_POSITION = MAP_CONTEXT_MENU_ACTIONS + "change_m_position"
    val MAP_CONTEXT_MENU_MARK_AS_PARKING_LOC = MAP_CONTEXT_MENU_ACTIONS + "mark_as_parking"
    val MAP_CONTEXT_MENU_MEASURE_DISTANCE = MAP_CONTEXT_MENU_ACTIONS + "measure_distance"
    val MAP_CONTEXT_MENU_EDIT_GPX_WP = MAP_CONTEXT_MENU_ACTIONS + "edit_gpx_waypoint"
    val MAP_CONTEXT_MENU_ADD_GPX_WAYPOINT = MAP_CONTEXT_MENU_ACTIONS + "add_gpx_waypoint"
    val MAP_CONTEXT_MENU_UPDATE_MAP = MAP_CONTEXT_MENU_ACTIONS + "update_map"
    val MAP_CONTEXT_MENU_DOWNLOAD_MAP = MAP_CONTEXT_MENU_ACTIONS + "download_map"
    val MAP_CONTEXT_MENU_MODIFY_POI = MAP_CONTEXT_MENU_ACTIONS + "modify_poi"
    val MAP_CONTEXT_MENU_MODIFY_OSM_CHANGE = MAP_CONTEXT_MENU_ACTIONS + "modify_osm_change"
    val MAP_CONTEXT_MENU_CREATE_POI = MAP_CONTEXT_MENU_ACTIONS + "create_poi"
    val MAP_CONTEXT_MENU_MODIFY_OSM_NOTE = MAP_CONTEXT_MENU_ACTIONS + "modify_osm_note"
    val MAP_CONTEXT_MENU_OPEN_OSM_NOTE = MAP_CONTEXT_MENU_ACTIONS + "open_osm_note"

    //Plug-in's IDs:
    val PLUGIN_OSMAND_MONITOR = "osmand.monitoring"
    val PLUGIN_MAPILLARY = "osmand.mapillary"
    val PLUGIN_OSMAND_DEV = "osmand.development"
    val PLUGIN_AUDIO_VIDEO_NOTES = "osmand.audionotes"
    val PLUGIN_NAUTICAL = "nauticalPlugin.plugin"
    val PLUGIN_OSMAND_EDITING = "osm.editing"
    val PLUGIN_PARKING_POSITION = "osmand.parking.position"
    val PLUGIN_RASTER_MAPS = "osmand.rastermaps"
    val PLUGIN_SKI_MAPS = "skimaps.plugin"
    val PLUGIN_SRTM = "osmand.srtm"
}