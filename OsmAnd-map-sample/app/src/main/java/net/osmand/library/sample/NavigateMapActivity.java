package net.osmand.library.sample;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import net.osmand.core.android.MapRendererView;
import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.AppInitializer;
import net.osmand.plus.AppInitializer.AppInitializeListener;
import net.osmand.plus.AppInitializer.InitEvents;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.RestartActivity;
import net.osmand.plus.helpers.TargetPointsHelper;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.utils.NativeUtilities;
import net.osmand.plus.views.MapViewWithLayers;
import net.osmand.plus.views.OsmandMap.OsmandMapListener;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.OsmandMapTileView.OnLongClickListener;
import net.osmand.plus.views.corenative.NativeCoreContext;

public class NavigateMapActivity extends AppCompatActivity implements OsmandMapListener {

	private OsmandApplication app;

	private MapViewWithLayers mapViewWithLayers;
	private AppInitializeListener initListener;
	private OnLongClickListener clickListener;

	private LatLon start;
	private LatLon finish;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_map_activity);
		mapViewWithLayers = findViewById(R.id.map_view_with_layers);

		app = (OsmandApplication) getApplication();
		app.getOsmandMap().addListener(this);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle("Navigate map");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}

		CompoundButton openglSwitch = findViewById(R.id.opengl_switch);
		openglSwitch.setChecked(app.getSettings().USE_OPENGL_RENDER.get());
		openglSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			app.getSettings().USE_OPENGL_RENDER.set(isChecked);
			RestartActivity.doRestart(this);
		});

		checkAppInitialization();
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	@SuppressLint("SyntheticAccessor")
	protected void onResume() {
		super.onResume();
		mapViewWithLayers.onResume();

		OsmandMapTileView mapTileView = app.getOsmandMap().getMapView();
		mapTileView.setOnLongClickListener(getClickListener());
	}

	private OnLongClickListener getClickListener() {
		if (clickListener == null) {
			OsmandMapTileView mapTileView = app.getOsmandMap().getMapView();
			clickListener = new OnLongClickListener() {
				@Override
				public boolean onLongPressEvent(PointF point) {
					RotatedTileBox tileBox = mapTileView.getCurrentRotatedTileBox();
					MapRendererView mapRendererView = mapTileView.getMapRenderer();

					LatLon latLon = NativeUtilities.getLatLonFromPixel(mapRendererView, tileBox, point.x, point.y);
					if (start == null) {
						start = latLon;
						app.showShortToastMessage("Start point " + latLon.getLatitude() + " " + latLon.getLongitude());
					} else if (finish == null) {
						finish = latLon;
						app.showShortToastMessage("Finish point " + latLon.getLatitude() + " " + latLon.getLongitude());
						startNavigation();
					}
					return true;
				}
			};
		}
		return clickListener;
	}

	private void startNavigation() {
		OsmandSettings settings = app.getSettings();
		RoutingHelper routingHelper = app.getRoutingHelper();
		settings.setApplicationMode(ApplicationMode.CAR);

		TargetPointsHelper targetPointsHelper = app.getTargetPointsHelper();

		targetPointsHelper.setStartPoint(start, false, new PointDescription(start.getLatitude(), start.getLongitude()));
		targetPointsHelper.navigateToPoint(finish, true, -1, new PointDescription(finish.getLatitude(), finish.getLongitude()));

		app.getOsmandMap().getMapActions().enterRoutePlanningModeGivenGpx(null, start, null, true, false);

		settings.FOLLOW_THE_ROUTE.set(true);
		routingHelper.setFollowingMode(true);
		routingHelper.setRoutePlanningMode(false);
		routingHelper.notifyIfRouteIsCalculated();
		routingHelper.setCurrentLocation(app.getLocationProvider().getLastKnownLocation(), false);

		OsmAndLocationProvider.requestFineLocationPermissionIfNeeded(this);

		app.showShortToastMessage("StartNavigation from " + start.getLatitude() + " " + start.getLongitude()
				+ " to " + finish.getLatitude() + " " + finish.getLongitude());

		start = null;
		finish = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapViewWithLayers.onPause();

		OsmandMapTileView mapTileView = app.getOsmandMap().getMapView();
		mapTileView.setOnLongClickListener(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapViewWithLayers.onDestroy();
		app.getOsmandMap().removeListener(this);
		app.getOsmandMap().getMapView().clearTouchDetectors();
	}

	@Override
	public void onChangeZoom(int i) {

	}

	@Override
	public void onSetMapElevation(float v) {
		mapViewWithLayers.onSetMapElevation(v);
	}

	@Override
	public void onSetupOpenGLView(boolean b) {
		mapViewWithLayers.setupOpenGLView(b);
		app.getOsmandMap().getMapView().setupTouchDetectors(this);
	}

	private void checkAppInitialization() {
		if (app.isApplicationInitializing()) {
			initListener = new AppInitializeListener() {
				boolean openGlSetup;

				@Override
				public void onStart(AppInitializer init) {

				}

				@Override
				public void onProgress(AppInitializer init, InitEvents event) {
					boolean openGlInitialized = event == InitEvents.NATIVE_OPEN_GL_INITIALIZED && NativeCoreContext.isInit();
					if ((openGlInitialized || event == InitEvents.NATIVE_INITIALIZED) && !openGlSetup) {
						app.getOsmandMap().setupOpenGLView(false);
						openGlSetup = true;
					}
					if (event == InitEvents.MAPS_INITIALIZED) {
						app.getOsmandMap().getMapView().refreshMap(false);
					}
					if (event == InitEvents.FAVORITES_INITIALIZED) {
						app.getOsmandMap().refreshMap();
					}
				}

				@Override
				public void onFinish(AppInitializer init) {
					if (!openGlSetup) {
						app.getOsmandMap().setupOpenGLView(false);
					}
					app.getOsmandMap().getMapView().refreshMap(false);
				}
			};
			app.checkApplicationIsBeingInitialized(initListener);
		} else {
			app.getOsmandMap().setupOpenGLView(true);
		}
	}
}