package net.osmand.library.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.osmand.plus.OsmandApplication;

public class MainActivity extends AppCompatActivity {

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
}