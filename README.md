# osmand-api-demo
Example of usage OsmAnd API

The simplest way to integrate with OsmAnd intent API is to copy
OsmAndHelper.java into your project, and call it's methods.

You can create URI yourself but it is suggested to use constants 
from OsmApiHelper anyway.

#### Command: GET_INFO
Simply requests data about OsmAnd status.
Data returned as extras. Each key value pair as separate entity.

#### Command: RECORD_AUDIO
Request to start recording audio note for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

##### URI parameter: PARAM_LAT - latitude.
##### URI parameter: PARAM_LON - longitude.

#### Command: RECORD_VIDEO
Request to start recording video note for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

##### URI parameter: PARAM_LAT - latitude.
##### URI parameter: PARAM_LON - longitude.

#### Command: RECORD_PHOTO
Request to take photo for given location.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.

##### URI parameter: PARAM_LAT - latitude.
##### URI parameter: PARAM_LON - longitude.

#### Command: STOP_AV_REC
Stop recording audio or video.
Audio video notes plugin must be enabled. Otherwise OsmAnd will return
RESULT_CODE_ERROR_PLUGIN_INACTIVE.


#### Command: ADD_MAP_MARKER
Add map marker at given location.

##### URI parameter: PARAM_LAT - latitude.
##### URI parameter: PARAM_LON - longitude.
##### URI parameter: PARAM_NAME - name.
 
#### Command: ADD_FAVORITE
Add favourite at given location

##### URI parameter: PARAM_LAT - latitude.
##### URI parameter: PARAM_LON - longitude.
##### URI parameter: PARAM_NAME - name of favourite item.
##### URI parameter: PARAM_DESC - description of favourite item.
##### URI parameter: PARAM_CATEGORY - category of favourite item. Symbols that are not safe for directory name will be removed.
##### URI parameter: PARAM_COLOR - color of favourite item. Can be one of: "red", "orange", "yellow", "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown".
##### URI parameter: PARAM_VISIBLE - should favourite item be visible after creation.

#### Command: START_GPX_REC
Start recording GPX track.
##### URI parameter: PARAM_CLOSE_AFTER_COMMAND - true if OsmAnd should be close immediately after executing command.

#### Command: STOP_GPX_REC
Stop recording GPX track.

##### URI parameter: PARAM_CLOSE_AFTER_COMMAND - true if OsmAnd should be close immediately after executing command.

#### Command: SHOW_GPX
Show GPX file on map.
OsmAnd must have rights to access location. Not recommended.

##### URI parameter: PARAM_PATH - path to file

#### Command: SHOW_GPX
Show GPX file on map.
In current implementation it is recommended way to share file if your app supports API 15.

##### Extra parameter: PARAM_DATA Raw contents of GPX file. Sent as intent's extra string parameter.

#### Command: SHOW_GPX
@TargetApi(16)
Show GPX file on map.
Recommended way to share file.
In current implementation it is recommended way to share file if your app supports API 16
and above.
URI created by FileProvider must be sent as ClipData

##### URI parameter: PARAM_URI. Must be "true"

#### Command: NAVIGATE_GPX
Navigate GPX file.
OsmAnd must have rights to access location. Not recommended.

##### URI parameter: PARAM_FORCE - Stop previous navigation if active.
##### URI parameter: PARAM_PATH - File which represents GPX track.

#### Command: NAVIGATE_GPX
Navigate GPX file.
In current implementation it is recommended way to share file if your app supports API 15.

##### URI parameter: PARAM_FORCE - Stop previous navigation if active.
##### Extra parameter: PARAM_DATA - Raw contents of GPX file.

#### Command: NAVIGATE_GPX
@TargetApi(16)
Navigate GPX file.
Recommended way to share file.
In current implementation it is recommended way to share file if your app supports API 16
and above.
URI created by FileProvider must be sent as ClipData

##### URI parameter: PARAM_URI must be "true"
##### URI parameter: PARAM_FORCE Stop previous navigation if active.

#### Command: NAVIGATE
Navigate from one location to another.

##### URI parameter: PARAM_START_LAT - Name of starting point.
##### URI parameter: PARAM_START_LON - Start latitude.
##### URI parameter: PARAM_START_NAME - Start longitude.
##### URI parameter: PARAM_DEST_LAT - Name of destination point.
##### URI parameter: PARAM_DEST_LON - Destination latitude.
##### URI parameter: PARAM_DEST_NAME - Destination longitude.
##### URI parameter: PARAM_PROFILE - Map profile can be one of: "default", "car", "bicycle","pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck".
##### URI parameter: PARAM_FORCE - Stop previous navigation if active.
