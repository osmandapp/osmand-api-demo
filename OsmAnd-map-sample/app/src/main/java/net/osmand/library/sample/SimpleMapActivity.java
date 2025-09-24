package net.osmand.library.sample;

import static net.osmand.plus.utils.InsetsUtils.InsetSide.BOTTOM;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.LEFT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.RIGHT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.TOP;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.OsmandActionBarActivity;
import net.osmand.plus.activities.RestartActivity;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.InsetsUtils;
import net.osmand.plus.utils.InsetsUtils.InsetSide;
import net.osmand.plus.views.MapViewWithLayers;
import net.osmand.plus.views.OsmandMapTileView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SimpleMapActivity extends OsmandActionBarActivity {

	private OsmandApplication app;
	private OsmandMapTileView mapTileView;
	private MapViewWithLayers mapViewWithLayers;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_map_activity);
		mapViewWithLayers = findViewById(R.id.map_view_with_layers);

		app = (OsmandApplication) getApplication();

		mapTileView = app.getOsmandMap().getMapView();
		mapTileView.setupRenderingView();

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("Simple map");
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
	}
}
