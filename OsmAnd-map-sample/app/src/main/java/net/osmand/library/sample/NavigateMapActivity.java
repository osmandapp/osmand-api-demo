package net.osmand.library.sample;

import static net.osmand.plus.utils.InsetsUtils.InsetSide.BOTTOM;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.LEFT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.RIGHT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.TOP;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.OsmandActionBarActivity;
import net.osmand.plus.activities.RestartActivity;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.helpers.TargetPointsHelper;
import net.osmand.plus.routing.RoutingHelper;
import net.osmand.plus.settings.backend.ApplicationMode;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.InsetsUtils;
import net.osmand.plus.utils.InsetsUtils.InsetSide;
import net.osmand.plus.utils.NativeUtilities;
import net.osmand.plus.views.MapViewWithLayers;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.OsmandMapTileView.OnLongClickListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class NavigateMapActivity extends OsmandActionBarActivity {

	private OsmandApplication app;
	private OsmandMapTileView mapTileView;
	private MapViewWithLayers mapViewWithLayers;
	private OnLongClickListener clickListener;

	private LatLon start;
	private LatLon finish;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_map_activity);
		mapViewWithLayers = findViewById(R.id.map_view_with_layers);

		app = (OsmandApplication) getApplication();

		mapTileView = app.getOsmandMap().getMapView();
		mapTileView.setupRenderingView();

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("Navigate map");
		toolbar.setNavigationIcon(AndroidUtils.getNavigationIconResId(app));
		toolbar.setNavigationOnClickListener(v -> onBackPressed());

		CompoundButton openglSwitch = findViewById(R.id.opengl_switch);
		openglSwitch.setChecked(app.getSettings().USE_OPENGL_RENDER.get());
		openglSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			app.getSettings().USE_OPENGL_RENDER.set(isChecked);
			RestartActivity.doRestart(this);
		});

		//set start location and zoom for map
		mapTileView.setIntZoom(14);
		mapTileView.setLatLon(52.3704312, 4.8904288);
	}

	@Override
	public void updateStatusBarColor() {
		int color = AndroidUtils.getColorFromAttr(this, android.R.attr.colorPrimary);
		if (color != -1) {
			AndroidUiHelper.setStatusBarColor(this, color);
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		View root = findViewById(R.id.root);
		List<InsetSide> sides = Arrays.asList(LEFT, TOP, RIGHT, BOTTOM);
		InsetsUtils.setWindowInsetsListener(root, new HashSet<>(sides));
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
		mapTileView.setOnLongClickListener(getClickListener());
	}

	private OnLongClickListener getClickListener() {
		if (clickListener == null) {
			clickListener = point -> {
				RotatedTileBox tileBox = mapTileView.getCurrentRotatedTileBox();
				LatLon latLon = NativeUtilities.getLatLonFromPixel(mapTileView.getMapRenderer(), tileBox, point.x, point.y);

				if (start == null) {
					start = latLon;
					app.showShortToastMessage("Start point " + latLon.getLatitude() + " " + latLon.getLongitude());
				} else if (finish == null) {
					finish = latLon;
					app.showShortToastMessage("Finish point " + latLon.getLatitude() + " " + latLon.getLongitude());
					startNavigation();
				}
				return true;
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
		mapTileView.setOnLongClickListener(null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapViewWithLayers.onDestroy();
	}
}