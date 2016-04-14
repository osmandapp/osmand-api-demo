package net.osmand.osmandapidemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class OsmAndHelper {
	private static final String PREFIX = "osmand.api://";

	// Information
	private static final String GET_INFO = "get_info";

	// Related to recording media
	private static final String RECORD_AUDIO = "record_audio";
	private static final String RECORD_VIDEO = "record_video";
	private static final String RECORD_PHOTO = "record_photo";
	private static final String STOP_AV_REC = "stop_av_rec";

	private static final String ADD_FAVORITE = "add_favorite";
	private static final String ADD_MAP_MARKER = "add_map_marker";

	private static final String SHOW_GPX = "show_gpx";
	private static final String NAVIGATE_GPX = "navigate_gpx";

	private static final String NAVIGATE = "navigate";

	private static final String START_GPX_REC = "start_gpx_rec";
	private static final String STOP_GPX_REC = "stop_gpx_rec";

	// Parameters
	public static final String API_CMD_SUBSCRIBE_VOICE_NOTIFICATIONS = "subscribe_voice_notifications";

	public static final String PARAM_NAME = "name";
	public static final String PARAM_DESC = "desc";
	public static final String PARAM_CATEGORY = "category";
	public static final String PARAM_LAT = "lat";
	public static final String PARAM_LON = "lon";
	public static final String PARAM_COLOR = "color";
	public static final String PARAM_VISIBLE = "visible";

	public static final String PARAM_PATH = "path";
	public static final String PARAM_DATA = "data";
	public static final String PARAM_FORCE = "force";

	public static final String PARAM_START_NAME = "start_name";
	public static final String PARAM_DEST_NAME = "dest_name";
	public static final String PARAM_START_LAT = "start_lat";
	public static final String PARAM_START_LON = "start_lon";
	public static final String PARAM_DEST_LAT = "dest_lat";
	public static final String PARAM_DEST_LON = "dest_lon";
	public static final String PARAM_PROFILE = "profile";

	public static final String PARAM_ETA = "eta";
	public static final String PARAM_TIME_LEFT = "time_left";
	public static final String PARAM_DISTANCE_LEFT = "time_distance_left";

	private final int mRequestCode;
	private final Activity mActivity;

	public OsmAndHelper(Activity activity, int requestCode) {
		this.mRequestCode = requestCode;
		mActivity = activity;
	}

	public void getInfo() {
		// test get info
		Uri uri = Uri.parse(getUriString(GET_INFO).toString());
		sendRequest(uri);
	}

	public void recordAudio(String lat, String lon) {
		// test record audio
		Uri uri = Uri.parse(getUriString(RECORD_AUDIO).append("?lat=").append(lat)
				.append("&lon=").append(lon).toString());
		sendRequest(uri);
	}
	public void recordVideo(String lat, String lon) {
		// test record video
		Uri uri = Uri.parse(getUriString(RECORD_VIDEO).append("?lat=").append(lat)
				.append("&lon=").append(lon).toString());
		sendRequest(uri);
	}
	public void recordPhoto(String lat, String lon) {
		// test record photo
		Uri uri = Uri.parse(getUriString(RECORD_PHOTO).append("?lat=").append(lat)
				.append("&lon=").append(lon).toString());
		sendRequest(uri);
	}
	public void stopAvRec() {
		// test stop recording
		Uri uri = Uri.parse(getUriString(STOP_AV_REC).toString());
		sendRequest(uri);
	}

	public void addMapMarker(String lat, String lon) {
		// test marker
		Uri uri = Uri.parse(getUriString(ADD_MAP_MARKER).append("?lat=").append(lat)
				.append("&lon=").append(lon).append("&name=Marker").toString());
		sendRequest(uri);
	}

	public void addFavorite(String lat, String lon) {
		// test favorite
		Uri uri = Uri.parse(getUriString(ADD_FAVORITE).append("?lat=").append(lat)
				.append("&lon=").append(lon).append("&name=Favorite&desc=Description&category=test2&color=red&visible=true").toString());
		sendRequest(uri);
	}

	public void startGpxRec() {
		// test start gpx recording
		Uri uri = Uri.parse(getUriString(START_GPX_REC).toString());
		sendRequest(uri);
	}

	public void stopGpxRec() {
		// test stop gpx recording
		Uri uri = Uri.parse(getUriString(STOP_GPX_REC).toString());
		sendRequest(uri);
	}

	public void showGpx() {
		// test show gpx (path)
		//File gpx = new File(app.getAppPath(IndexConstants.GPX_INDEX_DIR), gpxName);
		//uri = Uri.parse("osmand.api://show_gpx?path=" + URLEncoder.encode(gpx.getAbsolutePath(), "UTF-8"));

		// test show gpx (data)
		Uri uri = Uri.parse(getUriString(SHOW_GPX).toString());
		sendRequest(uri);
		//intent.putExtra("data", AndroidUtils.getFileAsString(
		//		new File(app.getAppPath(IndexConstants.GPX_INDEX_DIR), gpxName)));
	}

	public void navigateGpx() {
		// test navigate gpx (path)
		//File gpx = new File(app.getAppPath(IndexConstants.GPX_INDEX_DIR), gpxName);
		//uri = Uri.parse("osmand.api://navigate_gpx?force=true&path=" + URLEncoder.encode(gpx.getAbsolutePath(), "UTF-8"));

		// test navigate gpx (data)
		Uri uri = Uri.parse(getUriString(NAVIGATE_GPX).append("?force=true").toString());
		sendRequest(uri);
		//intent.putExtra("data", AndroidUtils.getFileAsString(
		//		new File(app.getAppPath(IndexConstants.GPX_INDEX_DIR), gpxName)));
	}

	public void navigate(Double startLat, Double startLon, Double destLat, Double destLon) {
		// test navigate
		Uri uri = Uri.parse(getUriString(NAVIGATE).append("?start_lat=").append(startLat)
				.append("&start_lon=").append(startLon)
				.append("&start_name=Start")
				.append("&dest_lat=").append(destLat)
				.append("&dest_lon=").append(destLon)
				.append("&dest_name=Finish").append("&profile=bicycle").toString());
		sendRequest(uri);
	}

	private StringBuilder getUriString(String command){
		StringBuilder stringBuilder = new StringBuilder(PREFIX);
		stringBuilder.append(command);
		return stringBuilder;
	}

	private void sendRequest(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		mActivity.startActivityForResult(intent, mRequestCode);
	}
}
