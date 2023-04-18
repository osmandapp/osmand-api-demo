package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import main.java.net.osmand.osmandapidemo.MainActivity
import net.osmand.osmandapidemo.R

class OpenGpxDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "OpenGpxDialogFragment"
        const val SEND_AS_RAW_DATA_REQUEST_CODE_KEY = "send_as_raw_data_request_code_key"
        const val SEND_AS_URI_REQUEST_CODE_KEY = "send_as_uri_request_code_key"
    }

    private var sendAsRawDataRequestCode: Int? = null
    private var sendAsUriDataRequestCode: Int? = null
    
    private lateinit var btnNavigateFromStart: View
    private lateinit var btnNavigateFromNearest: View
    private lateinit var btnSnapToRoad: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sendAsRawDataRequestCode = arguments?.getInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY)
        sendAsUriDataRequestCode = arguments?.getInt(SEND_AS_URI_REQUEST_CODE_KEY)

        val ctx = requireContext()
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_open_gpx, null)
        val builder = AlertDialog.Builder(ctx)
        builder.setView(view)
        builder.setTitle("Send GPX to OsmAnd as raw data or as URI")
        builder.setNeutralButton("As raw data") { _, _ -> sendAsRawData() }
        builder.setPositiveButton("As URI") { _, _ -> sendAsUri() }

        btnNavigateFromStart = view.findViewById(R.id.navigate_from_start)
        btnNavigateFromNearest = view.findViewById(R.id.navigate_from_nearest)
        btnSnapToRoad = view.findViewById(R.id.attach_to_roads)

        val activity = activity as MainActivity?
        activity!!.passWholeRoute = true
        activity.snapToRoad = false
        setChecked(btnNavigateFromStart, true)

        setupButton(btnNavigateFromStart, "Navigation from start") {
            setChecked(btnNavigateFromNearest, false)
            setChecked(btnNavigateFromStart, true)
            activity.passWholeRoute = true
        }

        setupButton(btnNavigateFromNearest, "Navigation from nearest point") {
            setChecked(btnNavigateFromNearest, true)
            setChecked(btnNavigateFromStart, false)
            activity.passWholeRoute = false
        }

        setupButton(btnSnapToRoad, "Attach to the roads before start navigation") {
            setChecked(btnSnapToRoad, !isChecked(btnSnapToRoad))
            activity.snapToRoad = isChecked(btnSnapToRoad)
        }

        return builder.create()
    }

    private fun setupButton(view: View, title: String, listener: OnClickListener) {
        view.findViewById<TextView>(R.id.title).setText(title)
        view.setOnClickListener(listener)
    }
    
    private fun setChecked(view: View, checked: Boolean) {
        view.findViewById<CompoundButton>(R.id.compound_button)?.isChecked = checked
    }

    private fun isChecked(view: View): Boolean {
        return view.findViewById<CompoundButton>(R.id.compound_button)?.isChecked ?: false
    }

    private fun sendAsRawData() {
        val sendAsRawDataRequestCode = sendAsRawDataRequestCode
        if (sendAsRawDataRequestCode != null) {
            requestChooseGpx(sendAsRawDataRequestCode)
        }
    }

    private fun sendAsUri() {
        val sendAsUriDataRequestCode = sendAsUriDataRequestCode
        if (sendAsUriDataRequestCode != null) {
            requestChooseGpx(sendAsUriDataRequestCode)
        }
    }

    private fun requestChooseGpx(requestCode: Int) {
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent = Intent.createChooser(intent, "Choose a file")

        val activity = activity as MainActivity?
        val osmandHelper = activity?.mOsmAndHelper
        if (osmandHelper != null && osmandHelper.isIntentSafe(intent)) {
            getActivity()?.startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(activity, "You need an app capable of selecting files like ES Explorer", Toast.LENGTH_LONG).show()
        }
    }
}