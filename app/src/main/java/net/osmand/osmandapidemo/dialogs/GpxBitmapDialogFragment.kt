package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import main.java.net.osmand.osmandapidemo.MainActivity
import net.osmand.osmandapidemo.R

class GpxBitmapDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_gpx_bitmap_created, null)
        val activity = activity as MainActivity?
        val gpxBitmap = activity?.gpxBitmap

        view.findViewById<ImageView>(R.id.gpx_image).setImageBitmap(gpxBitmap)
        builder.setView(view)
        builder.setPositiveButton("OK", null)

        return builder.create()
    }
}