package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Html

class OsmAndInfoDialog : DialogFragment() {
    companion object {
        const val INFO_KEY = "info_key"
    }

    @Suppress("deprecation")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(INFO_KEY)
        val builder = AlertDialog.Builder(requireContext())
        if (message != null) {
            if (Build.VERSION.SDK_INT < 24) {
                builder.setMessage(Html.fromHtml(message))
            } else {
                builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
            }
        }
        builder.setTitle("OsmAnd info:")
        builder.setPositiveButton("OK", null)
        return builder.create()
    }
}