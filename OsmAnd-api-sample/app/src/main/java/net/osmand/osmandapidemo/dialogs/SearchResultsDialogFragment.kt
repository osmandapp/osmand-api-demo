package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.title_desc_list_layout.view.*
import main.java.net.osmand.osmandapidemo.*
import net.osmand.aidlapi.search.SearchResult
import net.osmand.osmandapidemo.R

class SearchResultsDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "SearchResultsDialogFragment"
        const val RESULT_SET_KEY = "result_set_key"
        const val LATITUDE_KEY = "latitude_key"
        const val LONGITUDE_KEY = "longitude_key"
    }

    private var resultSet: List<SearchResult>? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        if (arguments != null) {
            resultSet = arguments.getParcelableArrayList(RESULT_SET_KEY)
            latitude = arguments.getDouble(LATITUDE_KEY)
            longitude = arguments.getDouble(LONGITUDE_KEY)
        }

        val context = requireActivity()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Search results - ${resultSet?.size}")
                .setNegativeButton("Cancel", null)

        if (resultSet != null) {
            builder.setAdapter(SearchResultsAdapter(context, resultSet!!, latitude, longitude)) { _, i ->
                val item = resultSet!![i]
                val activity = activity as MainActivity?
                activity?.execApiAction(ApiActionType.INTENT_SHOW_LOCATION, false,
                        Location(item.localName, item.latitude, item.longitude, item.latitude, item.longitude))
            }
        }
        return builder.create()
    }
}

class SearchResultsAdapter(context: Context, resultSet: List<SearchResult>, private var origLat: Double, private var origLon: Double) : ArrayAdapter<SearchResult>(context, R.layout.title_desc_list_layout, resultSet) {
    private val mInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = (convertView
                ?: mInflater?.inflate(R.layout.title_desc_list_layout, parent, false)) as LinearLayout

        val item = getItem(position)
        if (item != null) {
            val distance = Utils.getDistance(origLat, origLon, item.latitude, item.longitude)
            view.title.text = item.localName
            view.description.text = item.localTypeName
            view.info.text = Utils.getFormattedDistance(distance)
        }
        return view
    }
}