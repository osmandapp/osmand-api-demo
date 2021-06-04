package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import main.java.net.osmand.osmandapidemo.ApiActionType
import main.java.net.osmand.osmandapidemo.Location
import main.java.net.osmand.osmandapidemo.MainActivity
import net.osmand.osmandapidemo.R

class ChooseLocationDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "ChooseLocationDialogFragment"
        const val API_ACTION_CODE_KEY = "api_action_code_key"
        const val TITLE_KEY = "title_key"
        const val DELAYED_KEY = "delayed_key"
    }

    private var apiActionType: ApiActionType = ApiActionType.UNDEFINED
    private var title: String = ""
    private var delayed: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        if (arguments != null) {
            apiActionType = ApiActionType.valueOf(arguments.getString(API_ACTION_CODE_KEY, ApiActionType.UNDEFINED.name))
            title = arguments.getString(TITLE_KEY, "")
            delayed = arguments.getBoolean(DELAYED_KEY, true)
        }

        val context = requireActivity()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getTitle())
                .setAdapter(CitiesAdapter(context)) { _, i ->
                    locationSelectedCallback(MainActivity.CITIES[i])
                }
                .setNegativeButton("Cancel", null)
        return builder.create()
    }

    private fun locationSelectedCallback(location: Location) {
        val activity = activity as MainActivity?
        activity?.execApiAction(apiActionType, delayed, location)
    }

    private fun getTitle() = title
}

class CitiesAdapter(context: Context) : ArrayAdapter<Location>(context, R.layout.simple_list_layout, MainActivity.CITIES) {
    private val mInflater = LayoutInflater.from(context)
    private val icon: Drawable?

    init {
        val tempIcon = ContextCompat.getDrawable(context, R.drawable.ic_action_street_name)
        if (tempIcon != null) {
            icon = DrawableCompat.wrap(tempIcon)
            DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.iconColor))
        } else {
            icon = null
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView
                ?: mInflater?.inflate(R.layout.simple_list_layout, parent, false)) as TextView
        view.text = getItem(position)?.name
        view.compoundDrawablePadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8f, context.resources.displayMetrics).toInt()
        view.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
        return view
    }
}