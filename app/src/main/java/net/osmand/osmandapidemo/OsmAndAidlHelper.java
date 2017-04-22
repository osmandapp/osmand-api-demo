package main.java.net.osmand.osmandapidemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import net.osmand.aidl.ALatLon;
import net.osmand.aidl.IOsmAndAidlInterface;
import net.osmand.aidl.mapmarker.AMapMarker;
import net.osmand.aidl.mapmarker.AddMapMarkerParams;
import net.osmand.aidl.mapmarker.RemoveMapMarkerParams;
import net.osmand.aidl.mapmarker.UpdateMapMarkerParams;
import net.osmand.aidl.mapwidget.AMapWidget;
import net.osmand.aidl.mapwidget.AddMapWidgetParams;
import net.osmand.aidl.mapwidget.RemoveMapWidgetParams;
import net.osmand.aidl.mapwidget.UpdateMapWidgetParams;

import main.java.net.osmand.osmandapidemo.OsmAndHelper.OnOsmandMissingListener;

public class OsmAndAidlHelper {

	private final Activity mActivity;
	private final OnOsmandMissingListener mOsmandMissingListener;
	private IOsmAndAidlInterface mIOsmAndAidlInterface;

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service.  We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mIOsmAndAidlInterface = IOsmAndAidlInterface.Stub.asInterface(service);
			Toast.makeText(mActivity, "OsmAnd service connected", Toast.LENGTH_SHORT).show();
		}
		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mIOsmAndAidlInterface = null;
			Toast.makeText(mActivity, "OsmAnd service disconnected", Toast.LENGTH_SHORT).show();
		}
	};

	public OsmAndAidlHelper(Activity activity, OnOsmandMissingListener listener) {
		mActivity = activity;
		mOsmandMissingListener = listener;
		bindService();
	}

	private boolean bindService() {
		if (mIOsmAndAidlInterface == null) {
			Intent intent = new Intent("net.osmand.aidl.OsmandAidlService");
			intent.setPackage("net.osmand.plus");
			boolean res = mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
			if (res) {
				Toast.makeText(mActivity, "OsmAnd service bind", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				Toast.makeText(mActivity, "OsmAnd service NOT bind", Toast.LENGTH_SHORT).show();
				return false;
			}
		} else {
			return true;
		}
	}

	public void cleanupResources() {
		if (mIOsmAndAidlInterface != null) {
			mActivity.unbindService(mConnection);
		}
	}

	/**
	 * Add map marker at given location.
	 *
	 * @param lat  - latitude. Sent as URI parameter.
	 * @param lon  - longitude. Sent as URI parameter.
	 * @param name - name. Sent as URI parameter.
	 */
	public boolean addMapMarker(double lat, double lon, String name) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker marker = new AMapMarker(new ALatLon(lat, lon), name);
				return mIOsmAndAidlInterface.addMapMarker(new AddMapMarkerParams(marker));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean updateMapMarker(double latPrev, double lonPrev, String namePrev,
								   double latNew, double lonNew, String nameNew) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker markerPrev = new AMapMarker(new ALatLon(latPrev, lonPrev), namePrev);
				AMapMarker markerNew = new AMapMarker(new ALatLon(latNew, lonNew), nameNew);
				return mIOsmAndAidlInterface.updateMapMarker(new UpdateMapMarkerParams(markerPrev, markerNew));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean removeMapMarker(double lat, double lon, String name) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapMarker marker = new AMapMarker(new ALatLon(lat, lon), name);
				return mIOsmAndAidlInterface.removeMapMarker(new RemoveMapMarkerParams(marker));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Add map widget to the right side of the main screen.
	 *
	 * @param id - unique id.
	 */
	public boolean addMapWidget(String id, String menuIconName, String menuTitle,
								String lightIconName, String darkIconName, String text, String description,
								int order, Intent intentOnClick) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapWidget widget = new AMapWidget(id, menuIconName, menuTitle, lightIconName,
						darkIconName, text, description, order, intentOnClick);
				return mIOsmAndAidlInterface.addMapWidget(new AddMapWidgetParams(widget));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean updateMapWidget(String id, String menuIconName, String menuTitle,
								String lightIconName, String darkIconName, String text, String description,
								int order, Intent intentOnClick) {
		if (mIOsmAndAidlInterface != null) {
			try {
				AMapWidget widget = new AMapWidget(id, menuIconName, menuTitle, lightIconName,
						darkIconName, text, description, order, intentOnClick);
				return mIOsmAndAidlInterface.updateMapWidget(new UpdateMapWidgetParams(widget));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean removeMapWidget(String id) {
		if (mIOsmAndAidlInterface != null) {
			try {
				return mIOsmAndAidlInterface.removeMapWidget(new RemoveMapWidgetParams(id));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
