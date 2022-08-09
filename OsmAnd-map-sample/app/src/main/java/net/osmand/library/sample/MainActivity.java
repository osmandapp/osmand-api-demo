package net.osmand.library.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.osmand.plus.AppInitializer;
import net.osmand.plus.AppInitializer.AppInitializeListener;
import net.osmand.plus.AppInitializer.InitEvents;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.activities.RestartActivity;
import net.osmand.plus.views.MapViewWithLayers;
import net.osmand.plus.views.OsmandMap.OsmandMapListener;
import net.osmand.plus.views.corenative.NativeCoreContext;

public class MainActivity extends AppCompatActivity implements OsmandMapListener {

	private OsmandApplication app;

	private MapViewWithLayers mapViewWithLayers;
	private AppInitializeListener initListener;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapViewWithLayers = findViewById(R.id.map_view_with_layers);

		app = (OsmandApplication) getApplication();
		app.getOsmandMap().addListener(this);

		CompoundButton openglSwitch = findViewById(R.id.opengl_switch);
		openglSwitch.setChecked(app.getSettings().USE_OPENGL_RENDER.get());
		openglSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
			app.getSettings().USE_OPENGL_RENDER.set(isChecked);
			RestartActivity.doRestart(this);
		});

		findViewById(R.id.download_maps_button).setOnClickListener(v -> {
			Intent newIntent = new Intent(MainActivity.this, app.getAppCustomization().getDownloadActivity());
			startActivity(newIntent);
		});

		checkAppInitialization();
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