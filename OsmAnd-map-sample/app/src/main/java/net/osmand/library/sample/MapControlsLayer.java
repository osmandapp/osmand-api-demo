package net.osmand.library.sample;

import android.Manifest;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.osmand.Location;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.OsmAndLocationProvider;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.utils.AndroidUtils;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.layers.base.OsmandMapLayer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;

import static net.osmand.library.sample.MainActivity.PERMISSION_REQUEST_LOCATION_ON_BUTTON;

public class MapControlsLayer extends OsmandMapLayer {

	private static final String BACK_TO_LOC_HUD_ID = "backToLocationButton";
	private static final String ZOOM_IN_HUD_ID = "zoomInButton";
	private static final String ZOOM_OUT_HUD_ID = "zoomOutButton";

	private final MainActivity activity;
	private final List<MapHudButton> controls = new ArrayList<>();

	private MapHudButton backToLocationControl;
	private MapHudButton mapZoomOut;
	private MapHudButton mapZoomIn;

	public MapControlsLayer(MainActivity activity) {
		super(activity);
		this.activity = activity;
	}

	@Override
	public void destroyLayer() {
		controls.clear();
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings nightMode) {
		updateControls(tileBox, nightMode);
	}

	@Override
	public boolean drawInScreenPixels() {
		return true;
	}

	@Override
	public void initLayer(final OsmandMapTileView view) {
		initControls();
		updateControls(view.getCurrentRotatedTileBox(), null);
	}

	public SampleApplication getApplication() {
		return activity.getMyApplication();
	}

	private void initControls() {
		View backToLocation = activity.findViewById(net.osmand.plus.R.id.map_my_location_button);
		backToLocationControl = setupBackToLocationButton(backToLocation, BACK_TO_LOC_HUD_ID);
		View zoomInButton = activity.findViewById(net.osmand.plus.R.id.map_zoom_in_button);
		View zoomOutButton = activity.findViewById(net.osmand.plus.R.id.map_zoom_out_button);
		mapZoomIn = setupZoomInButton(zoomInButton, ZOOM_IN_HUD_ID);
		mapZoomOut = setupZoomOutButton(zoomOutButton, ZOOM_OUT_HUD_ID);
	}

	public MapHudButton setupBackToLocationButton(View backToLocation, String buttonId) {
		MapHudButton backToLocationButton = createHudButton(backToLocation, net.osmand.plus.R.drawable.ic_my_location, buttonId)
				.setIconColorId(net.osmand.plus.R.color.map_button_icon_color_light, net.osmand.plus.R.color.map_button_icon_color_dark)
				.setBg(net.osmand.plus.R.drawable.btn_circle_blue);

		backToLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackToLocation();
			}
		});

		controls.add(backToLocationButton);
		return backToLocationButton;
	}

	private void onBackToLocation() {
		if (OsmAndLocationProvider.isLocationPermissionAvailable(activity)) {
			getApplication().getMapViewTrackingUtilities().backToLocationImpl();
		} else {
			ActivityCompat.requestPermissions(activity,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
					PERMISSION_REQUEST_LOCATION_ON_BUTTON);
		}
	}

	public MapHudButton setupZoomOutButton(View zoomOutButton, String buttonId) {
		MapHudButton mapZoomOutButton = createHudButton(zoomOutButton, net.osmand.plus.R.drawable.ic_zoom_out, buttonId);
		mapZoomOutButton.setRoundTransparent();
		zoomOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.changeZoom(-1, System.currentTimeMillis());
			}
		});
		controls.add(mapZoomOutButton);

		return mapZoomOutButton;
	}

	public MapHudButton setupZoomInButton(View zoomInButton, String buttonId) {
		MapHudButton mapZoomInButton = createHudButton(zoomInButton, net.osmand.plus.R.drawable.ic_zoom_in, buttonId);
		mapZoomInButton.setRoundTransparent();
		zoomInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (activity.getMapView().isZooming()) {
					activity.changeZoom(2, System.currentTimeMillis());
				} else {
					activity.changeZoom(1, System.currentTimeMillis());
				}
			}
		});
		controls.add(mapZoomInButton);

		return mapZoomInButton;
	}

	private void updateControls(@NonNull RotatedTileBox tileBox, DrawSettings drawSettings) {
		boolean isNight = false;
		backToLocationControl.updateVisibility(true);
		mapZoomIn.updateVisibility(true);
		mapZoomOut.updateVisibility(true);

		for (MapHudButton mc : controls) {
			if (mc.id.startsWith(BACK_TO_LOC_HUD_ID)) {
				updateMyLocation(mc);
			}
			mc.update(getApplication(), isNight);
		}
	}

	private void updateMyLocation(MapHudButton backToLocationControl) {
		SampleApplication app = getApplication();
		Location lastKnownLocation = app.getLocationProvider().getLastKnownLocation();
		boolean enabled = lastKnownLocation != null;
		boolean tracked = app.getMapViewTrackingUtilities().isMapLinkedToLocation();
		if (!enabled) {
			backToLocationControl.setBg(net.osmand.plus.R.drawable.btn_circle, net.osmand.plus.R.drawable.btn_circle_night);
			backToLocationControl.setIconColorId(net.osmand.plus.R.color.map_button_icon_color_light, net.osmand.plus.R.color.map_button_icon_color_dark);
			backToLocationControl.iv.setContentDescription(activity.getString(net.osmand.plus.R.string.unknown_location));
		} else if (tracked) {
			backToLocationControl.setBg(net.osmand.plus.R.drawable.btn_circle, net.osmand.plus.R.drawable.btn_circle_night);
			backToLocationControl.setIconColorId(net.osmand.plus.R.color.color_myloc_distance);
			backToLocationControl.iv.setContentDescription(activity.getString(net.osmand.plus.R.string.access_map_linked_to_location));
		} else {
			backToLocationControl.setIconColorId(0);
			backToLocationControl.setBg(net.osmand.plus.R.drawable.btn_circle_blue);
			backToLocationControl.iv.setContentDescription(activity.getString(net.osmand.plus.R.string.map_widget_back_to_loc));
		}
	}

	public void showMapControlsIfHidden() {
		if (!isMapControlsVisible()) {
			showMapControls();
		}
	}

	public boolean isMapControlsVisible() {
		return activity.findViewById(net.osmand.plus.R.id.MapHudButtonsOverlay).getVisibility() == View.VISIBLE;
	}

	private void showMapControls() {
		activity.findViewById(net.osmand.plus.R.id.MapHudButtonsOverlay).setVisibility(View.VISIBLE);
		AndroidUtils.showNavBar(activity);
	}

	public void hideMapControls() {
		activity.findViewById(net.osmand.plus.R.id.MapHudButtonsOverlay).setVisibility(View.INVISIBLE);
	}

	public MapHudButton createHudButton(View iv, int resId, String id) {
		MapHudButton mc = new MapHudButton();
		mc.iv = iv;
		mc.resId = resId;
		mc.id = id;
		return mc;
	}

	public class MapHudButton {

		private View iv;
		private int bgDark;
		private int bgLight;
		private int resId;
		private int resLightId;
		private int resDarkId;
		private int resClrLight = net.osmand.plus.R.color.map_button_icon_color_light;
		private int resClrDark = net.osmand.plus.R.color.map_button_icon_color_dark;
		private String id;
		private boolean flipIconForRtl;

		private boolean nightMode = false;
		private boolean f = true;

		public MapHudButton setRoundTransparent() {
			setBg(net.osmand.plus.R.drawable.btn_circle_trans, net.osmand.plus.R.drawable.btn_circle_night);
			return this;
		}

		public MapHudButton setBg(int dayBg, int nightBg) {
			if (bgDark == nightBg && dayBg == bgLight) {
				return this;
			}
			bgDark = nightBg;
			bgLight = dayBg;
			f = true;
			return this;
		}

		public boolean updateVisibility(boolean visible) {
			if (visible != (iv.getVisibility() == View.VISIBLE)) {
				iv.setVisibility(visible ? View.VISIBLE : View.GONE);
				iv.invalidate();
				return true;
			}
			return false;
		}

		public MapHudButton setBg(int bg) {
			if (bgDark == bg && bg == bgLight) {
				return this;
			}
			bgDark = bg;
			bgLight = bg;
			f = true;
			return this;
		}

		public boolean setIconResId(int resId) {
			if (this.resId == resId) {
				return false;
			}
			this.resId = resId;
			f = true;
			return true;
		}

		public boolean resetIconColors() {
			if (resClrLight == net.osmand.plus.R.color.map_button_icon_color_light && resClrDark == net.osmand.plus.R.color.map_button_icon_color_dark) {
				return false;
			}
			resClrLight = net.osmand.plus.R.color.map_button_icon_color_light;
			resClrDark = net.osmand.plus.R.color.map_button_icon_color_dark;
			f = true;
			return true;
		}

		public MapHudButton setIconColorId(int clr) {
			if (resClrLight == clr && resClrDark == clr) {
				return this;
			}
			resClrLight = clr;
			resClrDark = clr;
			f = true;
			return this;
		}

		public MapHudButton setIconsId(int icnLight, int icnDark) {
			if (resLightId == icnLight && resDarkId == icnDark) {
				return this;
			}
			resLightId = icnLight;
			resDarkId = icnDark;
			f = true;
			return this;
		}

		public MapHudButton setIconColorId(int clrLight, int clrDark) {
			if (resClrLight == clrLight && resClrDark == clrDark) {
				return this;
			}
			resClrLight = clrLight;
			resClrDark = clrDark;
			f = true;
			return this;
		}

		public void update(OsmandApplication ctx, boolean night) {
			if (nightMode == night && !f) {
				return;
			}
			f = false;
			nightMode = night;
			if (bgDark != 0 && bgLight != 0) {
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
					iv.setBackground(AppCompatResources.getDrawable(activity, night ? bgDark : bgLight));
				} else {
					iv.setBackgroundDrawable(AppCompatResources.getDrawable(activity, night ? bgDark : bgLight));
				}
			}
			Drawable d = null;
			if (resDarkId != 0 && nightMode) {
				d = ctx.getUIUtilities().getIcon(resDarkId);
			} else if (resLightId != 0 && !nightMode) {
				d = ctx.getUIUtilities().getIcon(resLightId);
			} else if (resId != 0) {
				d = ctx.getUIUtilities().getIcon(resId, nightMode ? resClrDark : resClrLight);
				if (flipIconForRtl) {
					d = AndroidUtils.getDrawableForDirection(ctx, d);
				}
			}
			if (iv instanceof ImageView) {
				setMapButtonIcon((ImageView) iv, d);
			} else if (iv instanceof TextView) {
				((TextView) iv).setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
			}
		}
	}
}
