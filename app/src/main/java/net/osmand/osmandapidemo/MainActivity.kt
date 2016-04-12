package net.osmand.osmandapidemo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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
                override fun locationSelectedCallback(lat: Double, lon: Double) {
                    mOsmAndHelper.navigate(LAT, LON, lat, lon)
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
        private val DEST_LAT = "44.97799"
        private val DEST_LON = "34.10286"
        private val GPX_NAME = "xxx.gpx"
    }
}

val CITIES = arrayOf(
        Location("Bruxelles - Brussel", 50.8465565, 4.351697),
        Location("London", 51.5073219, -0.1276474),
        Location("Paris", 48.8566101, 2.3514992),
        Location("Budapest", 47.4983815, 19.0404707),
        Location("Moscow", 55.7506828, 37.6174976),
        Location("Beijing", 39.9059631, 116.391248),
        Location("Tokyo", 35.6828378, 139.7589667),
        Location("Washington", 38.8949549, -77.0366456),
        Location("Ottawa", 45.4210328, -75.6900219),
        Location("Panama", 8.9710438, -79.5340599))

class CitiesAdapter(context: Context) : ArrayAdapter<Location>(context, android.R.layout.simple_list_item_1, CITIES) {
    val mInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = (convertView ?: mInflater.inflate(android.R.layout.simple_list_item_1, parent, false)) as TextView
        view.text = getItem(position).name
        //        view.setCompoundDrawablesWithIntrinsicBounds()
        return view
    }
}

abstract class SelectLocationDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getTitle())
                .setAdapter(CitiesAdapter(activity), { dialogInterface, i ->
                    locationSelectedCallback(CITIES[i].lat, CITIES[i].lon)
                })
                .setNegativeButton("Cancel", null)
        return builder.create()
    }

    abstract fun locationSelectedCallback(lat: Double, lon: Double)

    abstract fun getTitle(): String
}