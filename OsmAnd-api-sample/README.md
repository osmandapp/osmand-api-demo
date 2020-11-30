# osmand-api-demo
Example of usage OsmAnd API

## Intent

The simplest way to integrate with OsmAnd intent API is to copy
OsmAndHelper.java into your project, and call it's methods.

You can create URI yourself but it is suggested to use constants 
from OsmApiHelper anyway.

#### Command: ADD_FAVORITE
Add favourite at given location

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude;
    * `PARAM_NAME` — name of favourite item;
    * `PARAM_DESC` — description of favourite item;
    * `PARAM_CATEGORY` — category of favourite item. Symbols that are not safe for directory name will be removed;
    * `PARAM_COLOR` — color of favourite item. Can be one of: "red", "orange", "yellow", "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown";
    * `PARAM_VISIBLE` — should favourite item be visible after creation.

#### Command: ADD_MAP_MARKER
Add map marker at given location.

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude;
    * `PARAM_NAME` — name.

#### Command: SHOW_LOCATION
Open map at given location

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude.

#### Command: RECORD_AUDIO
Request to start recording audio note for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude.

#### Command: RECORD_VIDEO
Request to start recording video note for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude.

#### Command: STOP_AV_REC
Stop recording audio or video.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

#### Command: RECORD_PHOTO
Request to take photo for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

*  **URI parameters:**
    * `PARAM_LAT` — latitude;
    * `PARAM_LON` — longitude.

#### Command: START_GPX_REC
Start recording GPX track.

*  **URI parameters:**
    * `PARAM_CLOSE_AFTER_COMMAND` — true if OsmAnd should be close immediately after executing command.

#### Command: STOP_GPX_REC
Stop recording GPX track.

*  **URI parameters:**
    * `PARAM_CLOSE_AFTER_COMMAND` — true if OsmAnd should be close immediately after executing command.

#### Command: SHOW_GPX
Show GPX file on map.
OsmAnd must have rights to access location. Not recommended.

*  **URI parameters:**
    * `PARAM_PATH` — path to file.

#### Command: SHOW_GPX
Show GPX file on map.
In current implementation it is recommended way to share file if your app supports API 15.

*  **Extra parameter:**
    * `PARAM_DATA` — Raw contents of GPX file. Sent as intent's extra string parameter.

#### Command: SHOW_GPX
@TargetApi(16)
Show GPX file on map.
Recommended way to share file.
In current implementation it is recommended way to share file if your app supports API 16
and above.
URI created by FileProvider must be sent as ClipData

*  **URI parameters:**
    * `PARAM_URI`. Must be "true".

#### Command: NAVIGATE_GPX
Navigate GPX file.
OsmAnd must have rights to access location. Not recommended.

*  **URI parameters:**
    * `PARAM_FORCE` — Stop previous navigation if active;
    * `PARAM_PATH` — File which represents GPX track.

#### Command: NAVIGATE_GPX
Navigate GPX file.
In current implementation it is recommended way to share file if your app supports API 15.

*  **URI parameters:**
    * `PARAM_FORCE` — Stop previous navigation if active.
    
*  **Extra parameter:**
    * `PARAM_DATA` — Raw contents of GPX file.

#### Command: NAVIGATE_GPX
@TargetApi(16)
Navigate GPX file.
Recommended way to share file.
In current implementation it is recommended way to share file if your app supports API 16
and above.
URI created by FileProvider must be sent as ClipData

*  **URI parameters:**
    * `PARAM_URI` must be "true";
    *   PARAM_FORCE` Stop previous navigation if active.

#### Command: NAVIGATE
Navigate from one location to another.

*  **URI parameters:**
    * `PARAM_START_LAT` — Latitude of starting point;
    * `PARAM_START_LON` — Longitude of starting point;
    * `PARAM_START_NAME` — Name of starting point.
    <i>If starting point's params (name/lat/lon) are not defined, the current location is used as start point.</i>
    * `PARAM_DEST_LAT` — Latitude of destination point;
    * `PARAM_DEST_LON` — Longitude of destination point;
    * `PARAM_DEST_NAME` — Name of destination point;
    * `PARAM_PROFILE` — Map profile can be one of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck";
    * `PARAM_FORCE` — Stop previous navigation if active.
    <i>If "true" — stops current navigation without alert. Otherwise - asks user to stop current navigation.</i>

#### Command: NAVIGATE_SEARCH
Search destination point and start navigation.

*  **URI parameters:**
    * `PARAM_START_LAT` — Latitude of starting point;
    * `PARAM_START_LON` — Longitude of starting point.
    * `PARAM_START_NAME` — Name of starting point;
    <i>If parameters of starting point (name/lat/lon) are not defined, the current location is used as start point.</i>
    * `PARAM_DEST_SEARCH_QUERY` — Text of a query for searching a destination point;
    * `PARAM_SEARCH_LAT` — Original location of search (latitude);
    * `PARAM_SEARCH_LON` — Original location of search (longitude).
    <i>A search query and original location of the search must be defined.</i>
    * `PARAM_SHOW_SEARCH_RESULTS` — Show search results on the screen.
    <i>If "true" — shows screen with search results where user can specify a destination point. Otherwise - pick first search result and start navigation immediately.</i>
    * `PARAM_PROFILE` — Map profile can be one of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck";
    * `PARAM_FORCE` — Stop previous navigation if active.
    <i>If "true" — stops current navigation without alert. Otherwise - asks user to stop current navigation.</i>

#### Command: PAUSE_NAVIGATION
Put navigation on pause.

#### Command: RESUME_NAVIGATION
Resume navigation if it was paused before.

#### Command: STOP_NAVIGATION
Stop navigation. Removes target / intermediate points and route path from the map.

#### Command: MUTE_NAVIGATION
Mute voice guidance. Stays muted until unmute manually or via the api.

#### Command: UNMUTE_NAVIGATION
Unmute voice guidance.

#### Command: GET_INFO
Simply requests data about OsmAnd status.
Data returned as extras. Each key value pair as separate entity.

## AIDL

The simplest way to integrate with OsmAnd AIDL API is to copy
OsmAndAidlHelper.java into your project, and call it's methods.

#### Command: `addMapMarker()`
Add map marker at given location.

*  **Parameters:**
    * `lat` — latitude;
    * `lon` — longitude;
    * `name` — name of marker.

#### Command: `updateMapMarker()`
Update map marker.
If ignoreCoordinates is false the marker gets updated only if latPrev/lonPrev match the currently set values of the marker.
If ignoreCoordinates is true the marker gets updated if the name matches, the values of latPrev/lonPrev are ignored.

*  **Parameters:**
    * `latPrev` — latitude (current marker);
    * `lonPrev` — longitude (current marker);
    * `namePrev` — name (current marker);
    * `latNew` — latitude (new marker);
    * `lonNew` — longitude (new marker);
    * `nameNew` — name (new marker);
    * `ignoreCoordinates` — flag to determine whether latPrev/lonPrev shall be ignored.

#### Command: `removeMapMarker()`
Remove map marker.
If ignoreCoordinates is false the marker is only removed if lat/lon match the currently set values of the marker.
If ignoreCoordinates is true the marker is removed if the name matches, the values of lat/lon are ignored.

*  **Parameters:**
    * `lat` — latitude;
    * `lon` — longitude;
    * `name` — name of marker;
    * `ignoreCoordinates` — flag to determine whether lat/lon shall be ignored.

#### Command: `addFavoriteGroup()`
Add favorite group with given params.

*  **Parameters:**
    * `name` — group name;
    * `color` — group color. Can be one of: "red", "orange", "yellow", "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown";
    * `visible` — group visibility.

#### Command: `updateFavoriteGroup()`
Update favorite group with given params.

*  **Parameters:**
    * `namePrev` — group name (current);
    * `colorPrev` — group color (current);
    * `visiblePrev` — group visibility (current);
    * `nameNew` — group name (new);
    * `colorNew` — group color (new);
    * `visibleNew` — group visibility (new).

#### Command: `removeFavoriteGroup()`
Remove favorite group with given name.

*  **Parameters:**
    * `name` — name of favorite group.

#### Command: `addFavorite()`
Add favorite at given location with given params.

*  **Parameters:**
    * `lat` — latitude;
    * `lon` — longitude;
    * `name` — name of favorite item;
    * `description` — description of favorite item;
    * `category` — category of favorite item;
    * `color` — color of favorite item. Can be one of: "red", "orange", "yellow", "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown";
    * `visible` — should favorite item be visible after creation.

#### Command: `removeFavorite()`
Remove favorite at given location with given params.

*  **Parameters:**
    * `lat` — latitude;
    * `lon` — longitude;
    * `name` — name of favorite item;
    * `category` — category of favorite item.

#### Command: `updateFavorite()`
Update favorite at given location with given params.

*  **Parameters:**
    * `latPrev` — latitude (current favorite);
    * `lonPrev` — longitude (current favorite);
    * `namePrev` — name of favorite item (current favorite);
    * `categoryPrev` — category of favorite item (current favorite);
    * `latNew` — latitude (new favorite);
    * `lonNew` — longitude (new favorite);
    * `nameNew` — name of favorite item (new favorite);
    * `descriptionNew` — description of favorite item (new favorite);
    * `categoryNew` — category of favorite item (new favorite). category of favorite item (new favorite). Use only to create a new category, not to update an existing one. If you want to  update an existing category, use the {@link #updateFavoriteGroup(String, String, boolean, String, String, boolean)} method;
    * `colorNew` — color of new category. Can be one of: "red", "orange", "yellow", "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown";
    * `visibleNew` — should new category be visible after creation.


#### Command: `addMapLayer()`
Add user layer on the map.

*  **Parameters:**
    * `id` — layer id;
    * `name` — layer name;
    * `zOrder` — z-order position of layer. Default value is 5.5f;
    * `points` — initial list of points. Nullable;
    * `imagePoints` — use new style for points on map or not. Also default zoom bounds for new style can be edited.

#### Command: `removeMapLayer()`
Remove user layer.

*  **Parameters:**
    * `id` — layer id.

#### Command: `updateMapLayer()`
Update user layer.

*  **Parameters:**
    * `id` — layer id;
    * `name` — layer name;
    * `zOrder` — z-order position of layer. Default value is 5.5f;
    * `points` — initial list of points. Nullable;
    * `imagePoints` — use new style for points on map or not. Also default zoom bounds for new style can be edited.

#### Command: `showMapPoint()`
Show AMapPoint on map in OsmAnd.

*  **Parameters:**
    * `layerId` — layer id. Note: layer should be added first;
    * `pointId` — point id;
    * `shortName` — short name (single char). Displayed on the map;
    * `fullName` — full name. Displayed in the context menu on the first row;
    * `typeName` — type name. Displayed in context menu on second row;
    * `color` — color of circle's background;
    * `location` — location of the point;
    * `details` — list of details. Displayed under context menu;
    * `params` — optional map of params for point.

#### Command: `addMapPoint()`
Add point to user layer.

*  **Parameters:**
    * `layerId` — layer id. Note: layer should be added first;
    * `pointId` — point id;
    * `shortName` — short name (single char). Displayed on the map;
    * `fullName` — full name. Displayed in the context menu on first row;
    * `typeName` — type name. Displayed in context menu on second row;
    * `color` — color of circle's background;
    * `location` — location of the point;
    * `details` — list of details. Displayed under context menu;
    * `params` — optional map of params for point.

#### Command: `removeMapPoint()`
Remove point.

*  **Parameters:**
    * `layerId` — layer id;
    * `pointId` — point id.

#### Command: `updateMapPoint()`
Update point.

*  **Parameters:**
    * `layerId` — layer id;
    * `pointId` — point id;
    * `updateOpenedMenuAndMap` — flag to enable folowing mode and menu updates for point;
    * `shortName` — short name (single char). Displayed on the map;
    * `fullName` — full name. Displayed in the context menu on first row;
    * `typeName` — type name. Displayed in context menu on second row;
    * `color` — color of circle's background;
    * `location` — location of the point;
    * `details` — list of details. Displayed under context menu;
    * `params` — optional map of params for point.    

#### Command: `refreshMap()`
Refresh the map (UI).


#### Command: `importGpx()`
Import GPX file to OsmAnd (from URI or file).

*  **Parameters:**
    * `gpxUri` — URI created by FileProvider (preference method).
    * `file` — File which represents GPX track (not recomended, OsmAnd should have rights to access file location).
    * `fileName` — Destination file name. May contain dirs.
    * `color` — color of gpx. Can be one of: "red", "orange", "lightblue", "blue", "purple", "translucent_red", "translucent_orange", "translucent_lightblue", "translucent_blue", "translucent_purple"
    * `show` — show track on the map after import


#### Command: `showGpx()`
Show GPX file on map.

*  **Parameters:**
    * `fileName` — file name to show. Must be imported first.

#### Command: `hideGpx()`
Hide GPX file.

*  **Parameters:**
    * `fileName` — file name to hide.

#### Command: `getActiveGpx()`
Get list of active GPX files.

 * **Returns:** `List<ASelectedGpxFile>` — list of active gpx files.

#### Command: `removeGpx()`
Remove GPX file.

*  **Parameters:**
    * `fileName` — file name to remove.


#### Command: `getImportedGpx()`
Method to get list of gpx files currently registered (imported or created) in OsmAnd.

 * **Returns:** `List<AGpxFile>` — list of GPX files 


#### Command: `getSqliteDbFiles()`
Method to get list of sqlitedb files registered in OsmAnd.
 
 * **Returns:**`List<ASqliteDbFile>` — list of SqliteDb files


#### Command: `getActiveSqliteDbFiles()`
Method to get list of currently active sqlitedb files.

 * **Returns:**`List<ASqliteDbFile` — list of SqliteDb files


#### Command: `showSqliteDbFile(String fileName)`
Method to show selected sqlitedb file as map overlay.

 *  **Parameters:**
    * `filename` — name of sqlitedb file.


#### Command: `hideSqliteDbFile(String fileName)`
Method to hide sqlitedb file from map overlay.

 *  **Parameters:**
    * `filename` — name of sqlitedb file.
    
#### Command: `getBitmapForGpx()`
Requests bitmap snap-shot of map with GPX file from provided URI in its center.
You can set bitmap size, density and GPX lines color, but you need
to manually download appropriate map in OsmAnd or background will be empty.
Bitmap will be returned through callback {@link IOsmAndAidlCallback#onGpxBitmapCreated(AGpxBitmap)}

 *  **Parameters:**
     * `gpxUri` — Uri for gpx file;
     * `density` — image density. Recommended to use default metrics for device's display;
     * `widthPixels` — width of bitmap;
     * `heightPixels` — height of bitmap;
     * `color` — color in ARGB format;
     * `callback` — instance of callback from OsmAnd.

#### Command: `getGpxColor()`
Method to get color name for gpx.

 *  **Parameters:**
     * `fileName` — name of gpx file;
     * `gpxColor` — color name of gpx. Can be one of: "red", "orange", "lightblue", "blue", 
     "purple", "translucent_red", "translucent_orange", "translucent_lightblue", 
     "translucent_blue", "translucent_purple", 
     Which used in {@link #importGpx(in ImportGpxParams params) importGpx}, 
     Or color hex if gpx has custom color.

#### Command: `copyFile(CopyFileParams params)`
Method to copy and open files to OsmAnd part by part. !!!Parts size (bytearray) should not exceed 256k.

*  **Parameters:**
    * `params` — contains String fileName, byte[] filePartData, long startTime, boolean done.

* **Returns:** `int` — number of last successfully received file part or error(-1).


#### Command: `setMapLocation()`
Set map view to current location.

 *  **Parameters:**
     * `latitude` — latitude of new map center.
     * `longitude` — longitude of new map center.
     * `zoom` — map zoom level. Set 0 to keep zoom unchanged.
     * `animated` — set true to animate changes.

#### Command: `startGpxRecording()`
Start gpx recording.

#### Command: `stopGpxRecording()`
Stop gpx recording.


#### Command: `takePhotoNote()`
Take photo note.
AudioVideoNotesPlugin must be enabled for taking photo notes.

*  **Parameters:**
    * `lat` — latitude of photo note;
    * `lon` — longitude of photo note.

#### Command: `startVideoRecording()`
Start video note recording.
AudioVideoNotesPlugin must be enabled for taking video notes.

*  **Parameters:**
    * `lat` — latitude of video note;
    * `lon` — longitude of video note.

#### Command: `startAudioRecording()`
Start audio note recording.
AudioVideoNotesPlugin must be enabled for taking audio notes.

*  **Parameters:**
    * `lat` — latitude of audio note;
    * `lon` — longitude of audio note.

#### Command: `stopRecording()`
Stop Audio/Video recording.


#### Command: `navigate()`
Start navigation.

*  **Parameters:**
    * `startName` — name of the start point as it displays in OsmAnd's UI. Nullable;
    * `startLat` — latitude of the start point. If 0 — current location is used;
    * `startLon` — longitude of the start point. If 0 — current location is used;
    * `destName` — name of the start point as it displays in OsmAnd's UI;
    * `destLat` — latitude of a destination point;
    * `destLon` — longitude of a destination point;
    * `profile` — One of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck". Nullable (default);
    * `force` — ask to stop current navigation if any. False — ask. True — don't ask.
    
#### Command: `navigateGpx()`
Start navigation using gpx file. User need to grant Uri permission to OsmAnd.

*  **Parameters:**
    * `gpxUri` — URI created by FileProvider;
    * `force` — ask to stop current navigation if any. False — ask. True — don't ask.

#### Command: `pauseNavigation()`
Put navigation on pause.

#### Command: `resumeNavigation()`
Resume navigation if it was paused before.

#### Command: `stopNavigation()`
Stop navigation. Removes target / intermediate points and route path from the map.

#### Command: `muteVoiceGuidance()`
Mute voice guidance. Stays muted until unmute manually or via the api.

#### Command: `unmuteVoiceGuidance()`
Unmute voice guidance.

#### Command: `search()`
Run search for POI / Address.

*  **Parameters:**
    * `searchQuery` — search query string.
    * `searchType` — type of search. Values: 
    SearchParams.SEARCH_TYPE_ALL — all kind of search; 
    SearchParams.SEARCH_TYPE_POI — POIs only; 
    SearchParams.SEARCH_TYPE_ADDRESS — addresses only.
    * `latitude` — latitude of original search location;
    * `longitude` — longitude of original search location;
    * `radiusLevel` — value from 1 to 7. Default value = 1;
    * `totalLimit` — limit of returned search result rows. Default value = -1 (unlimited).

#### Command: `navigateSearch()`
Do search and start navigation.

*  **Parameters:**
    * `startName` — name of the start point as it displays in OsmAnd's UI. Nullable;
    * `startLat` — latitude of the start point. If 0 - current location is used;
    * `startLon` — longitude of the start point. If 0 - current location is used;
    * `searchQuery` — Text of a query for searching a destination point. Sent as URI parameter;
    * `searchLat` — original location of search (latitude). Sent as URI parameter;
    * `searchLon` — original location of search (longitude). Sent as URI parameter;
    * `profile` — one of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck". Nullable (default);
    * `force` — ask to stop current navigation if any. False - ask. True - don't ask.

#### Command: `registerForVoiceRouterMessages()`
Method to register for Voice Router voice messages during navigation. Notifies user about voice messages.

*  **Parameters:**
    * `subscribeToUpdates` — boolean flag to subscribe or unsubscribe from messages;
    * `callbackId` — id of callback, needed to unsubscribe from messages;
    * `callback` — callback to notify user on voice message;
* **Returns:** `long` — id of callback in OsmAnd. Needed to unsubscribe from messages.

#### Command: `registerForUpdates()`
Method to register for periodical callbacks from OsmAnd

*  **Parameters:**
    * `updateTimeMS` — period of time in millisecond after which callback is triggered
    * `callback` — create and provide instance of {@link IOsmAndAidlCallback} interface
    * `id` — id of callback in OsmAnd. Needed to unsubscribe from updates.

#### Command: `unregisterFromUpdates()`
Method to unregister from periodical callbacks from OsmAnd

*  **Parameters:**
    * `callbackId` — id of registered callback (provided by OsmAnd in {@link OsmAndAidlHelper#registerForUpdates(long, IOsmAndAidlCallback)})

#### Command: `registerForNavigationUpdates()`
Method to register for updates during navgation. Notifies user about distance to the next turn and its type.

*  **Parameters:**
    * `subscribeToUpdates` — subscribe or unsubscribe from updates;
    * `callbackId` — id of callback, needed to unsubscribe from updates;
    * `callback` — callback to notify user on navigation data change;
* **Returns:** `long` — id of callback in OsmAnd. Needed to unsubscribe from updates.

#### Command: `setNavDrawerItems()`
Method for adding up to 3 items to the OsmAnd navigation drawer.

*  **Parameters:**
    * `appPackage` — current application package;
    * `names` — list of names for items;
    * `uris` — list of uris for intents;
    * `iconNames` — list of icon names for items;
    * `flags` — list of flags for intents. Use -1 if no flags needed.

#### Command: `setEnabledIds(List<String> ids)`
Method for selected UI elements (like QuickSearch button) to show.

* **Parameters:** 
   *`ids` — list of menu items keys from {@link OsmAndCustomizationConstants}.


#### Command: `setDisabledIds(List<String> ids)`
Method for selected UI elements (like QuickSearch button) to hide.

 * **Parameters:** 
   * `ids` — list of menu items keys from {@link OsmAndCustomizationConstants}.


#### Command: `setEnabledPatterns(List<String> ids)`
Method to show selected NavDrawer's menu items.

 * **Parameters:** 
   * `ids — list of drawer' menu items {@link OsmAndCustomizationConstants}`.


#### Command: `setDisabledPatterns(List<String> ids)`
Method to hide selected NavDrawer's menu items.

 * **Parameters:**
   * `ids` — list of drawer' menu items {@link OsmAndCustomizationConstants}`.


#### Command: `regWidgetVisibility(String widgetKey, @Nullable List<String> appModKeys)`
Register OsmAnd widgets for visibility. 

 * **Parameters:**
   * `widgetKey` — widget id;
   * `appModKeys` — list of modes widget active with. Could be "null" for all modes.


#### Command: `regWidgetAvailability(String widgetKey, @Nullable List<String> appModKeys)`
Register OsmAnd widgets for availability.

 * **Parameters:**
   * `widgetKey` — widget id;
   * `appModKeys` — list of modes widget active with. Could be "null" for all modes.
 
 
#### Command: `customizeOsmandSettings(String sharedPrefsName, Bundle bundle)`
Add custom parameters for OsmAnd settings to associate with client app.
 
 * **Parameters:**
    * `sharedPreferencesName` - string with name of clint's app for shared preferences key;
    * `bundle` - bundle with keys from Settings IDs {@link OsmAndCustomizationConstants} and Settings params.


#### Command: `setNavDrawerLogoWithParams(NavDrawHeaderParams params)`
Method for adding image to the top of Osmand's NavDrawer. Image shouldn't be larger than 600px*160px

**Parameters:** 
   * `imageUri` — image's URI.toString;
   * `packageName` — client's app package name;
   * `intent` — intent for additional functionality on image click.


#### Command: `setNavDrawerFooterWithParams(NavDrawerFooterParams params)`
Method for adding functionality to NavDrawer's footer (option to reset OsmAnd settings to previous state)

* **Parameters:** 
   * `packageName` — package name;
   * `intent` — intent;
   * `appName` — client's app name.

#### Command: `restoreOsmand(String pluginId, int newState)`
Restore default OsmAnd settings and state: clears settings, widget and menu customization.

#### Command: `changePluginState()`
Method to change state of plug-ins in OsmAnd

*  **Parameters:**
    * `pluginId` — id (name) of plugin;
    * `newState` — new state (0 - off, 1 - on).

#### Command: `addMapWidget()`
Add map widget to the right side of the main screen.
Note: any specified icon should exist in OsmAnd app resources.

*  **Parameters:**
    * `id` — widget id;
    * `menuTitle` — widget name (configure map menu);
    * `lightIconName` — icon name for the light theme (widget);
    * `darkIconName` — icon name for the dark theme (widget);
    * `text` — main widget text;
    * `description` — sub text, like "km/h";
    * `order` — order position in the widgets list;
    * `intentOnClick` — onClick intent. Called after click on widget as startActivity(Intent intent).

#### Command: `removeMapWidget()`
Remove map widget.

*  **Parameters:**
    * `id` — widget id.

#### Command: `updateMapWidget()`
Update map widget.
Note: any specified icon should exist in OsmAnd app resources.

*  **Parameters:**
    * `id` — widget id;
    * `menuTitle` — widget name (configure map menu);
    * `lightIconName` — icon name for the light theme (widget);
    * `darkIconName` — icon name for the dark theme (widget);
    * `text` — main widget text;
    * `description` — sub text, like "km/h";
    * `order` — order position in the widgets list;
    * `intentOnClick` — onClick intent. Called after click on widget as startActivity(Intent intent).

#### Command: `registerForOsmandInitListener()`
Method to register for callback on OsmAnd initialization.

*  **Parameters:**
    * `callback` — create and provide instance of {@link IOsmAndAidlCallback} interface.
    
#### Command: `addContextMenuButtons()`
Method to add Context Menu buttons to OsmAnd Context menu.

*  **Parameters:**
    * `callback` — create and provide instance of {@link IOsmAndAidlCallback} interface;
    * `leftButton` — parameters for left context button;
    * `buttonId` — id of button in View;
    * `leftTextCaption` — left-side button text;
    * `rightTextCaption` — right-side button text;
    * `leftIconName` — name of left-side icon;
    * `rightIconName` — name of right-side icon;
    * `needColorizeIcon` — flag to apply color to icon;
    * `enabled` — enable button flag;
    * `rightButton` — parameters for right context button, see <i>leftButton</i> param for details;
    * `id` — button id;
    * `appPackage` — clinet's app package name;
    * `layerId` — id of Osmand's map layer;
    * `callbackId` — {@link IOsmAndAidlCallback} id;
    * `pointsIds` — list of point Ids to which this rules applies to;
    * `callback` — AIDL callback.
* **Returns:**`long` — callback's Id.

#### Command: `removeContextMenuButtons()`
Method to remove Context Menu buttons from OsmAnd Context menu.

*  **Parameters:**
    * `paramsId` — id of {@link ContextMenuButtonsParams} of button you want to remove;
    * `callbackId` — id of {@ling IOsmAndAidlCallback} of button you want to remove.


#### Command: `updateContextMenuButtons()`
Method to update params on already set custom Context Button.

*  **Parameters:**
    * `callback` — create and provide instance of {@link IOsmAndAidlCallback} interface;
    * `leftButton` — parameters for left context button;
    * `buttonId` — id of button in View;
    * `leftTextCaption` — left-side button text;
    * `rightTextCaption` — right-side button text;
    * `leftIconName` — name of left-side icon;
    * `rightIconName` — name of right-side icon;
    * `needColorizeIcon` — flag to apply color to icon;
    * `enabled` — enable button flag;
    * `rightButton` — parameters for right context button, see <i>leftButton</i> param for details;
    * `id` — button id;
    * `appPackage` — clinet's app package name;
    * `layerId` — id of Osmand's map layer;
    * `callbackId` — {@link IOsmAndAidlCallback} id;
    * `pointsIds` — list of point Ids to which this rules applies to;
    * `callback` — AIDL callback.


#### Command: `areOsmandSettingsCustomized()`
Method to check if there is a customized setting in OsmAnd Settings.

*  **Parameters:**
    * `sharedPreferencesName` — key of setting in OsmAnd's preferences;
    * **Returns:**`boolean` — true if setting is already set in SharedPreferences.

#### Command: `setCustomization()`
Method to customize parameters of OsmAnd.

*  **Parameters:**
     * `settingsParams` — wrapper class for OsmAnd shared preferences params. See <i>{@link #customizeOsmandSettings(in OsmandSettingsParams params) customizeOsmandSettings}</i> method description for details;
     * `navDrawerHeaderParams` — wrapper class for OsmAnd navdrawer header params. See <i>{@link #setNavDrawerLogoWithParams(in NavDrawerHeaderParams params) setNavDrawerLogoWithParams}</i> method description for details;
     * `navDrawerFooterParams` — wrapper class for OsmAnd navdrawer footer params. See <i>{@link #setNavDrawerFooterWithParams(in NavDrawerFooterParams params) setNavDrawerFooterWithParams}</i> method description for details;
     * `navDrawerItemsParams` — wrapper class for OsmAnd navdrawer items params.See <i>{@link #setNavDrawerItems(in SetNavDrawerItemsParams params) setNavDrawerItems}</i> method description for details;
     * `visibilityWidgetsParams` — wrapper class for OsmAnd widgets visibility. See <i>{@link #regWidgetVisibility(in SetWidgetsParams params) regWidgetVisibility}</i> method description for details;
     * `availabilityWidgetsParams` — wrapper class for OsmAnd widgets availability. See <i>{@link #regWidgetAvailability(in SetWidgetsParams params) regWidgetAvailability}</i> method description for details;
     * `pluginsParams` — wrapper class for OsmAnd plugins states params.See <i>{@link #changePluginState(in PluginParams params) changePluginState}</i> method description for details;
     * `featuresEnabledIds` — list of UI elements (like QuickSearch button) to show. See <i>{@link #setEnabledIds(in List<String> ids) setEnabledIds}</i>;
     * `featuresDisabledIds` — list of UI elements (like QuickSearch button) to hide. See <i>{@link #setDisabledIds(in List<String> ids) setDisabledIds}</i>;
     * `featuresEnabledPatterns` — list of NavDrawer menu items to show. See <i>{@link #setEnabledPatterns(in List<String> patterns) setEnabledPatterns}</i>;
     * `featuresDisabledPatterns` — list of NavDrawer menu items to hide. See <i>{@link #setDisabledPatterns(in List<String> patterns) setDisabledPatterns}</i>.