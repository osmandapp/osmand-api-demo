package net.osmand.osmandapidemo

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*;

public class MainActivity : AppCompatActivity(), OsmAndHelper.OnOsmandMissingListener {
    var mOsmAndHelper: OsmAndHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOsmAndHelper = OsmAndHelper(this, REQUEST_OSMAND_API, this)

        setContentView(R.layout.activity_main)

        addFavoriteButton.setOnClickListener({
            getLocationSelectorInstance("Add favourite",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.addFavorite(lat, lon)
                    }).show(supportFragmentManager, null)
        })
        addMapMarkerButton.setOnClickListener({
            getLocationSelectorInstance("Add map marker",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.addMapMarker(lat, lon)
                    }).show(supportFragmentManager, null)
        })
        startAudioRecButton.setOnClickListener({
            getLocationSelectorInstance("Start audio recording",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.recordAudio(lat, lon)
                    }).show(supportFragmentManager, null)
        })
        startVideoRecButton.setOnClickListener({
            getLocationSelectorInstance("Start video recording",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.recordVideo(lat, lon)
                    }).show(supportFragmentManager, null)
        })
        takePhotoButton.setOnClickListener({
            getLocationSelectorInstance("Take photo",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.recordPhoto(lat, lon)
                    }).show(supportFragmentManager, null)
        })
        stopRecButton.setOnClickListener({ mOsmAndHelper!!.stopAvRec() })
        startGpxRecButton.setOnClickListener({ mOsmAndHelper!!.startGpxRec() })
        stopGpxRecButton.setOnClickListener({ mOsmAndHelper!!.stopGpxRec() })
        showGpxButton.setOnClickListener({ mOsmAndHelper!!.showGpx() })
        navigateGpxButton.setOnClickListener({ mOsmAndHelper!!.navigateGpx() })
        navigateButton.setOnClickListener({
            getLocationSelectorInstance("Navigate to",
                    { lat, lon, latStart, lonStart ->
                        mOsmAndHelper!!.navigate(latStart, lonStart, lat, lon)
                    }).show(supportFragmentManager, null)
        })
        getInfoButton.setOnClickListener({ mOsmAndHelper!!.getInfo() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OSMAND_API && resultCode == RESULT_OK) {
            val sb = StringBuilder()
            sb.append("ResultCode=").append(resultCodeStr(resultCode))
            val extras = data!!.extras
            if (extras != null && extras.size() > 0) {
                for (key in data.extras.keySet()) {
                    val `val` = extras.get(key)
                    if (sb.length > 0) {
                        sb.append("\n")
                    }
                    sb.append(key).append("=").append(`val`)
                }
            }
            val args = Bundle()
            args.putString(OsmAndInfoDialog.INFO_KEY, sb.toString())
            val infoDialog = OsmAndInfoDialog()
            infoDialog.arguments = args
            supportFragmentManager.beginTransaction()
                    .add(infoDialog, null).commitAllowingStateLoss()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun osmandMissing() {
        OsmAndMissingDialogFragment().show(supportFragmentManager, null);
    }

    private fun resultCodeStr(resultCode: Int): String {
        when (resultCode) {
            Activity.RESULT_OK -> return "OK"
            Activity.RESULT_CANCELED -> return "Canceled"
            Activity.RESULT_FIRST_USER -> return "First user"
            OsmAndHelper.RESULT_CODE_ERROR_UNKNOWN -> return "Unknown error"
            OsmAndHelper.RESULT_CODE_ERROR_NOT_IMPLEMENTED -> return "Feature is not implemented"
            OsmAndHelper.RESULT_CODE_ERROR_GPX_NOT_FOUND -> return "GPX not found"
            OsmAndHelper.RESULT_CODE_ERROR_INVALID_PROFILE -> return "Invalid profile"
            OsmAndHelper.RESULT_CODE_ERROR_PLUGIN_INACTIVE -> return "Plugin inactive"
        }
        return "" + resultCode
    }

    private fun getLocationSelectorInstance(title: String, callback: (Double, Double, Double, Double) -> Unit)
            : SelectLocationDialogFragment {
        return object : SelectLocationDialogFragment() {
            override fun locationSelectedCallback(lat: Double, lon: Double, latStart: Double, lonStart: Double) {
                callback(lat, lon, latStart, lonStart)
            }

            override fun getTitle(): String = title
        }
    }

    companion object {
        val REQUEST_INFO = 1;
        val REQUEST_OSMAND_API = 101
        private val GPX_NAME = "xxx.gpx"
    }
}

val CITIES = arrayOf(
        Location("Bruxelles - Brussel", 50.8465565, 4.351697, 50.83477, 4.4068823),
        Location("London", 51.5073219, -0.1276474, 51.52753, -0.07244986),
        Location("Paris", 48.8566101, 2.3514992, 48.87588, 2.428313),
        Location("Budapest", 47.4983815, 19.0404707, 47.48031, 19.067793),
        Location("Moscow", 55.7506828, 37.6174976, 55.769417, 37.698547),
        Location("Beijing", 39.9059631, 116.391248, 39.88707, 116.43207),
        Location("Tokyo", 35.6828378, 139.7589667, 35.72936, 139.703),
        Location("Washington", 38.8949549, -77.0366456, 38.91373, -77.02069),
        Location("Ottawa", 45.4210328, -75.6900219, 45.386864, -75.783356),
        Location("Panama", 8.9710438, -79.5340599, 8.992735, -79.5157))


class CitiesAdapter(context: Context) : ArrayAdapter<Location>(context, android.R.layout.simple_list_item_1, CITIES) {
    val mInflater = LayoutInflater.from(context)
    val icon: Drawable;

    init {
        icon = ContextCompat.getDrawable(context, R.drawable.ic_action_street_name);
        DrawableCompat.wrap(icon)
        icon.mutate()
        DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.iconColor))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = (convertView ?: mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)) as TextView
        view.text = getItem(position).name
        view.compoundDrawablePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8f, context.resources.displayMetrics).toInt()
        view.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        return view
    }
}

abstract class SelectLocationDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getTitle())
                .setAdapter(CitiesAdapter(activity), { dialogInterface, i ->
                    locationSelectedCallback(CITIES[i].lat, CITIES[i].lon, CITIES[i].latStart,
                            CITIES[i].lonStart)
                })
                .setNegativeButton("Cancel", null)
        return builder.create()
    }

    abstract fun locationSelectedCallback(lat: Double, lon: Double, latStart: Double, lonStart: Double)

    abstract fun getTitle(): String
}

class OsmAndMissingDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setView(R.layout.dialog_install_osm_and)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Install", { dialogInterface, i ->
                    val intent = Intent()
                    intent.data = Uri.parse("market://details?id=net.osmand.plus")
                    startActivity(intent)
                })
        return builder.create()
    }
}

class OsmAndInfoDialog : DialogFragment() {
    companion object {
        const val INFO_KEY = "info_key"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments.getString(INFO_KEY)
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
        builder.setTitle("OsmAnd info:")
        builder.setPositiveButton("OK", null)
        return builder.create()
    }
}