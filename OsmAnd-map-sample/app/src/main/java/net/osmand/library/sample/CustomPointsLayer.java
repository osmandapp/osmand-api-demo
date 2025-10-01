package net.osmand.library.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.osmand.core.android.MapRendererView;
import net.osmand.core.jni.TextRasterizer;
import net.osmand.data.FavouritePoint;
import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.data.QuadRect;
import net.osmand.data.QuadTree;
import net.osmand.data.RotatedTileBox;
import net.osmand.plus.views.OsmandMapTileView;
import net.osmand.plus.views.PointImageDrawable;
import net.osmand.plus.views.PointImageUtils;
import net.osmand.plus.views.layers.ContextMenuLayer.IContextMenuProvider;
import net.osmand.plus.views.layers.MapSelectionResult;
import net.osmand.plus.views.layers.MapSelectionRules;
import net.osmand.plus.views.layers.MapTextLayer;
import net.osmand.plus.views.layers.base.OsmandMapLayer;
import net.osmand.plus.views.layers.core.FavoritesTileProvider;

import java.util.ArrayList;
import java.util.List;

public class CustomPointsLayer extends OsmandMapLayer implements IContextMenuProvider {

	private static final int START_ZOOM = 3;

	private final List<FavouritePoint> favouritePoints = new ArrayList<>();

	//OpenGl
	private boolean favoritesDrawn;
	private FavoritesTileProvider mapLayerProvider;

	@ColorInt
	private int defaultColor;

	public CustomPointsLayer(@NonNull Context ctx, @NonNull List<FavouritePoint> points) {
		super(ctx);
		favouritePoints.addAll(points);
	}

	@Override
	public void initLayer(@NonNull OsmandMapTileView view) {
		super.initLayer(view);
		defaultColor = view.getResources().getColor(net.osmand.plus.R.color.profile_icon_color_green_light);
	}

	@Override
	public void destroyLayer() {
		super.destroyLayer();
		clearOpenGlPoints();
	}

	@Override
	public void onDraw(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {

	}

	@Override
	public void onPrepareBufferImage(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {
		if (hasMapRenderer()) {
			if (mapActivityInvalidated || !favoritesDrawn) {
				favoritesDrawn = true;
				showOpenGlPoints();
			}
		} else if (tileBox.getZoom() >= START_ZOOM) {
			drawLegacyPoints(canvas, tileBox, settings);
		}
		mapActivityInvalidated = false;
	}

	public void showOpenGlPoints() {
		MapRendererView mapRenderer = getMapRenderer();
		if (mapRenderer == null) {
			return;
		}
		clearOpenGlPoints();

		float textScale = getTextScale();
		TextRasterizer.Style textStyle = MapTextLayer.getTextStyle(getContext(), false, textScale, view.getDensity());
		mapLayerProvider = new FavoritesTileProvider(getContext(), getBaseOrder(), true, textStyle, view.getDensity());

		for (FavouritePoint favoritePoint : favouritePoints) {
			mapLayerProvider.addToData(favoritePoint, defaultColor, true, false, textScale);
		}
		mapLayerProvider.drawSymbols(mapRenderer);
	}

	public void clearOpenGlPoints() {
		MapRendererView mapRenderer = getMapRenderer();
		if (mapRenderer == null || mapLayerProvider == null) {
			return;
		}
		mapLayerProvider.clearSymbols(mapRenderer);
		mapLayerProvider = null;
	}

	public void drawLegacyPoints(Canvas canvas, RotatedTileBox tileBox, DrawSettings settings) {
		float textScale = getTextScale();
		float iconSize = getIconSize(view.getApplication());
		QuadTree<QuadRect> boundIntersections = initBoundIntersections(tileBox);

		// request to load
		QuadRect latLonBounds = tileBox.getLatLonBounds();
		List<LatLon> fullObjectsLatLon = new ArrayList<>();
		List<LatLon> smallObjectsLatLon = new ArrayList<>();
		List<FavouritePoint> fullObjects = new ArrayList<>();
		for (FavouritePoint point : favouritePoints) {
			double lat = point.getLatitude();
			double lon = point.getLongitude();
			if (lat >= latLonBounds.bottom && lat <= latLonBounds.top
					&& lon >= latLonBounds.left && lon <= latLonBounds.right) {

				float x = tileBox.getPixXFromLatLon(lat, lon);
				float y = tileBox.getPixYFromLatLon(lat, lon);

				if (intersects(boundIntersections, x, y, iconSize, iconSize)) {
					PointImageDrawable pointImageDrawable = PointImageUtils.getFromPoint(getContext(), defaultColor, true, point);
					pointImageDrawable.drawSmallPoint(canvas, x, y, textScale);
					smallObjectsLatLon.add(new LatLon(lat, lon));
				} else {
					fullObjects.add(point);
					fullObjectsLatLon.add(new LatLon(lat, lon));
				}
			}
		}
		for (FavouritePoint favoritePoint : fullObjects) {
			float x = tileBox.getPixXFromLatLon(favoritePoint.getLatitude(), favoritePoint.getLongitude());
			float y = tileBox.getPixYFromLatLon(favoritePoint.getLatitude(), favoritePoint.getLongitude());

			PointImageDrawable pointImageDrawable = PointImageUtils.getFromPoint(getContext(), defaultColor, true, favoritePoint);
			pointImageDrawable.drawPoint(canvas, x, y, textScale, false);
		}
		this.fullObjectsLatLon = fullObjectsLatLon;
		this.smallObjectsLatLon = smallObjectsLatLon;
	}

	@Override
	public boolean drawInScreenPixels() {
		return true;
	}

	@Override
	public PointDescription getObjectName(Object o) {
		return null;
	}

	@Override
	public boolean disableSingleTap() {
		return false;
	}

	@Override
	public boolean disableLongPressOnMap(PointF point, RotatedTileBox tileBox) {
		return false;
	}

	@Override
	public boolean runExclusiveAction(Object o, boolean unknownLocation) {
		return false;
	}

	@Override
	public boolean showMenuAction(@Nullable Object o) {
		return false;
	}

	@Override
	public void collectObjectsFromPoint(@NonNull MapSelectionResult mapSelectionResult,
			@NonNull MapSelectionRules mapSelectionRules) {

	}

	@Override
	public LatLon getObjectLocation(Object o) {
		return null;
	}
}