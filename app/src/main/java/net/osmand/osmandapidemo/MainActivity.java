package net.osmand.osmandapidemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
	public static final int REQUEST_OSMAND_API = 101;

	public static final int RESULT_CODE_OK = 0;
	public static final int RESULT_CODE_ERROR_UNKNOWN = -1;
	public static final int RESULT_CODE_ERROR_NOT_IMPLEMENTED = -2;
	public static final int RESULT_CODE_ERROR_PLUGIN_INACTIVE = 10;
	public static final int RESULT_CODE_ERROR_GPX_NOT_FOUND = 20;
	public static final int RESULT_CODE_ERROR_INVALID_PROFILE = 30;

	private static String LAT = "44.98062";
	private static String LON = "34.09258";
	private static String DEST_LAT = "44.97799";
	private static String DEST_LON = "34.10286";
	private static String GPX_NAME = "xxx.gpx";

	private OsmAndHelper mOsmAndHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mOsmAndHelper = new OsmAndHelper(this, REQUEST_OSMAND_API);

		setContentView(R.layout.activity_main);

		Button btn = (Button) findViewById(R.id.btn_add_favorite);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.addFavorite(LAT, LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_add_map_marker);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.addMapMarker(LAT, LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_start_audio_rec);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.recordAudio(LAT, LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_start_video_rec);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.recordVideo(LAT, LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_stop_rec);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.stopAvRec();
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_take_photo);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.recordPhoto(LAT, LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_start_gpx_rec);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.startGpxRec();
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_stop_gpx_rec);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.stopGpxRec();
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_show_gpx);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.showGpx();
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_navigate_gpx);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.navigateGpx();
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_navigate);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.navigate(LAT, LON, DEST_LAT, DEST_LON);
				}
			});
		}

		btn = (Button) findViewById(R.id.btn_get_info);
		if (btn != null) {
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mOsmAndHelper.getInfo();
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_OSMAND_API) {
			View view = findViewById(R.id.main_view);
			if (view != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("ResultCode=").append(resultCodeStr(resultCode));
				Bundle extras = data.getExtras();
				if (extras != null && extras.size() > 0) {
					for (String key : data.getExtras().keySet()) {
						Object val = extras.get(key);
						if (sb.length() > 0) {
							sb.append("\n");
						}
						sb.append(key).append("=").append(val);
					}
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(sb.toString());
				builder.setPositiveButton("OK", null);
				builder.create().show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private String resultCodeStr(int resultCode) {
		switch (resultCode) {
			case RESULT_CODE_OK:
				return "OK";
			case RESULT_CODE_ERROR_UNKNOWN:
				return "Unknown error";
			case RESULT_CODE_ERROR_NOT_IMPLEMENTED:
				return "Feature is not implemented";
			case RESULT_CODE_ERROR_GPX_NOT_FOUND:
				return "GPX not found";
			case RESULT_CODE_ERROR_INVALID_PROFILE:
				return "Invalid profile";
			case RESULT_CODE_ERROR_PLUGIN_INACTIVE:
				return "Plugin inactive";
		}
		return "" + resultCode;
	}
}
