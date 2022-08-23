package net.osmand.library.sample;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import net.osmand.data.FavouritePoint;
import net.osmand.plus.AppInitializer;
import net.osmand.plus.AppInitializer.AppInitializeListener;
import net.osmand.plus.AppInitializer.InitEvents;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.RestartActivity;
import net.osmand.plus.myplaces.FavouritesHelper;
import net.osmand.plus.views.MapViewWithLayers;
import net.osmand.plus.views.OsmandMap.OsmandMapListener;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.corenative.NativeCoreContext;

import java.util.ArrayList;
import java.util.List;

public class PointsOnMapActivity extends AppCompatActivity implements OsmandMapListener {

	private OsmandApplication app;
	private OsmandMapTileView mapTileView;

	private MapViewWithLayers mapViewWithLayers;
	private AppInitializeListener initListener;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_map_activity);
		mapViewWithLayers = findViewById(R.id.map_view_with_layers);

		app = (OsmandApplication) getApplication();
		app.getOsmandMap().addListener(this);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setTitle("Points on map");
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
		}

		CompoundButton openglSwitch = findViewById(R.id.opengl_switch);
		openglSwitch.setChecked(app.getSettings().USE_OPENGL_RENDER.get());
		openglSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			app.getSettings().USE_OPENGL_RENDER.set(isChecked);
			RestartActivity.doRestart(this);
		});

		FavouritesHelper favouritesHelper = app.getFavoritesHelper();
		for (FavouritePoint point : getFavouritePoints()) {
			favouritesHelper.addFavourite(point);
		}
		mapTileView = app.getOsmandMap().getMapView();
		mapTileView.setupOpenGLView();

		mapTileView.setIntZoom(14);
		mapTileView.setLatLon(52.3704312, 4.8904288);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapViewWithLayers.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapViewWithLayers.onPause();
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

	private List<FavouritePoint> getFavouritePoints() {
		List<FavouritePoint> points = new ArrayList<>();
		points.add(new FavouritePoint(50.8465565, 4.351697, "Brussel", "cities"));
		points.add(new FavouritePoint(51.5073219, -0.1276474, "London", "cities"));
		points.add(new FavouritePoint(48.8566101, 2.3514992, "Paris", "cities"));
		points.add(new FavouritePoint(47.4983815, 19.0404707, "Budapest", "cities"));
		points.add(new FavouritePoint(55.7506828, 37.6174976, "Moscow", "cities"));
		points.add(new FavouritePoint(39.9059631, 116.391248, "Beijing", "cities"));
		points.add(new FavouritePoint(35.6828378, 139.7589667, "Tokyo", "cities"));
		points.add(new FavouritePoint(38.8949549, -77.0366456, "Washington", "cities"));
		points.add(new FavouritePoint(45.4210328, -75.6900219, "Ottawa", "cities"));
		points.add(new FavouritePoint(8.9710438, -79.5340599, "Panama", "cities"));
		points.add(new FavouritePoint(53.9072394, 27.5863608, "Minsk", "cities"));
		points.add(new FavouritePoint(52.5162303,13.3777309, "Berlin", "cities"));
		points.add(new FavouritePoint(52.3704312, 4.8904288, "Amsterdam", "cities"));

		return points;
	}
}
