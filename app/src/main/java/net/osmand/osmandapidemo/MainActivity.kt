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
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*;
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

public class MainActivity : AppCompatActivity(), OsmAndHelper.OnOsmandMissingListener {
    var mOsmAndHelper: OsmAndHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOsmAndHelper = OsmAndHelper(this, REQUEST_OSMAND_API, this)

        paintDrawable(R.drawable.ic_action_fav_dark)
        paintDrawable(R.drawable.ic_action_flag_dark)
        paintDrawable(R.drawable.ic_action_micro_dark)
        paintDrawable(R.drawable.ic_action_video_dark)
        paintDrawable(R.drawable.ic_action_rec_stop)
        paintDrawable(R.drawable.ic_action_photo_dark)
        paintDrawable(R.drawable.ic_action_play)
        paintDrawable(R.drawable.ic_action_rec_stop)
        paintDrawable(R.drawable.ic_action_polygom_dark)
        paintDrawable(R.drawable.ic_action_gdirections_dark)
        paintDrawable(R.drawable.ic_action_gdirections_dark)
        paintDrawable(R.drawable.ic_action_gabout_dark)

        setContentView(R.layout.activity_main)

        addFavoriteButton.setOnClickListener({
            getLocationSelectorInstance("Add favourite",
                    { location ->
                        mOsmAndHelper!!.addFavorite(location.lat, location.lon, location.name,
                                location.name + " city", "Cities", "red", true);
                    }).show(supportFragmentManager, null)
        })
        addMapMarkerButton.setOnClickListener({
            getLocationSelectorInstance("Add map marker",
                    { location ->
                        mOsmAndHelper!!.addMapMarker(location.lat, location.lon, location.name)
                    }).show(supportFragmentManager, null)
        })
        startAudioRecButton.setOnClickListener({
            getLocationSelectorInstance("Start audio recording",
                    { location ->
                        mOsmAndHelper!!.recordAudio(location.lat, location.lon)
                    }).show(supportFragmentManager, null)
        })
        startVideoRecButton.setOnClickListener({
            getLocationSelectorInstance("Start video recording",
                    { location ->
                        mOsmAndHelper!!.recordVideo(location.lat, location.lon)
                    }).show(supportFragmentManager, null)
        })
        takePhotoButton.setOnClickListener({
            getLocationSelectorInstance("Take photo",
                    { location ->
                        mOsmAndHelper!!.recordPhoto(location.lat, location.lon)
                    }).show(supportFragmentManager, null)
        })
        stopRecButton.setOnClickListener({ mOsmAndHelper!!.stopAvRec() })
        startGpxRecButton.setOnClickListener({ mOsmAndHelper!!.startGpxRec() })
        stopGpxRecButton.setOnClickListener({ mOsmAndHelper!!.stopGpxRec() })
        showGpxButton.setOnClickListener({ requestChooseGpx(REQUEST_SHOW_GPX_FILE) })
        navigateGpxButton.setOnClickListener({ requestChooseGpx(REQUEST_NAVIGATE_GPX_FILE) })
        navigateButton.setOnClickListener({
            getLocationSelectorInstance("Navigate to",
                    { location ->
                        mOsmAndHelper!!.navigate(location.name + " start",
                                location.latStart, location.lonStart,
                                location.name + " finish", location.lat, location.lon, "bicycle")
                    }).show(supportFragmentManager, null)
        })
        getInfoButton.setOnClickListener({ mOsmAndHelper!!.getInfo() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == REQUEST_OSMAND_API) {
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
        }
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_NAVIGATE_GPX_FILE -> {
                    handleGpxFile(data!!, { data -> mOsmAndHelper!!.navigateRawGpx(true, data) })
                }
                REQUEST_SHOW_GPX_FILE -> {
                    handleGpxFile(data!!, { data -> mOsmAndHelper!!.showRawGpx(data) })
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun handleGpxFile(data: Intent, action: (String) -> Unit) {
        try {
            val gpxParceDescriptor = contentResolver.openFileDescriptor(data.data, "r")
            val fileDescriptor = gpxParceDescriptor.fileDescriptor
            val inputStreamReader = InputStreamReader(FileInputStream(fileDescriptor))
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            bufferedReader.lineSequence().forEach { string: String -> stringBuilder.append(string) }
            inputStreamReader.close()
            action(stringBuilder.toString());
        } catch (e: NullPointerException) {
            Log.e(TAG, "", e)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "", e)
        }
    }

    private fun requestChooseGpx(requestCode: Int) {
        var intent = Intent(Intent.ACTION_GET_CONTENT);
        intent.type = "*/*";
        intent = Intent.createChooser(intent, "Choose a file")
        if (mOsmAndHelper!!.isIntentSafe(intent)) {
            startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(this, "You need an app capable of selecting files like ES Explorer", Toast.LENGTH_LONG).show()
        }
    }

    override fun osmandMissing() {
        OsmAndMissingDialogFragment().show(supportFragmentManager, null);
    }

    fun paintDrawable(drawableRes: Int) {
        val icon = ContextCompat.getDrawable(this, drawableRes);
        val compatIcon = DrawableCompat.wrap(icon)
        DrawableCompat.setTint(compatIcon, ContextCompat.getColor(this, R.color.iconColor))
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

    private fun getLocationSelectorInstance(title: String, callback: (Location) -> Unit)
            : SelectLocationDialogFragment {
        return object : SelectLocationDialogFragment() {
            override fun locationSelectedCallback(location: Location) {
                callback(location)
            }

            override fun getTitle(): String = title
        }
    }

    companion object {
        private val TAG = "MainActivity"
        val REQUEST_OSMAND_API = 101
        val REQUEST_NAVIGATE_GPX_FILE = 102
        val REQUEST_SHOW_GPX_FILE = 103
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
        Location("Panama", 8.9710438, -79.5340599, 8.992735, -79.5157),
        Location("Minsk", 53.9072394, 27.5863608, 53.9022545, 27.5619212),
        Location("Amsterdam", 52.3704312, 4.8904288, 52.3693012, 4.9013307)
)


class CitiesAdapter(context: Context) : ArrayAdapter<Location>(context, R.layout.simple_list_layout, CITIES) {
    val mInflater = LayoutInflater.from(context)
    val icon: Drawable;

    init {
        val tempIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_street_name);
        icon = DrawableCompat.wrap(tempIcon)
        DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.iconColor))
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = (convertView ?: mInflater.inflate(R.layout.simple_list_layout, parent, false)) as TextView
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
                    locationSelectedCallback(CITIES[i])
                })
                .setNegativeButton("Cancel", null)
        return builder.create()
    }

    abstract fun locationSelectedCallback(location: Location)

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