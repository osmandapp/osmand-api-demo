package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.Toast
import main.java.net.osmand.osmandapidemo.MainActivity

class OpenGpxDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "OpenGpxDialogFragment"
        const val SEND_AS_RAW_DATA_REQUEST_CODE_KEY = "send_as_raw_data_request_code_key"
        const val SEND_AS_URI_REQUEST_CODE_KEY = "send_as_uri_request_code_key"
    }

    private var sendAsRawDataRequestCode: Int? = null
    private var sendAsUriDataRequestCode: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sendAsRawDataRequestCode = arguments?.getInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY)
        sendAsUriDataRequestCode = arguments?.getInt(SEND_AS_URI_REQUEST_CODE_KEY)

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Send GPX to OsmAnd as raw data or as URI")
        builder.setNeutralButton("As raw data") { _, _ -> sendAsRawData() }
        builder.setPositiveButton("As URI") { _, _ -> sendAsUri() }
        return builder.create()
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
        var intent: Intent
        if (Build.VERSION.SDK_INT < 19) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
        } else {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
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