package net.osmand.osmandapidemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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
        navigateButton.setOnClickListener({ mOsmAndHelper.navigate(LAT, LON, DEST_LAT, DEST_LON) })
        getInfoButton.setOnClickListener({ mOsmAndHelper.getInfo() })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_OSMAND_API) {
            val view = findViewById(R.id.main_view)
            if (view != null) {
                val sb = StringBuilder()
                sb.append("ResultCode=").append(resultCodeStr(resultCode))
                val extras = data.extras
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