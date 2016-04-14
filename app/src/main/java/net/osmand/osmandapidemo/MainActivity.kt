package net.osmand.osmandapidemo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import kotlinx.android.synthetic.main.activity_main.*;

public class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mOsmAndHelper = OsmAndHelper(this, REQUEST_OSMAND_API)

        setContentView(R.layout.activity_main)

        addFavoriteButton.setOnClickListener({ mOsmAndHelper.addFavorite(LAT, LON) })
        addMapMarkerButton.setOnClickListener({ mOsmAndHelper.addMapMarker(LAT, LON) })
        startAudioRecButton.setOnClickListener({ mOsmAndHelper.recordAudio(LAT, LON) })
        startVideoRecButton.setOnClickListener({ mOsmAndHelper.recordVideo(LAT, LON) })
        stopRecButton.setOnClickListener({ mOsmAndHelper.stopAvRec() })
        takePhotoButton.setOnClickListener({ mOsmAndHelper.recordPhoto(LAT, LON) })
        startGpxRecButton.setOnClickListener({ mOsmAndHelper.startGpxRec() })
        stopGpxRecButton.setOnClickListener({ mOsmAndHelper.stopGpxRec() })
        showGpxButton.setOnClickListener({ mOsmAndHelper.showGpx() })
        navigateGpxButton.setOnClickListener({ mOsmAndHelper.navigateGpx() })
        navigateButton.setOnClickListener({
            object : SelectLocationDialogFragment() {
                override fun locationSelectedCallback(lat: Double, lon: Double, latStart: Double, lonStart: Double) {
                    mOsmAndHelper.navigate(latStart, lonStart, lat, lon)
                    Log.d("dickbut","dickbut")
                }

                override fun getTitle(): String = "Navigate to"
            }.show(supportFragmentManager, null)
        })
        getInfoButton.setOnClickListener({ mOsmAndHelper.getInfo() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OSMAND_API && resultCode == RESULT_OK) {
            val view = findViewById(R.id.main_view)
            if (view != null) {
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

                val builder = AlertDialog.Builder(this)
                builder.setMessage(sb.toString())
                builder.setPositiveButton("OK", null)
                builder.create().show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun resultCodeStr(resultCode: Int): String {
        when (resultCode) {
            RESULT_CODE_OK -> return "OK"
            RESULT_CODE_ERROR_UNKNOWN -> return "Unknown error"
            RESULT_CODE_ERROR_NOT_IMPLEMENTED -> return "Feature is not implemented"
            RESULT_CODE_ERROR_GPX_NOT_FOUND -> return "GPX not found"
            RESULT_CODE_ERROR_INVALID_PROFILE -> return "Invalid profile"
            RESULT_CODE_ERROR_PLUGIN_INACTIVE -> return "Plugin inactive"
        }
        return "" + resultCode
    }

    companion object {
        val REQUEST_OSMAND_API = 101

        val RESULT_CODE_OK = 0
        val RESULT_CODE_ERROR_UNKNOWN = -1
        val RESULT_CODE_ERROR_NOT_IMPLEMENTED = -2
        val RESULT_CODE_ERROR_PLUGIN_INACTIVE = 10
        val RESULT_CODE_ERROR_GPX_NOT_FOUND = 20
        val RESULT_CODE_ERROR_INVALID_PROFILE = 30

        private val LAT = "44.98062"
        private val LON = "34.09258"
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
        icon.setTint(ContextCompat.getColor(context, R.color.iconColor))
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