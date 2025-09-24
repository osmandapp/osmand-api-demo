package net.osmand.library.sample;

import static net.osmand.plus.utils.InsetsUtils.InsetSide.BOTTOM;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.LEFT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.RIGHT;
import static net.osmand.plus.utils.InsetsUtils.InsetSide.TOP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.OsmandActionBarActivity;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.utils.InsetsUtils;
import net.osmand.plus.utils.InsetsUtils.InsetSide;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends OsmandActionBarActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle(getString(R.string.app_name));

		OsmandApplication app = (OsmandApplication) getApplication();

		findViewById(R.id.download_maps_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, app.getAppCustomization().getDownloadActivity());
			startActivity(newIntent);
		});

		findViewById(R.id.simple_map_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, SimpleMapActivity.class);
			startActivity(newIntent);
		});

		findViewById(R.id.navigate_map_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, NavigateMapActivity.class);
			startActivity(newIntent);
		});

		findViewById(R.id.points_on_map_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, PointsOnMapActivity.class);
			startActivity(newIntent);
		});

		findViewById(R.id.full_map_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, FullMapActivity.class);
			startActivity(newIntent);
		});
	}

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
}