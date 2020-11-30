package main.java.net.osmand.osmandapidemo.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import main.java.net.osmand.osmandapidemo.MainActivity

class CloseAfterCommandDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "CloseAfterCommandDialogFragment"
        const val ACTION_CODE_KEY = "action_code_key"
    }

    private var actionType: ActionType = ActionType.UNDEFINED

    enum class ActionType {
        UNDEFINED,
        START_GPX_REC,
        STOP_GPX_REC,
        SAVE_GPX,
        CLEAR_GPX
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        if (arguments != null) {
            actionType = ActionType.valueOf(arguments.getString(ACTION_CODE_KEY, ActionType.UNDEFINED.name))
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Close OsmAnd immediately after execution of command?")
        builder.setNeutralButton("Close") { _, _ -> shouldClose(true) }
        builder.setPositiveButton("Don't close") { _, _ -> shouldClose(false) }
        return builder.create()
    }

    private fun shouldClose(close: Boolean) {
        val activity = activity as MainActivity?
        val osmandHelper = activity?.mOsmAndHelper
        if (osmandHelper != null) {
            when (actionType) {
                ActionType.UNDEFINED -> Unit
                ActionType.START_GPX_REC -> osmandHelper.startGpxRec(close)
                ActionType.STOP_GPX_REC -> osmandHelper.stopGpxRec(close)
                ActionType.SAVE_GPX -> osmandHelper.saveGpx(close)
                ActionType.CLEAR_GPX -> osmandHelper.clearGpx(close)
            }
        }
    }
}