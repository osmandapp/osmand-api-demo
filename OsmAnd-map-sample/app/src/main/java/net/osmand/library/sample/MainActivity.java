package net.osmand.library.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import net.osmand.Location;
import net.osmand.SecondSplashScreenFragment;
import net.osmand.data.LatLon;
import net.osmand.data.QuadPoint;
import net.osmand.data.RotatedTileBox;
import net.osmand.map.MapTileDownloader;
import net.osmand.map.MapTileDownloader.IMapDownloaderCallback;
import net.osmand.plus.AppInitializer;
import net.osmand.plus.AppInitializer.AppInitializeListener;
import net.osmand.plus.OsmAndConstants;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.ProgressImplementation;
import net.osmand.plus.base.MapViewTrackingUtilities;
import net.osmand.plus.dialogs.WhatsNewDialogFragment;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.helpers.ScrollHelper;
import net.osmand.plus.helpers.ScrollHelper.OnScrollEventListener;
import net.osmand.plus.resources.ResourceManager;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.views.AnimateDraggingMapThread;
import net.osmand.plus.views.OsmAndMapSurfaceView;
import net.osmand.plus.views.OsmandMapTileView;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity implements OnScrollEventListener {

	public static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 5;
	public static final int PERMISSION_REQUEST_LOCATION_ON_RESUME = 6;
	public static final int PERMISSION_REQUEST_LOCATION_ON_BUTTON = 7;

	private static final int SHOW_POSITION_MSG_ID = OsmAndConstants.UI_HANDLER_MAP_VIEW + 1;

	private static final int SMALL_SCROLLING_UNIT = 1;
	private static final int BIG_SCROLLING_UNIT = 200;

	private MapViewTrackingUtilities mapViewTrackingUtilities;
	private OsmandMapTileView mapView;

	private MapLayers mapLayers;

	private SampleApplication app;
	private OsmandSettings settings;
	private ScrollHelper mapScrollHelper;

	private boolean landscapeLayout;

	private AppInitializeListener initListener;
	private IMapDownloaderCallback downloaderCallback;

	private boolean pendingPause = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		app = getMyApplication();
		settings = app.getSettings();
		mapScrollHelper = new ScrollHelper(app);
		app.applyTheme(this);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

		boolean portraitMode = AndroidUiHelper.isOrientationPortrait(this);
		boolean largeDevice = AndroidUiHelper.isXLargeDevice(this);
		landscapeLayout = !portraitMode && !largeDevice;
		mapViewTrackingUtilities = app.getMapViewTrackingUtilities();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= 21) {
			enterToFullScreen();
		}

		boolean externalStoragePermissionGranted = ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		if (!externalStoragePermissionGranted) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
		}

		int statusBarHeight = AndroidUtils.getStatusBarHeight(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		int h = dm.heightPixels - statusBarHeight;

		mapView = new OsmandMapTileView(this, w, h);
		if (WhatsNewDialogFragment.shouldShowDialog(app)) {
			boolean showed = WhatsNewDialogFragment.showInstance(getSupportFragmentManager());
			if (showed) {
				SecondSplashScreenFragment.SHOW = false;
			}
		}
		mapLayers = new MapLayers(this);

		checkAppInitialization();
		mapViewTrackingUtilities.setMapView(mapView);

		downloaderCallback = new IMapDownloaderCallback() {
			@Override
			public void tileDownloaded(MapTileDownloader.DownloadRequest request) {
				if (request != null && !request.error && request.fileToSave != null) {
					ResourceManager mgr = app.getResourceManager();
					mgr.tileDownloaded(request);
				}
				if (request == null || !request.error) {
					mapView.tileDownloaded(request);
				}
			}
		};
		app.getResourceManager().getMapTileDownloader().addDownloaderCallback(downloaderCallback);
		mapLayers.createLayers(mapView);

		if (!settings.isLastKnownMapLocation()) {
			// show first time when application ran
			final WeakReference<MainActivity> activityRef = new WeakReference<>(this);
			net.osmand.Location location = app.getLocationProvider().getFirstTimeRunDefaultLocation(new OsmAndLocationProvider.OsmAndLocationListener() {
				@Override
				public void updateLocation(Location location) {
					MainActivity a = activityRef.get();
					if (AndroidUtils.isActivityNotDestroyed(a) && app.getLocationProvider().getLastKnownLocation() == null) {
						setMapInitialLatLon(a.mapView, location);
					}
				}
			});
			mapViewTrackingUtilities.setMapLinkedToLocation(true);
			if (location != null) {
				setMapInitialLatLon(mapView, location);
			}
		}
		mapView.refreshMap(true);

		if (!InstallOsmandAppDialog.show(getSupportFragmentManager(), this)
				&& externalStoragePermissionGranted) {
			checkMapsInstalled();
		}
	}

	private void setMapInitialLatLon(@NonNull OsmandMapTileView mapView, @Nullable Location location) {
		if (location != null) {
			mapView.setLatLon(location.getLatitude(), location.getLongitude());
			mapView.setIntZoom(14);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (OsmAndLocationProvider.isLocationPermissionAvailable(this)) {
			app.getLocationProvider().resumeAllUpdates();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSION_REQUEST_LOCATION_ON_RESUME);
		}

		if (settings != null && settings.isLastKnownMapLocation()) {
			LatLon l = settings.getLastKnownMapLocation();
			mapView.setLatLon(l.getLatitude(), l.getLongitude());
			mapView.setIntZoom(settings.getLastKnownMapZoom());
		}

		showAndHideMapPosition();
		readLocationToShow();

		mapLayers.updateLayers(mapView);
		mapView.refreshMap(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode()) {
			pendingPause = true;
		} else {
			onPauseActivity();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mapScrollHelper.setListener(this);
		getMyApplication().getNotificationHelper().showNotifications();
	}

	@Override
	protected void onStop() {
		getMyApplication().getNotificationHelper().removeNotifications(true);
		if (pendingPause) {
			onPauseActivity();
		}
		mapScrollHelper.setListener(null);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getMyApplication().unsubscribeInitListener(initListener);
		mapViewTrackingUtilities.setMapView(null);
		app.getResourceManager().getMapTileDownloader().removeDownloaderCallback(mapView);
	}

	private void onPauseActivity() {
		pendingPause = false;
		mapView.setOnDrawMapListener(null);
		app.getLocationProvider().pauseAllUpdates();

		settings.setLastKnownMapLocation((float) mapView.getLatitude(), (float) mapView.getLongitude());
		AnimateDraggingMapThread animatedThread = mapView.getAnimatedDraggingThread();
		if (animatedThread.isAnimating() && animatedThread.getTargetIntZoom() != 0 && !mapViewTrackingUtilities.isMapLinkedToLocation()) {
			settings.setMapLocationToShow(animatedThread.getTargetLatitude(), animatedThread.getTargetLongitude(),
					animatedThread.getTargetIntZoom());
		}

		settings.setLastKnownMapZoom(mapView.getZoom());
		app.getResourceManager().interruptRendering();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			SampleApplication app = getMyApplication();
			switch (requestCode) {
				case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
					app.setExternalStorageDirectory(OsmandSettings.EXTERNAL_STORAGE_TYPE_DEFAULT,
							app.getSettings().getDefaultInternalStorage().getAbsolutePath());
					if (!InstallOsmandAppDialog.wasShown()) {
						checkMapsInstalled();
					}
					reloadData();
					break;
				case PERMISSION_REQUEST_LOCATION_ON_BUTTON:
					app.getMapViewTrackingUtilities().backToLocationImpl();
				case PERMISSION_REQUEST_LOCATION_ON_RESUME:
					app.getLocationProvider().resumeAllUpdates();
					break;
			}
		}
	}

	public SampleApplication getMyApplication() {
		return ((SampleApplication) getApplication());
	}

	private void checkAppInitialization() {
		if (app.isApplicationInitializing()) {
			initListener = new AppInitializeListener() {

				@Override
				public void onStart(AppInitializer init) {

				}

				@Override
				public void onProgress(AppInitializer init, AppInitializer.InitEvents event) {
					if (event == AppInitializer.InitEvents.MAPS_INITIALIZED) {
						mapView.refreshMap(false);
					}
					if (event == AppInitializer.InitEvents.FAVORITES_INITIALIZED) {
						refreshMap();
					}
				}

				@Override
				public void onFinish(AppInitializer init) {
					setupMapView();
					mapView.refreshMap(false);
				}
			};
			getMyApplication().checkApplicationIsBeingInitialized(initListener);
		} else {
			setupMapView();
		}
	}

	private void checkMapsInstalled() {
		File mapsDir = getMyApplication().getAppPath(null);
		boolean noMapsFound;
		if (mapsDir.exists()) {
			File[] maps = mapsDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.toLowerCase().endsWith(".obf");
				}
			});
			noMapsFound = maps == null || maps.length == 0;
		} else {
			noMapsFound = true;
		}

		if (noMapsFound) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.install_maps_title);
			builder.setMessage(R.string.install_maps_desc);
			builder.setNegativeButton(R.string.shared_string_cancel, null);
			builder.setPositiveButton(R.string.restart_app, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SampleApplication.doRestart(MainActivity.this);
				}
			});
			builder.create().show();
		}
	}

	private void reloadData() {
		new ReloadData(this, getMyApplication()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
	}

	private void setupMapView() {
		OsmAndMapSurfaceView surf = (OsmAndMapSurfaceView) findViewById(R.id.MapView);
		surf.setVisibility(View.VISIBLE);
		surf.setMapView(mapView);
	}

	public void exitFromFullScreen(View view) {
		AndroidUtils.exitFromFullScreen(this, view);
	}

	public void enterToFullScreen() {
		AndroidUtils.enterToFullScreen(this, getLayout());
	}

	public void refreshMap() {
		getMapView().refreshMap();
	}

	public View getLayout() {
		return getWindow().getDecorView().findViewById(android.R.id.content);
	}

	public OsmandMapTileView getMapView() {
		return mapView;
	}

	public void showAndHideMapPosition() {
		mapView.setShowMapPosition(true);
		app.runMessageInUIThreadAndCancelPrevious(SHOW_POSITION_MSG_ID, new Runnable() {
			@Override
			public void run() {
				if (mapView.isShowMapPosition()) {
					mapView.setShowMapPosition(false);
					mapView.refreshMap();
				}
			}
		}, 2500);
	}

	public void changeZoom(int stp, long time) {
		mapViewTrackingUtilities.setZoomTime(time);
		changeZoom(stp);
	}

	public void changeZoom(int stp) {
		boolean changeLocation = false;
		final int newZoom = mapView.getZoom() + stp;
		final double zoomFrac = mapView.getZoomFractionalPart();
		if (newZoom > mapView.getMaxZoom()) {
			Toast.makeText(this, R.string.edit_tilesource_maxzoom, Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
			return;
		}
		if (newZoom < mapView.getMinZoom()) {
			Toast.makeText(this, R.string.edit_tilesource_minzoom, Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
			return;
		}
		mapView.getAnimatedDraggingThread().startZooming(newZoom, zoomFrac, changeLocation);
		if (app.accessibilityEnabled())
			Toast.makeText(this, getString(R.string.zoomIs) + " " + newZoom, Toast.LENGTH_SHORT).show(); //$NON-NLS-1$
		showAndHideMapPosition();
	}

	public void setMapLocation(double lat, double lon) {
		mapView.setLatLon(lat, lon);
		mapViewTrackingUtilities.locationChanged(lat, lon, this);
	}

	public LatLon getMapLocation() {
		return mapViewTrackingUtilities.getMapLocation();
	}

	public float getMapRotate() {
		if (mapView == null) {
			return 0;
		}
		return mapView.getRotate();
	}

	public void readLocationToShow() {
		showMapControls();

		LatLon cur = new LatLon(mapView.getLatitude(), mapView.getLongitude());
		LatLon latLonToShow = settings.getAndClearMapLocationToShow();
		if (latLonToShow != null) {
			// remember if map should come back to isMapLinkedToLocation=true
			mapViewTrackingUtilities.setMapLinkedToLocation(false);
			if (!latLonToShow.equals(cur)) {
				mapView.getAnimatedDraggingThread().startMoving(latLonToShow.getLatitude(),
						latLonToShow.getLongitude(), settings.getMapZoomToShow(), true);
			}
		}
	}

	public void showMapControls() {
		if (mapLayers.getMapControlsLayer() != null) {
			mapLayers.getMapControlsLayer().showMapControlsIfHidden();
		}
	}

	private void scrollMap(int dx, int dy) {
		final RotatedTileBox tb = mapView.getCurrentRotatedTileBox();
		final QuadPoint cp = tb.getCenterPixelPoint();
		final LatLon l = tb.getLatLonFromPixel(cp.x + dx, cp.y + dy);
		setMapLocation(l.getLatitude(), l.getLongitude());
	}

	@Override
	public void onScrollEvent(boolean continuousScrolling, boolean up, boolean down, boolean left, boolean right) {
		int scrollingUnit = continuousScrolling ? SMALL_SCROLLING_UNIT : BIG_SCROLLING_UNIT;
		int dx = (left ? -scrollingUnit : 0) + (right ? scrollingUnit : 0);
		int dy = (up ? -scrollingUnit : 0) + (down ? scrollingUnit : 0);
		scrollMap(dx, dy);
	}

	private static class ReloadData extends AsyncTask<Void, Void, Boolean> {
		private Context ctx;
		protected ProgressImplementation progress;
		private OsmandApplication app;

		public ReloadData(Context ctx, OsmandApplication app) {
			this.ctx = ctx;
			this.app = app;
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressImplementation.createProgressDialog(ctx, ctx.getString(net.osmand.plus.R.string.loading_data),
					ctx.getString(net.osmand.plus.R.string.loading_data), ProgressDialog.STYLE_HORIZONTAL);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (progress.getDialog().isShowing()) {
					progress.getDialog().dismiss();
				}
			} catch (Exception e) {
				//ignored
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			app.getResourceManager().reloadIndexes(progress, new ArrayList<String>());
			return true;
		}
	}
}