package main.java.net.osmand.osmandapidemo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import kotlin.jvm.Throws;
import net.osmand.aidl.IOsmAndAidlCallback;
import net.osmand.aidl.IOsmAndAidlInterface;
import net.osmand.aidl.copyfile.CopyFileParams;
import net.osmand.aidl.customization.OsmandSettingsParams;
import net.osmand.aidl.customization.SetWidgetsParams;
import net.osmand.aidl.favorite.AFavorite;
import net.osmand.aidl.favorite.AddFavoriteParams;
import net.osmand.aidl.favorite.RemoveFavoriteParams;
import net.osmand.aidl.favorite.UpdateFavoriteParams;
import net.osmand.aidl.favorite.group.AFavoriteGroup;
import net.osmand.aidl.favorite.group.AddFavoriteGroupParams;
import net.osmand.aidl.favorite.group.RemoveFavoriteGroupParams;
import net.osmand.aidl.favorite.group.UpdateFavoriteGroupParams;
import net.osmand.aidl.gpx.AGpxBitmap;
import net.osmand.aidl.gpx.AGpxFile;
import net.osmand.aidl.gpx.ASelectedGpxFile;
import net.osmand.aidl.gpx.CreateGpxBitmapParams;
import net.osmand.aidl.gpx.HideGpxParams;
import net.osmand.aidl.gpx.ImportGpxParams;
import net.osmand.aidl.gpx.RemoveGpxParams;
import net.osmand.aidl.gpx.ShowGpxParams;
import net.osmand.aidl.gpx.StartGpxRecordingParams;
import net.osmand.aidl.gpx.StopGpxRecordingParams;
import net.osmand.aidl.map.ALatLon;
import net.osmand.aidl.map.SetMapLocationParams;
import net.osmand.aidl.maplayer.AMapLayer;
import net.osmand.aidl.maplayer.AddMapLayerParams;
import net.osmand.aidl.maplayer.RemoveMapLayerParams;
import net.osmand.aidl.maplayer.UpdateMapLayerParams;
import net.osmand.aidl.maplayer.point.AMapPoint;
import net.osmand.aidl.maplayer.point.AddMapPointParams;
import net.osmand.aidl.maplayer.point.RemoveMapPointParams;
import net.osmand.aidl.maplayer.point.ShowMapPointParams;
import net.osmand.aidl.maplayer.point.UpdateMapPointParams;
import net.osmand.aidl.mapmarker.AMapMarker;
import net.osmand.aidl.mapmarker.AddMapMarkerParams;
import net.osmand.aidl.mapmarker.RemoveMapMarkerParams;
import net.osmand.aidl.mapmarker.UpdateMapMarkerParams;
import net.osmand.aidl.mapwidget.AMapWidget;
import net.osmand.aidl.mapwidget.AddMapWidgetParams;
import net.osmand.aidl.mapwidget.RemoveMapWidgetParams;
import net.osmand.aidl.mapwidget.UpdateMapWidgetParams;
import net.osmand.aidl.navdrawer.NavDrawerFooterParams;
import net.osmand.aidl.navdrawer.NavDrawerHeaderParams;
import net.osmand.aidl.navdrawer.NavDrawerItem;
import net.osmand.aidl.navdrawer.SetNavDrawerItemsParams;
import net.osmand.aidl.navigation.ADirectionInfo;
import net.osmand.aidl.navigation.MuteNavigationParams;
import net.osmand.aidl.navigation.NavigateGpxParams;
import net.osmand.aidl.navigation.NavigateParams;
import net.osmand.aidl.navigation.NavigateSearchParams;
import net.osmand.aidl.navigation.PauseNavigationParams;
import net.osmand.aidl.navigation.ResumeNavigationParams;
import net.osmand.aidl.navigation.StopNavigationParams;
import net.osmand.aidl.navigation.UnmuteNavigationParams;
import net.osmand.aidl.note.StartAudioRecordingParams;
import net.osmand.aidl.note.StartVideoRecordingParams;
import net.osmand.aidl.note.StopRecordingParams;
import net.osmand.aidl.note.TakePhotoNoteParams;
import net.osmand.aidl.plugins.PluginParams;
import net.osmand.aidl.search.SearchParams;
import net.osmand.aidl.search.SearchResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.java.net.osmand.osmandapidemo.OsmAndHelper.OnOsmandMissingListener;
import net.osmand.aidl.tiles.ASqliteDbFile;
import org.jetbrains.annotations.Nullable;

public class OsmAndAidlHelper {

	private static final String OSMAND_FREE_PACKAGE_NAME = "net.osmand";
	private static final String OSMAND_PLUS_PACKAGE_NAME = "net.osmand.plus";
	private static final String OSMAND_PACKAGE_NAME = OSMAND_PLUS_PACKAGE_NAME;

	private final Application app;
	private final OnOsmandMissingListener mOsmandMissingListener;
	private IOsmAndAidlInterface mIOsmAndAidlInterface;

	private SearchCompleteListener mSearchCompleteListener;

	private OnUpdateListener onUpdateListener = null;
	private GpxBitmapCreatedListener gpxBitmapCreatedListener = null;

	public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
		this.onUpdateListener = onUpdateListener;
	}

	public void setGpxBitmapCreatedListener(GpxBitmapCreatedListener gpxBitmapCreatedListener) {
		this.gpxBitmapCreatedListener = gpxBitmapCreatedListener;
	}

	interface SearchCompleteListener {
		void onSearchComplete(List<SearchResult> resultSet);
	}

	interface OnUpdateListener {
		void update();
	}

	interface GpxBitmapCreatedListener {
		void onGpxBitmapCreated(AGpxBitmap bitmap);
	}

	private IOsmAndAidlCallback.Stub mIOsmAndAidlCallback = new IOsmAndAidlCallback.Stub() {
		@Override
		public void onSearchComplete(List<SearchResult> resultSet) throws RemoteException {
			if (mSearchCompleteListener != null) {
				mSearchCompleteListener.onSearchComplete(resultSet);
			}
		}

		@Override
		public void onUpdate() throws RemoteException {

		}

		@Override
		public void onAppInitialized() throws RemoteException {

		}

		@Override
		public void onGpxBitmapCreated(AGpxBitmap bitmap) throws RemoteException {

		}

		@Override
		public void updateNavigationInfo(ADirectionInfo directionInfo) throws RemoteException {

		}
	};

	public void setSearchCompleteListener(SearchCompleteListener mSearchCompleteListener) {
		this.mSearchCompleteListener = mSearchCompleteListener;
	}

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mIOsmAndAidlInterface = IOsmAndAidlInterface.Stub.asInterface(service);
			Toast.makeText(app, "OsmAnd service connected", Toast.LENGTH_SHORT).show();
		}
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mIOsmAndAidlInterface = null;
			Toast.makeText(app, "OsmAnd service disconnected", Toast.LENGTH_SHORT).show();
		}
	};

	public OsmAndAidlHelper(Application application, OnOsmandMissingListener listener) {
		this.app = application;
		this.mOsmandMissingListener = listener;
		bindService();
	}

	private boolean bindService() {
		if (mIOsmAndAidlInterface == null) {
			Intent intent = new Intent("net.osmand.aidl.OsmandAidlService");
			intent.setPackage(OSMAND_PACKAGE_NAME);
			boolean res = app.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
			if (res) {
				Toast.makeText(app, "OsmAnd service bind", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				Toast.makeText(app, "OsmAnd service NOT bind", Toast.LENGTH_SHORT).show();
				mOsmandMissingListener.osmandMissing();
				return false;
			}
		} else {
			return true;
		}
	}

	public void cleanupResources() {
		if (mIOsmAndAidlInterface != null) {
			app.unbindService(mConnection);
		}
	}

	/**
	 * Refresh the map (UI)
	 */
	public boolean refreshMap() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.refreshMap();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add favorite group with given params.
	 *
	 * @param name    - group name.
	 * @param color   - group color. Can be one of: "red", "orange", "yellow",
	 *                "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown".
	 * @param visible - group visibility.
	 */
	public boolean addFavoriteGroup(String name, String color, boolean visible) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavoriteGroup favoriteGroup = new AFavoriteGroup(name, color, visible);
				return mIOsmAndAidlInterface.addFavoriteGroup(new AddFavoriteGroupParams(favoriteGroup));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update favorite group with given params.
	 *
	 * @param namePrev    - group name (current).
	 * @param colorPrev   - group color (current).
	 * @param visiblePrev - group visibility (current).
	 * @param nameNew     - group name (new).
	 * @param colorNew    - group color (new).
	 * @param visibleNew  - group visibility (new).
	 */
	public boolean updateFavoriteGroup(String namePrev, String colorPrev, boolean visiblePrev,
									   String nameNew, String colorNew, boolean visibleNew) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavoriteGroup favoriteGroupPrev = new AFavoriteGroup(namePrev, colorPrev, visiblePrev);
				AFavoriteGroup favoriteGroupNew = new AFavoriteGroup(nameNew, colorNew, visibleNew);
				return mIOsmAndAidlInterface.updateFavoriteGroup(new UpdateFavoriteGroupParams(favoriteGroupPrev, favoriteGroupNew));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove favorite group with given name.
	 *
	 * @param name - name of favorite group.
	 */
	public boolean removeFavoriteGroup(String name) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavoriteGroup favoriteGroup = new AFavoriteGroup(name, "", false);
				return mIOsmAndAidlInterface.removeFavoriteGroup(new RemoveFavoriteGroupParams(favoriteGroup));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add favorite at given location with given params.
	 *
	 * @param lat         - latitude.
	 * @param lon         - longitude.
	 * @param name        - name of favorite item.
	 * @param description - description of favorite item.
	 * @param category    - category of favorite item.
	 * @param color       - color of favorite item. Can be one of: "red", "orange", "yellow",
	 *                    "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown".
	 * @param visible     - should favorite item be visible after creation.
	 */
	public boolean addFavorite(double lat, double lon, String name, String description,
							   String category, String color, boolean visible) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavorite favorite = new AFavorite(lat, lon, name, description, category, color, visible);
				return mIOsmAndAidlInterface.addFavorite(new AddFavoriteParams(favorite));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update favorite at given location with given params.
	 *
	 * @param latPrev        - latitude (current favorite).
	 * @param lonPrev        - longitude (current favorite).
	 * @param namePrev       - name of favorite item (current favorite).
	 * @param categoryPrev   - category of favorite item (current favorite).
	 * @param latNew         - latitude (new favorite).
	 * @param lonNew         - longitude (new favorite).
	 * @param nameNew        - name of favorite item (new favorite).
	 * @param descriptionNew - description of favorite item (new favorite).
	 * @param categoryNew    - category of favorite item (new favorite). Use only to create a new category,
	 *                       not to update an existing one. If you want to  update an existing category,
	 *                       use the {@link #updateFavoriteGroup(String, String, boolean, String, String, boolean)} method.
	 * @param colorNew       - color of new category. Can be one of: "red", "orange", "yellow",
	 *                       "lightgreen", "green", "lightblue", "blue", "purple", "pink", "brown".
	 * @param visibleNew     - should new category be visible after creation.
	 */
	public boolean updateFavorite(double latPrev, double lonPrev, String namePrev, String categoryPrev,
								  double latNew, double lonNew, String nameNew, String descriptionNew,
								  String categoryNew, String colorNew, boolean visibleNew) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavorite favoritePrev = new AFavorite(latPrev, lonPrev, namePrev, "", categoryPrev, "", false);
				AFavorite favoriteNew = new AFavorite(latNew, lonNew, nameNew, descriptionNew, categoryNew, colorNew, visibleNew);
				return mIOsmAndAidlInterface.updateFavorite(new UpdateFavoriteParams(favoritePrev, favoriteNew));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove favorite at given location with given params.
	 *
	 * @param lat      - latitude.
	 * @param lon      - longitude.
	 * @param name     - name of favorite item.
	 * @param category - category of favorite item.
	 */
	public boolean removeFavorite(double lat, double lon, String name, String category) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AFavorite favorite = new AFavorite(lat, lon, name, "", category, "", false);
				return mIOsmAndAidlInterface.removeFavorite(new RemoveFavoriteParams(favorite));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add map marker at given location.
	 *
	 * @param lat  - latitude.
	 * @param lon  - longitude.
	 * @param name - name.
	 */
	public boolean addMapMarker(double lat, double lon, String name) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker marker = new AMapMarker(new ALatLon(lat, lon), name);
				return mIOsmAndAidlInterface.addMapMarker(new AddMapMarkerParams(marker));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update map marker at given location with name.
	 *
	 * @param latPrev  - latitude (current marker).
	 * @param lonPrev  - longitude (current marker).
	 * @param namePrev - name (current marker).
	 * @param latNew  - latitude (new marker).
	 * @param lonNew  - longitude (new marker).
	 * @param nameNew - name (new marker).
	 */
	public boolean updateMapMarker(double latPrev, double lonPrev, String namePrev,
								   double latNew, double lonNew, String nameNew) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker markerPrev = new AMapMarker(new ALatLon(latPrev, lonPrev), namePrev);
				AMapMarker markerNew = new AMapMarker(new ALatLon(latNew, lonNew), nameNew);
				return mIOsmAndAidlInterface.updateMapMarker(new UpdateMapMarkerParams(markerPrev, markerNew));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove map marker at given location with name.
	 *
	 * @param lat  - latitude.
	 * @param lon  - longitude.
	 * @param name - name.
	 */
	public boolean removeMapMarker(double lat, double lon, String name) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker marker = new AMapMarker(new ALatLon(lat, lon), name);
				return mIOsmAndAidlInterface.removeMapMarker(new RemoveMapMarkerParams(marker));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add map widget to the right side of the main screen.
	 * Note: any specified icon should exist in OsmAnd app resources.
	 *
	 * @param id - widget id.
	 * @param menuIconName - icon name (configure map menu).
	 * @param menuTitle - widget name (configure map menu).
	 * @param lightIconName - icon name for the light theme (widget).
	 * @param darkIconName - icon name for the dark theme (widget).
	 * @param text - main widget text.
	 * @param description - sub text, like "km/h".
	 * @param order - order position in the widgets list.
	 * @param intentOnClick - onClick intent. Called after click on widget as startActivity(Intent intent).
	 */
	public boolean addMapWidget(String id, String menuIconName, String menuTitle,
								String lightIconName, String darkIconName, String text, String description,
								int order, Intent intentOnClick) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapWidget widget = new AMapWidget(id, menuIconName, menuTitle, lightIconName,
						darkIconName, text, description, order, intentOnClick);
				return mIOsmAndAidlInterface.addMapWidget(new AddMapWidgetParams(widget));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update map widget.
	 * Note: any specified icon should exist in OsmAnd app resources.
	 *
	 * @param id - widget id.
	 * @param menuIconName - icon name (configure map menu).
	 * @param menuTitle - widget name (configure map menu).
	 * @param lightIconName - icon name for the light theme (widget).
	 * @param darkIconName - icon name for the dark theme (widget).
	 * @param text - main widget text.
	 * @param description - sub text, like "km/h".
	 * @param order - order position in the widgets list.
	 * @param intentOnClick - onClick intent. Called after click on widget as startActivity(Intent intent).
	 */
	public boolean updateMapWidget(String id, String menuIconName, String menuTitle,
								String lightIconName, String darkIconName, String text, String description,
								int order, Intent intentOnClick) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapWidget widget = new AMapWidget(id, menuIconName, menuTitle, lightIconName,
						darkIconName, text, description, order, intentOnClick);
				return mIOsmAndAidlInterface.updateMapWidget(new UpdateMapWidgetParams(widget));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove map widget.
	 *
	 * @param id - widget id.
	 */
	public boolean removeMapWidget(String id) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.removeMapWidget(new RemoveMapWidgetParams(id));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add user layer on the map.
	 *
	 * @param id - layer id.
	 * @param name - layer name.
	 * @param zOrder - z-order position of layer. Default value is 5.5f
	 * @param points - initial list of points. Nullable.
	 * @param imagePoints - use new style for points on map or not. Also default zoom bounds for new style can be edited.
	 */
	public boolean addMapLayer(String id, String name, float zOrder, List<AMapPoint> points, boolean imagePoints) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapLayer layer = new AMapLayer(id, name, zOrder, points);
				layer.setImagePoints(imagePoints);
				return mIOsmAndAidlInterface.addMapLayer(new AddMapLayerParams(layer));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update user layer.
	 *
	 * @param id - layer id.
	 * @param name - layer name.
	 * @param zOrder - z-order position of layer. Default value is 5.5f
	 * @param points - list of points. Nullable.
	 * @param imagePoints - use new style for points on map or not. Also default zoom bounds for new style can be edited.
	 */
	public boolean updateMapLayer(String id, String name, float zOrder, List<AMapPoint> points, boolean imagePoints) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapLayer layer = new AMapLayer(id, name, zOrder, points);
				layer.setImagePoints(imagePoints);
				return mIOsmAndAidlInterface.updateMapLayer(new UpdateMapLayerParams(layer));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove user layer.
	 *
	 * @param id - layer id.
	 */
	public boolean removeMapLayer(String id) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.removeMapLayer(new RemoveMapLayerParams(id));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Show AMapPoint on map in OsmAnd.
	 *
	 * @param layerId   - layer id. Note: layer should be added first.
	 * @param pointId   - point id.
	 * @param shortName - short name (single char). Displayed on the map.
	 * @param fullName  - full name. Displayed in the context menu on first row.
	 * @param typeName  - type name. Displayed in context menu on second row.
	 * @param color     - color of circle's background.
	 * @param location  - location of the point.
	 * @param details   - list of details. Displayed under context menu.
	 * @param params    - optional map of params for point.
	 */
	public boolean showMapPoint(String layerId, String pointId, String shortName, String fullName,
								String typeName, int color, ALatLon location, List<String> details,
								Map<String, String> params) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapPoint point = new AMapPoint(pointId, shortName, fullName, typeName, color, location, details, params);
				return mIOsmAndAidlInterface.showMapPoint(new ShowMapPointParams(layerId, point));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add point to user layer.
	 *
	 * @param layerId - layer id. Note: layer should be added first.
	 * @param pointId - point id.
	 * @param shortName - short name (single char). Displayed on the map.
	 * @param fullName - full name. Displayed in the context menu on first row.
	 * @param typeName - type name. Displayed in context menu on second row.
	 * @param color - color of circle's background.
	 * @param location - location of the point.
	 * @param details - list of details. Displayed under context menu.
	 * @param params - optional map of params for point.
	 */
	public boolean addMapPoint(String layerId, String pointId, String shortName, String fullName,
							   String typeName, int color, ALatLon location, List<String> details,
							   Map<String, String> params) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapPoint point = new AMapPoint(pointId, shortName, fullName, typeName, color, location, details, params);
				return mIOsmAndAidlInterface.addMapPoint(new AddMapPointParams(layerId, point));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Update point.
	 *
	 * @param layerId - layer id.
	 * @param pointId - point id.
	 * @param shortName - short name (single char). Displayed on the map.
	 * @param fullName - full name. Displayed in the context menu on first row.
	 * @param typeName - type name. Displayed in context menu on second row.
	 * @param color - color of circle's background.
	 * @param location - location of the point.
	 * @param details - list of details. Displayed under context menu.
	 * @param params - optional map of params for point.
	 */
	public boolean updateMapPoint(String layerId, String pointId, String shortName, String fullName,
								  String typeName, int color, ALatLon location, List<String> details,
								  Map<String, String> params) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapPoint point = new AMapPoint(pointId, shortName, fullName, typeName, color, location, details, params);
				return mIOsmAndAidlInterface.updateMapPoint(new UpdateMapPointParams(layerId, point));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Remove point.
	 *
	 * @param layerId - layer id.
	 * @param pointId - point id.
	 */
	public boolean removeMapPoint(String layerId, String pointId) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.removeMapPoint(new RemoveMapPointParams(layerId, pointId));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Import GPX file to OsmAnd.
	 * OsmAnd must have rights to access location. Not recommended.
	 *
	 * @param file      - File which represents GPX track.
	 * @param fileName  - Destination file name. May contain dirs.
	 * @param color     - color of gpx. Can be one of: "red", "orange", "lightblue", "blue", "purple",
	 *                    "translucent_red", "translucent_orange", "translucent_lightblue",
	 *                    "translucent_blue", "translucent_purple"
	 * @param show      - show track on the map after import
	 */
	public boolean importGpxFromFile(File file, String fileName, String color, boolean show) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.importGpx(new ImportGpxParams(file, fileName, color, show));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Import GPX file to OsmAnd.
	 *
	 * @param gpxUri    - URI created by FileProvider.
	 * @param fileName  - Destination file name. May contain dirs.
	 * @param color     - color of gpx. Can be one of: "", "red", "orange", "lightblue", "blue", "purple",
	 *                    "translucent_red", "translucent_orange", "translucent_lightblue",
	 *                    "translucent_blue", "translucent_purple"
	 * @param show      - show track on the map after import
	 */
	public boolean importGpxFromUri(Uri gpxUri, String fileName, String color, boolean show) {
		if (mIOsmAndAidlInterface != null) {
			try {
				app.grantUriPermission(OSMAND_PACKAGE_NAME, gpxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				return mIOsmAndAidlInterface.importGpx(new ImportGpxParams(gpxUri, fileName, color, show));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start navigation using gpx file.
	 *
	 * @param gpxUri - URI created by FileProvider.
	 * @param force - ask to stop current navigation if any. False - ask. True - don't ask.
	 */
	public boolean navigateGpxFromUri(Uri gpxUri, boolean force) {
		if (mIOsmAndAidlInterface != null) {
			try {
				app.grantUriPermission(OSMAND_PACKAGE_NAME, gpxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				return mIOsmAndAidlInterface.navigateGpx(new NavigateGpxParams(gpxUri, force));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Import GPX file to OsmAnd.
	 *
	 * @param data      - Raw contents of GPX file. Sent as intent's extra string parameter.
	 * @param fileName  - Destination file name. May contain dirs.
	 * @param color     - color of gpx. Can be one of: "red", "orange", "lightblue", "blue", "purple",
	 *                    "translucent_red", "translucent_orange", "translucent_lightblue",
	 *                    "translucent_blue", "translucent_purple"
	 * @param show      - show track on the map after import
	 */
	public boolean importGpxFromData(String data, String fileName, String color, boolean show) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.importGpx(new ImportGpxParams(data, fileName, color, show));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start navigation using gpx file content.
	 *
	 * @param data - gpx file content.
	 * @param force - ask to stop current navigation if any. False - ask. True - don't ask.
	 */
	public boolean navigateGpxFromData(String data, boolean force) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.navigateGpx(new NavigateGpxParams(data, force));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Show GPX file on map.
	 *
	 * @param fileName - file name to show. Must be imported first.
	 */
	public boolean showGpx(String fileName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.showGpx(new ShowGpxParams(fileName));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Hide GPX file.
	 *
	 * @param fileName - file name to hide.
	 */
	public boolean hideGpx(String fileName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.hideGpx(new HideGpxParams(fileName));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Get list of active GPX files.
	 *
	 * @return list of active gpx files.
	 */
	public List<ASelectedGpxFile> getActiveGpxFiles() {
		if (mIOsmAndAidlInterface != null) {
			try {
				List<ASelectedGpxFile> res = new ArrayList<>();
				if (mIOsmAndAidlInterface.getActiveGpx(res)) {
					return res;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Remove GPX file.
	 *
	 * @param fileName - file name to remove;
	 */
	public boolean removeGpx(String fileName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.removeGpx(new RemoveGpxParams(fileName));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Set map view to selected location with set zoom level.
	 *
	 * @param latitude - latitude of new map center.
	 * @param longitude - longitude of new map center.
	 * @param zoom - map zoom level. Set 0 to keep zoom unchanged.
	 * @param animated - set true to animate changes.
	 */
	public boolean setMapLocation(double latitude, double longitude, int zoom, boolean animated) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setMapLocation(
						new SetMapLocationParams(latitude, longitude, zoom, animated));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start gpx recording.
	 */
	public boolean startGpxRecording(StartGpxRecordingParams params) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.startGpxRecording(params);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Stop gpx recording.
	 */
	public boolean stopGpxRecording(StopGpxRecordingParams params) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.stopGpxRecording(params);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Take photo note.
	 *
	 * @param lat - latutude of photo note.
	 * @param lon - longitude of photo note.
	 */
	public boolean takePhotoNote(double lat, double lon) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.takePhotoNote(new TakePhotoNoteParams(lat, lon));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start video note recording.
	 *
	 * @param lat - latutude of video note point.
	 * @param lon - longitude of video note point.
	 */
	public boolean startVideoRecording(double lat, double lon) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.startVideoRecording(new StartVideoRecordingParams(lat, lon));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start audio note recording.
	 *
	 * @param lat - latutude of audio note point.
	 * @param lon - longitude of audio note point.
	 */
	public boolean startAudioRecording(double lat, double lon) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.startAudioRecording(new StartAudioRecordingParams(lat, lon));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Stop Audio/Video recording.
	 */
	public boolean stopRecording() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.stopRecording(new StopRecordingParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Start navigation.
	 *
	 * @param startName - name of the start point as it displays in OsmAnd's UI. Nullable.
	 * @param startLat - latitude of the start point. If 0 - current location is used.
	 * @param startLon - longitude of the start point. If 0 - current location is used.
	 * @param destName - name of the start point as it displays in OsmAnd's UI.
	 * @param destLat - latitude of a destination point.
	 * @param destLon - longitude of a destination point.
	 * @param profile - One of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck". Nullable (default).
	 * @param force - ask to stop current navigation if any. False - ask. True - don't ask.
	 */
	public boolean navigate(String startName, double startLat, double startLon, String destName, double destLat, double destLon, String profile, boolean force) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.navigate(new NavigateParams(startName, startLat, startLon, destName, destLat, destLon, profile, force));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Do search and start navigation.
	 *
	 * @param startName - name of the start point as it displays in OsmAnd's UI. Nullable.
	 * @param startLat - latitude of the start point. If 0 - current location is used.
	 * @param startLon - longitude of the start point. If 0 - current location is used.
	 * @param searchQuery  - Text of a query for searching a destination point. Sent as URI parameter.
	 * @param searchLat - original location of search (latitude). Sent as URI parameter.
	 * @param searchLon - original location of search (longitude). Sent as URI parameter.
	 * @param profile - one of: "default", "car", "bicycle", "pedestrian", "aircraft", "boat", "hiking", "motorcycle", "truck". Nullable (default).
	 * @param force - ask to stop current navigation if any. False - ask. True - don't ask.
	 */
	public boolean navigateSearch(String startName, double startLat, double startLon,
								  String searchQuery, double searchLat, double searchLon,
								  String profile, boolean force) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.navigateSearch(new NavigateSearchParams(
						startName, startLat, startLon, searchQuery, searchLat, searchLon, profile, force));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for adding up to 3 items to the OsmAnd navigation drawer.
	 *
	 * @param appPackage - current application package.
	 * @param names - list of names for items.
	 * @param uris - list of uris for intents.
	 * @param iconNames - list of icon names for items.
	 * @param flags - list of flags for intents. Use -1 if no flags needed.
	 */
	public boolean setNavDrawerItems(String appPackage, List<String> names, List<String> uris, List<String> iconNames, List<Integer> flags) {
		if (mIOsmAndAidlInterface != null) {
			try {
				List<NavDrawerItem> items = new ArrayList<>();
				for (int i = 0; i < names.size(); i++) {
					items.add(new NavDrawerItem(names.get(i), uris.get(i), iconNames.get(i), flags.get(i)));
				}
				return mIOsmAndAidlInterface.setNavDrawerItems(new SetNavDrawerItemsParams(appPackage, items));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Method for adding image to the top of OsmAnd's NavDrawer.
	 *
	 * @param imageUri - image's URI.toString
	 *
	 * @deprecated
	 * Use the {@link #setNavDrawerLogoWithParams(String imageUri, String packageName, String intent)} method.
	 */
	public boolean setNavDrawerLogo(String imageUri) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setNavDrawerLogo(imageUri);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for adding image to the top of OsmAnd's NavDrawer with additional params
	 *
	 * @param imageUri - image's URI.toString
	 * @param packageName - client's app package name
	 * @param intent - intent for additional functionality on image click
	 *
	 */
	public boolean setNavDrawerLogoWithParams(String imageUri, String packageName, String intent) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface
					.setNavDrawerLogoWithParams(new NavDrawerHeaderParams(imageUri, packageName, intent));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Method for adding functionality to "Powered by Osmand" logo in NavDrawer's footer
	 * (reset OsmAnd settings to pre-clinet app's state)
	 *
	 * @param packageName - package name
	 * @param intent - intent
	 * @param appName - client's app name
	 */
	public boolean setNavDrawerFooterWithParams(String packageName, String intent, String appName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setNavDrawerFooterWithParams(new NavDrawerFooterParams(packageName, intent, appName));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for selected UI elements (like QuickSearch button) to show.
	 *
	 * @param ids - list of menu items keys from {@link OsmAndCustomizationConstants}
	 */
	boolean setEnabledIds(List<String> ids) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setEnabledIds(ids);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for selected UI elements (like QuickSearch button) to hide.
	 *
	 * @param ids - list of menu items keys from {@link OsmAndCustomizationConstants}
	 */
	boolean setDisabledIds(List<String> ids) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setDisabledIds(ids);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for selected NavDrawer's menu items to show.
	 *
	 * @param patterns - list of menu items names from {@link OsmAndCustomizationConstants}
	 */
	boolean setEnabledPatterns(List<String> patterns) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setEnabledPatterns(patterns);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method for selected NavDrawer's menu items to hide.
	 *
	 * @param patterns - list of menu items names from {@link OsmAndCustomizationConstants}
	 */
	boolean setDisabledPatterns(List<String> patterns) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.setDisabledPatterns(patterns);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Put navigation on pause.
	 */
	public boolean pauseNavigation() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.pauseNavigation(new PauseNavigationParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Resume navigation if it was paused before.
	 */
	public boolean resumeNavigation() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.resumeNavigation(new ResumeNavigationParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Stop navigation. Removes target / intermediate points and route path from the map.
	 */
	public boolean stopNavigation() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.stopNavigation(new StopNavigationParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Mute voice guidance. Stays muted until unmute manually or via the api.
	 */
	public boolean muteNavigation() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.muteNavigation(new MuteNavigationParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Unmute voice guidance.
	 */
	public boolean unmuteNavigation() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.unmuteNavigation(new UnmuteNavigationParams());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Run search for POI / Address.
	 *
	 * @param searchQuery - search query string.
	 * @param searchType - type of search. Values:
	 *                   SearchParams.SEARCH_TYPE_ALL - all kind of search
	 *                   SearchParams.SEARCH_TYPE_POI - POIs only
	 *                   SearchParams.SEARCH_TYPE_ADDRESS - addresses only
	 *
	 * @param latitude - latitude of original search location.
	 * @param longitude - longitude of original search location.
	 * @param radiusLevel - value from 1 to 7. Default value = 1.
	 * @param totalLimit - limit of returned search result rows. Default value = -1 (unlimited).
	 */
	public boolean search(String searchQuery, int searchType, double latitude, double longitude, int radiusLevel, int totalLimit) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.search(new SearchParams(searchQuery, searchType, latitude, longitude, radiusLevel, totalLimit), mIOsmAndAidlCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Register OsmAnd widgets for visibility.
	 *
	 * @param widgetKey - widget id.
	 * @param appModKeys - list of modes widget active with. Could be "null" for all modes.
	 */
	public boolean regWidgetVisibility(String widgetKey, @Nullable List<String> appModKeys) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.regWidgetVisibility(new SetWidgetsParams(widgetKey, appModKeys));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Register OsmAnd widgets for availability.
	 *
	 * @param widgetKey - widget id.
	 * @param appModKeys - list of modes widget active with. Could be "null" for all modes.
	 */
	public boolean regWidgetAvailability(String widgetKey, @Nullable List<String> appModKeys) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.regWidgetAvailability(new SetWidgetsParams(widgetKey, appModKeys));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add custom parameters for OsmAnd settings to associate with client app.
	 *
	 * @param sharedPreferencesName - string with name of clint's app for shared preferences key
	 * @param bundle - bundle with keys from Settings IDs {@link OsmAndCustomizationConstants} and Settings params
	 */
	public boolean customizeOsmandSettings(String sharedPreferencesName, Bundle bundle) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface
					.customizeOsmandSettings(new OsmandSettingsParams(sharedPreferencesName, bundle));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Method to get list of gpx files currently registered (imported or created) in OsmAnd;
	 *
	 * @return list of gpx files
	 */
	public List<AGpxFile> getImportedGpx() {
		if (mIOsmAndAidlInterface != null) {
			try {
				List<AGpxFile> fileList = new ArrayList<>();
				if (mIOsmAndAidlInterface.getImportedGpx(fileList)) {
					return fileList;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Method to get list of sqlitedb files registered in OsmAnd;
	 *
	 * @return list of sqlitedb files
	 */
	public List<ASqliteDbFile> getSqliteDbFiles() {
		if (mIOsmAndAidlInterface != null) {
			try {
				List<ASqliteDbFile> fileList = new ArrayList<>();
				mIOsmAndAidlInterface.getSqliteDbFiles(fileList);
				return fileList;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Method to get list of currently active sqlitedb files
	 *
	 * @return list of sqlitedb files
	 */
	public List<ASqliteDbFile> getActiveSqliteDbFiles() {
		if (mIOsmAndAidlInterface != null) {
			try {
				List<ASqliteDbFile> fileList = new ArrayList<>();
				mIOsmAndAidlInterface.getActiveSqliteDbFiles(fileList);
				return fileList;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Method to show selected sqlitedb file as map overlay.
	 *
	 * @param fileName - name of sqlitedb file
	 */
	public boolean showSqliteDbFile(String fileName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.showSqliteDbFile(fileName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method to hide sqlitedb file from map overlay.
	 *
	 * @param fileName - name of sqlitedb file
	 */
	public boolean hideSqliteDbFile(String fileName) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.hideSqliteDbFile(fileName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method to copy files to OsmAnd part by part.
	 * Part size (bytearray) should not exceed 256k.
	 *
	 * @param fileName - name of file
	 * @param filePartData - parts of file, byte[] with size 256k or less.
	 * @param startTime - timestamp of copying start.
	 * @param isDone - boolean to mark end of copying.
	 * @return number of last successfully received file part or error(-1).
	 */
	public int copyFile(String fileName, byte[] filePartData, long startTime, boolean isDone) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.copyFile(new CopyFileParams(fileName, filePartData, startTime, isDone));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * Restore default (pre-client) OsmAnd settings and state:
	 * clears feature's, widget's and setting customization, NavDraw logo.
	 */
	public boolean restoreOsmand() {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.restoreOsmand();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method to change state of plug-ins in OsmAnd.
	 *
	 * @param pluginId - id (name) of plugin.
	 * @param newState - new state (0 - off, 1 - on).
	 */
	boolean changePluginState(String pluginId, int newState) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.changePluginState(new PluginParams(pluginId, newState));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method to register for callback on OsmAnd initialization
	 * @param callback - create and provide instance of {@link IOsmAndAidlCallback} interface
	 */
	public boolean registerForOsmandInitListener(IOsmAndAidlCallback callback) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.registerForOsmandInitListener(callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Method to register for periodical callbacks from OsmAnd
	 *
	 * @param updateTimeMS - period of time in millisecond after which callback is triggered
	 * @param callback - create and provide instance of {@link IOsmAndAidlCallback} interface
	 * @return id of callback in OsmAnd. Needed to unsubscribe from updates.
	 */
	public long registerForUpdates(long updateTimeMS, IOsmAndAidlCallback callback) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.registerForUpdates(updateTimeMS, callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * Method to unregister from periodical callbacks from OsmAnd
	 *
	 * @param callbackId - id of registered callback (provided by OsmAnd
	 * in {@link OsmAndAidlHelper#registerForUpdates(long, IOsmAndAidlCallback)})
	 */
	boolean unregisterFromUpdates(long callbackId) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.unregisterFromUpdates(callbackId);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}


	/**
	 * Requests bitmap snap-shot of map with GPX file from provided URI in its center.
	 * You can set bitmap size, density and GPX lines color, but you need
	 * to manually download appropriate map in OsmAnd or background will be empty.
	 * Bitmap will be returned through callback {@link IOsmAndAidlCallback#onGpxBitmapCreated(AGpxBitmap)}
	 *
	 * @param gpxUri - Uri for gpx file
	 * @param density - image density. Recommended to use default metrics for device's display.
	 * @param widthPixels - width of bitmap
	 * @param heightPixels - height of bitmap
	 * @param color - color in ARGB format
	 * @param callback - instance of callback from OsmAnd.
	 */
	public boolean getBitmapForGpx(Uri gpxUri, float density, int widthPixels, int heightPixels, int color, IOsmAndAidlCallback callback) {
		if (mIOsmAndAidlInterface != null) {
			try {
				app.grantUriPermission("net.osmand", gpxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				app.grantUriPermission("net.osmand.plus", gpxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				app.grantUriPermission("net.osmand.dev", gpxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				return mIOsmAndAidlInterface.getBitmapForGpx(new CreateGpxBitmapParams(gpxUri, density, widthPixels, heightPixels, color), callback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
