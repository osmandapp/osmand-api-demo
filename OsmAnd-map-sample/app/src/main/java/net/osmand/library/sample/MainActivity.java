package net.osmand.library.sample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.osmand.Location;
import net.osmand.SecondSplashScreenFragment;
import net.osmand.core.android.AtlasMapRendererView;
import net.osmand.data.LatLon;
import net.osmand.data.QuadPoint;
import net.osmand.data.RotatedTileBox;
import net.osmand.map.MapTileDownloader;
import net.osmand.map.MapTileDownloader.IMapDownloaderCallback;
import net.osmand.plus.AppInitializer;
import net.osmand.plus.AppInitializer.AppInitializeListener;
import net.osmand.plus.AppInitializer.InitEvents;
import net.osmand.plus.OsmAndConstants;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.ProgressImplementation;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.base.MapViewTrackingUtilities;
import net.osmand.plus.dialogs.WhatsNewDialogFragment;
import net.osmand.plus.helpers.AndroidUiHelper;
import net.osmand.plus.helpers.ScrollHelper;
import net.osmand.plus.helpers.ScrollHelper.OnScrollEventListener;
import net.osmand.plus.resources.ResourceManager;
import net.osmand.plus.settings.backend.OsmandSettings;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.views.AnimateDraggingMapThread;
import net.osmand.plus.views.OsmAndMapLayersView;
import net.osmand.plus.views.OsmAndMapSurfaceView;
import net.osmand.plus.views.OsmandMap.OsmandMapListener;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.corenative.NativeCoreContext;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MainActivity extends MapActivity implements OnScrollEventListener, OsmandMapListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getMyApplication().getSettings().USE_OPENGL_RENDER.set(true);
	}
}