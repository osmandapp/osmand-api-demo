package main.java.net.osmand.osmandapidemo

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import main.java.net.osmand.osmandapidemo.OsmAndAidlHelper.VoiceRouterNotifyListener
import main.java.net.osmand.osmandapidemo.dialogs.*
import main.java.net.osmand.osmandapidemo.dialogs.CloseAfterCommandDialogFragment.ActionType
import main.java.net.osmand.osmandapidemo.dialogs.CloseAfterCommandDialogFragment.Companion.ACTION_CODE_KEY
import main.java.net.osmand.osmandapidemo.dialogs.OpenGpxDialogFragment.Companion.SEND_AS_RAW_DATA_REQUEST_CODE_KEY
import main.java.net.osmand.osmandapidemo.dialogs.OpenGpxDialogFragment.Companion.SEND_AS_URI_REQUEST_CODE_KEY
import net.osmand.aidlapi.OsmAndCustomizationConstants
import net.osmand.aidlapi.customization.OsmandSettingsParams
import net.osmand.aidlapi.customization.SetWidgetsParams
import net.osmand.aidlapi.map.ALatLon
import net.osmand.aidlapi.maplayer.point.AMapPoint
import net.osmand.aidlapi.navdrawer.NavDrawerFooterParams
import net.osmand.aidlapi.navdrawer.NavDrawerHeaderParams
import net.osmand.aidlapi.navdrawer.NavDrawerItem
import net.osmand.aidlapi.navdrawer.SetNavDrawerItemsParams
import net.osmand.aidlapi.navigation.ABlockedRoad
import net.osmand.aidlapi.navigation.ADirectionInfo
import net.osmand.aidlapi.plugins.PluginParams
import net.osmand.aidlapi.profile.AExportSettingsType
import net.osmand.aidlapi.search.SearchParams
import net.osmand.aidlapi.search.SearchResult
import net.osmand.osmandapidemo.R
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OsmAndHelper.OnOsmandMissingListener {

    companion object {
        private const val TAG = "MainActivity"
        const val REQUEST_OSMAND_API = 1001
        const val REQUEST_NAVIGATE_GPX_RAW_DATA = 1002
        const val REQUEST_SHOW_GPX_RAW_DATA = 1003
        const val REQUEST_NAVIGATE_GPX_URI = 1004
        const val REQUEST_SHOW_GPX_URI = 1005
        const val REQUEST_SHOW_GPX_RAW_DATA_AIDL = 1006
        const val REQUEST_SHOW_GPX_URI_AIDL = 1007
        const val REQUEST_NAVIGATE_GPX_RAW_DATA_AIDL = 1008
        const val REQUEST_NAVIGATE_GPX_URI_AIDL = 1009
        const val REQUEST_GET_GPX_BITMAP_URI_AIDL = 1010
        const val REQUEST_COPY_FILE = 1011
        const val REQUEST_IMPORT_FILE = 1012
        const val AUTHORITY = "net.osmand.osmandapidemo.fileprovider"
        const val GPX_FILE_NAME = "test.gpx"
        const val SQLDB_FILE_NAME = "test.sqlitedb"

        const val MAP_LAYER_ID = "layer_1"

        const val KEY_UPDATES_LISTENER = "subscribe_for_updates"
        const val KEY_OSMAND_INIT_LISTENER = "on_osmand_init"
        const val KEY_GPX_BITMAP_LISTENER = "on_bitmap_created"
        const val KEY_NAV_INFO_LISTENER = "on_nav_info_update"
        const val KEY_NAV_VOICE_INFO_LISTENER = "on_nav_voice_info_update"
        const val KEY_CONTEXT_BTN_LISTENER = "on_ctx_btn_click"

        const val DEMO_INTENT_URI = "osmand_api_demo://main_activity"

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
                Location("Panama", 8.9710438, -79.5340599, 8.992735, -79.5157),
                Location("Minsk", 53.9072394, 27.5863608, 53.9022545, 27.5619212),
                Location("Amsterdam", 52.3704312, 4.8904288, 52.3693012, 4.9013307)
        )

        val GPX_COLORS = arrayOf(
                "", "red", "orange", "lightblue", "blue", "purple",
                "translucent_red", "translucent_orange", "translucent_lightblue",
                "translucent_blue", "translucent_purple"
        )

        private const val APP_MODE_CAR = "car"
        private const val APP_MODE_PEDESTRIAN = "pedestrian"
        private const val APP_MODE_BICYCLE = "bicycle"
        private const val APP_MODE_BOAT = "boat"
        private const val APP_MODE_AIRCRAFT = "aircraft"
        private const val APP_MODE_BUS = "bus"
        private const val APP_MODE_TRAIN = "train"

        private const val SPEED_CONST_KILOMETERS_PER_HOUR = "KILOMETERS_PER_HOUR"
        private const val SPEED_CONST_MILES_PER_HOUR = "MILES_PER_HOUR"
        private const val SPEED_CONST_METERS_PER_SECOND = "METERS_PER_SECOND"
        private const val SPEED_CONST_MINUTES_PER_MILE = "MINUTES_PER_MILE"
        private const val SPEED_CONST_MINUTES_PER_KILOMETER = "MINUTES_PER_KILOMETER"
        private const val SPEED_CONST_NAUTICALMILES_PER_HOUR = "NAUTICALMILES_PER_HOUR"

        private const val METRIC_CONST_KILOMETERS_AND_METERS = "KILOMETERS_AND_METERS"
        private const val METRIC_CONST_MILES_AND_FEET = "MILES_AND_FEET"
        private const val METRIC_CONST_MILES_AND_METERS = "MILES_AND_METERS"
        private const val METRIC_CONST_MILES_AND_YARDS = "MILES_AND_YARDS"
        private const val METRIC_CONST_NAUTICAL_MILES = "NAUTICAL_MILES"

        private const val OSMAND_SHARED_PREFERENCES_NAME = "osmand-api-demo"

        private val appModesAll = null
        private val appModesNone = emptyList<String>()
        private val appModesPedestrian = listOf(APP_MODE_PEDESTRIAN)
        private val appModesPedestrianBicycle = listOf(APP_MODE_PEDESTRIAN, APP_MODE_BICYCLE)
        private val appModesExceptAirBoatDefault = listOf(APP_MODE_CAR, APP_MODE_BICYCLE, APP_MODE_PEDESTRIAN)
    }

    private var counter = 1
    private var delay: Long = 5000
    var mOsmAndHelper: OsmAndHelper? = null
    var gpxBitmap: Bitmap? = null
    private var mAidlHelper: OsmAndAidlHelper? = null

    private var progressDialog: ProgressDialog? = null
    private var lastLatitude: Double = 0.0
    private var lastLongitude: Double = 0.0

    private val callbackKeys = mutableMapOf<String, Long>()

    fun execApiAction(apiActionType: ApiActionType, delayed: Boolean = true, location: Location? = null) {
        if (location != null) {
            lastLatitude = location.lat
            lastLongitude = location.lon
        }
        if (delayed) {
            Handler().postDelayed({
                execApiActionImpl(apiActionType, location)
            }, delay)
        } else {
            execApiActionImpl(apiActionType, location)
        }
    }

    private fun execApiActionImpl(apiActionType: ApiActionType, location: Location? = null) {
        val aidlHelper = mAidlHelper
        val osmandHelper = mOsmAndHelper
        if (aidlHelper != null && osmandHelper != null) {
            when (apiActionType) {
                ApiActionType.AIDL_SET_NAV_DRAWER_ITEMS -> {
                    aidlHelper.setNavDrawerItems(packageName, listOf(getString(R.string.app_name)), listOf(DEMO_INTENT_URI), listOf("ic_action_travel"), listOf(-1))
                }
                ApiActionType.AIDL_REFRESH_MAP -> {
                    aidlHelper.refreshMap()
                }
                ApiActionType.AIDL_ADD_FAVORITE_GROUP -> {
                    aidlHelper.addFavoriteGroup("New group", "purple", false)
                }
                ApiActionType.AIDL_UPDATE_FAVORITE_GROUP -> {
                    aidlHelper.updateFavoriteGroup("New group", "purple", false, "New group 1", "red", true)
                }
                ApiActionType.AIDL_REMOVE_FAVORITE_GROUP -> {
                    aidlHelper.removeFavoriteGroup("New group")
                }
                ApiActionType.AIDL_REGISTER_FOR_UPDATES -> {
                    aidlHelper.setUpdateListener(object : OsmAndAidlHelper.UpdateListener {
                        override fun onUpdatePing() {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Ping from OsmAnd every 7 sec", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    callbackKeys[KEY_UPDATES_LISTENER] = aidlHelper.registerForUpdates(7000)
                }
                ApiActionType.AIDL_UNREGISTER_FORM_UPDATES -> {
                    if (callbackKeys.containsKey(KEY_UPDATES_LISTENER)) {
                        aidlHelper.unregisterFromUpdates(callbackKeys[KEY_UPDATES_LISTENER]!!)
                        callbackKeys.remove(KEY_UPDATES_LISTENER)
                        Toast.makeText(this@MainActivity, "Unsubscribed from OsmAnd pings", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "You need first to subscribe for updates from OsmAnd", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.AIDL_ADD_MAP_LAYER -> {
                    aidlHelper.addMapLayer(MAP_LAYER_ID, "OSMO Layer", 5.5f, null, true)
                }
                ApiActionType.AIDL_REMOVE_MAP_LAYER -> {
                    aidlHelper.removeMapLayer(MAP_LAYER_ID)
                }
                ApiActionType.AIDL_UPDATE_MAP_LAYER -> {
                    aidlHelper.updateMapLayer(MAP_LAYER_ID, "OSMO Layer Updated", 6.5f, null, true)
                }
                ApiActionType.AIDL_IMPORT_GPX -> {
                    val args = Bundle()
                    args.putInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY, REQUEST_SHOW_GPX_RAW_DATA_AIDL)
                    args.putInt(SEND_AS_URI_REQUEST_CODE_KEY, REQUEST_SHOW_GPX_URI_AIDL)
                    val openGpxDialogFragment = OpenGpxDialogFragment()
                    openGpxDialogFragment.arguments = args
                    openGpxDialogFragment.show(supportFragmentManager, OpenGpxDialogFragment.TAG)
                }
                ApiActionType.AIDL_SHOW_GPX -> {
                    aidlHelper.showGpx(GPX_FILE_NAME)
                }
                ApiActionType.AIDL_HIDE_GPX -> {
                    aidlHelper.hideGpx(GPX_FILE_NAME)
                }
                ApiActionType.AIDL_GET_ACTIVE_GPX_FILES -> {
                    val activeGpxFiles = aidlHelper.activeGpxFiles
                    val sb = StringBuilder()
                    if (activeGpxFiles != null) {
                        for (gpxFile in activeGpxFiles) {
                            if (sb.isNotEmpty()) {
                                sb.append("<br>")
                            }
                            sb.append(gpxFile.fileName)
                        }
                    }
                    if (sb.isEmpty()) {
                        sb.append("No active files found")
                    }
                    showOsmandInfoDialog(sb.toString())
                }
                ApiActionType.AIDL_START_GPX_REC -> {
                    aidlHelper.startGpxRecording()
                }
                ApiActionType.AIDL_STOP_GPX_REC -> {
                    aidlHelper.stopGpxRecording()
                }
                ApiActionType.AIDL_STOP_REC -> {
                    aidlHelper.stopRecording()
                }
                ApiActionType.AIDL_NAVIGATE_GPX -> {
                    val args = Bundle()
                    args.putInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY, REQUEST_NAVIGATE_GPX_RAW_DATA_AIDL)
                    args.putInt(SEND_AS_URI_REQUEST_CODE_KEY, REQUEST_NAVIGATE_GPX_URI_AIDL)
                    val openGpxDialogFragment = OpenGpxDialogFragment()
                    openGpxDialogFragment.arguments = args
                    openGpxDialogFragment.show(supportFragmentManager, OpenGpxDialogFragment.TAG)
                }
                ApiActionType.AIDL_REMOVE_GPX -> {
                    aidlHelper.removeGpx(GPX_FILE_NAME)
                }
                ApiActionType.AIDL_HIDE_DRAWER_PROFILE -> {
                    aidlHelper.setDisabledPatterns(listOf(OsmAndCustomizationConstants.DRAWER_SWITCH_PROFILE_ID,
                            OsmAndCustomizationConstants.DRAWER_CONFIGURE_PROFILE_ID))
                }
                ApiActionType.AIDL_SET_ENABLED_UI_IDS -> {
                    val enabledIds = getFeaturesEnabledIds()
                    aidlHelper.setEnabledIds(enabledIds)
                }
                ApiActionType.AIDL_SET_DISABLED_UI_IDS -> {
                    val disabledIds = getFeaturesDisabledIds()
                    aidlHelper.setDisabledIds(disabledIds)
                }
                ApiActionType.AIDL_SET_ENABLED_MENU_PATTERNS -> {
                    val enabledPatterns = getFeaturesEnabledPatterns()
                    aidlHelper.setEnabledPatterns(enabledPatterns)
                }
                ApiActionType.AIDL_SET_DISABLED_MENU_PATTERNS -> {
                    val disabledPatterns = getFeaturesDisabledPatterns()
                    aidlHelper.setDisabledPatterns(disabledPatterns)
                }
                ApiActionType.AIDL_REG_WIDGET_VISIBILITY -> {
                    aidlHelper.regWidgetVisibility("ruler", null)
                }
                ApiActionType.AIDL_REG_WIDGET_AVAILABILITY -> {
                    aidlHelper.regWidgetAvailability("bearing", listOf(APP_MODE_BOAT))
                }
                ApiActionType.AIDL_CUSTOMIZE_OSMAND_SETTINGS -> {
                    val bundle = Bundle().apply {
                        putString("application_mode", APP_MODE_CAR)
                        putBoolean("driving_region_automatic", false)
                        putBoolean("show_coordinates_widget", true)
                        putBoolean("show_compass_ruler", true)
                        putString("map_info_controls", "ruler;")
                        putString("default_metric_system", METRIC_CONST_NAUTICAL_MILES)
                        putString("default_speed_system", SPEED_CONST_NAUTICALMILES_PER_HOUR)
                    }
                    aidlHelper.customizeOsmandSettings(OSMAND_SHARED_PREFERENCES_NAME, bundle)
                }
                ApiActionType.AIDL_GET_IMPORTED_GPX_FILES -> {
                    val importedGpxFiles = aidlHelper.importedGpx
                    val sb = StringBuilder()
                    if (importedGpxFiles != null) {
                        for (gpxFile in importedGpxFiles) {
                            if (sb.isNotEmpty()) {
                                sb.append("<br>")
                            }
                            sb.append(gpxFile.fileName)
                        }
                    }
                    if (sb.isEmpty()) {
                        sb.append("No imported Gpx files found")
                    }
                    showOsmandInfoDialog(sb.toString())
                }
                ApiActionType.AIDL_GET_SQLITEDB_FILES -> {
                    val sqliteDbFiles = aidlHelper.sqliteDbFiles
                    val sb = StringBuilder()
                    if (sqliteDbFiles != null) {
                        for (gpxFile in sqliteDbFiles) {
                            if (sb.isNotEmpty()) {
                                sb.append("<br>")
                            }
                            sb.append(gpxFile.fileName)
                        }
                    }
                    if (sb.isEmpty()) {
                        sb.append("No SqliteDb files found")
                    }
                    showOsmandInfoDialog(sb.toString())
                }
                ApiActionType.AIDL_GET_ACTIVE_SQLITEDB_FILES -> {
                    val activeSqliteDbFiles = aidlHelper.activeSqliteDbFiles
                    val sb = StringBuilder()
                    if (activeSqliteDbFiles != null) {
                        for (gpxFile in activeSqliteDbFiles) {
                            if (sb.isNotEmpty()) {
                                sb.append("<br>")
                            }
                            sb.append(gpxFile.fileName)
                        }
                    }
                    if (sb.isEmpty()) {
                        sb.append("No active SqliteDb files files found")
                    }
                    showOsmandInfoDialog(sb.toString())
                }
                ApiActionType.AIDL_SHOW_SQLITEDB_FILE -> {
                    aidlHelper.showSqliteDbFile(SQLDB_FILE_NAME)
                }
                ApiActionType.AIDL_HIDE_SQLITEDB_FILE -> {
                    aidlHelper.hideSqliteDbFile(SQLDB_FILE_NAME)
                }
                ApiActionType.AIDL_SET_NAV_DRAWER_LOGO -> {
                    val resId = R.drawable.ic_osmand_logo
                    val pack = resources.getResourcePackageName(resId)
                    val type = resources.getResourceTypeName(resId)
                    val entry = resources.getResourceEntryName(resId)
                    val logoUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://$pack/$type/$entry")

                    aidlHelper.setNavDrawerLogoWithParams(logoUri.toString(), packageName, DEMO_INTENT_URI)
                }
                ApiActionType.AIDL_SET_NAV_DRAWER_FOOTER -> {
                    aidlHelper.setNavDrawerFooterWithParams(packageName, DEMO_INTENT_URI, getString(R.string.app_name))
                }
                ApiActionType.AIDL_RESTORE_OSMAND -> {
                    aidlHelper.restoreOsmand()
                }
                ApiActionType.AIDL_CHANGE_PLUGIN_STATE -> {
                    aidlHelper.changePluginState(OsmAndCustomizationConstants.PLUGIN_RASTER_MAPS, 1)
                }
                ApiActionType.AIDL_REGISTER_FOR_OSMAND_INITIALIZATION -> {
                    aidlHelper.setOsmandInitializedListener(object : OsmAndAidlHelper.OsmandInitializedListener {
                        override fun onOsmandInitilized() {
                            runOnUiThread {
                                Toast.makeText(this@MainActivity, "Osmand Initilized", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    aidlHelper.registerForOsmandInitListener()
                }
                ApiActionType.AIDL_GET_BITMAP_FOR_GPX -> {
                    aidlHelper.setGpxBitmapCreatedListener(object : OsmAndAidlHelper.GpxBitmapCreatedListener {
                        override fun onGpxBitmapCreated(bitmap: Bitmap?) {
                            gpxBitmap = bitmap
                            val gpxDialogFragment = GpxBitmapDialogFragment()
                            supportFragmentManager.beginTransaction()
                                    .add(gpxDialogFragment, null)
                                    .commitAllowingStateLoss()
                        }
                    })
                    requestChooseFile(REQUEST_GET_GPX_BITMAP_URI_AIDL)
                }
                ApiActionType.AIDL_COPY_FILE_TO_OSMAND -> {
                    requestChooseFile(REQUEST_COPY_FILE)
                }
                ApiActionType.AIDL_REGISTER_FOR_NAV_UPDATES -> {
                    aidlHelper.setNavigationInfoUpdateListener(object : OsmAndAidlHelper.NavigationInfoUpdateListener {
                        override fun onNavigationInfoUpdate(directionInfo: ADirectionInfo?) {
                            runOnUiThread {
                                val text = "NavigationInfoUpdate"
                                Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    callbackKeys[KEY_NAV_INFO_LISTENER] = aidlHelper.registerForNavigationUpdates(true, 0)
                }
                ApiActionType.AIDL_UNREGISTER_FOR_NAV_UPDATES -> {
                    if (callbackKeys.containsKey(KEY_NAV_INFO_LISTENER)) {
                        aidlHelper.registerForNavigationUpdates(false, callbackKeys[KEY_NAV_INFO_LISTENER]!!)
                        callbackKeys.remove(KEY_NAV_INFO_LISTENER)
                        Toast.makeText(this@MainActivity, "Unsubscribed from OsmAnd pings", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "You need first to subscribe for updates from OsmAnd", Toast.LENGTH_SHORT).show()
                    }
                }
               ApiActionType.AIDL_GET_AVOID_ROADS -> {
                    val list = arrayListOf<ABlockedRoad>()
                    aidlHelper.getBlockedRoads(list)
                    val text = SpannableStringBuilder("Avoid roads size: ${list.size} \n")
                    for (i in list) {
                        text.append(i.name).append("\n")
                    }
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                }
               ApiActionType.AIDL_ADD_AVOID_ROAD -> {
                    aidlHelper.addRoadBlock(ABlockedRoad(0,52.37391, 4.90193, 0.0, "Api road block", "car"  ))
                }
               ApiActionType.AIDL_REMOVE_AVOID_ROAD -> {
                    aidlHelper.removeRoadBlock(ABlockedRoad(0,52.37391, 4.90193, 0.0, "Api road block", "car"  ))
                }
                ApiActionType.AIDL_ADD_CONTEXT_MENU_BUTTONS -> {
                    aidlHelper.setContextButtonClickListener(OsmAndAidlHelper.ContextButtonClickListener { buttonId, pointId, layerId ->
                        runOnUiThread {
                            val text = "Context menu button clicked! buttonId $buttonId pointId $pointId layerId $layerId"
                            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                        }
                    })
                    aidlHelper.addContextMenuButtons(
                            1,
                            "LeftText",
                            "RightText",
                            "ic_action_start_navigation",
                            "ic_action_start_navigation",
                            true,
                            true,
                            2,
                            "LeftText4",
                            "RightText4",
                            "ic_action_start_navigation",
                            "ic_action_start_navigation",
                            true,
                            true,
                            "Buttons",
                            packageName,
                            MAP_LAYER_ID,
                            18,
                            emptyList()
                    )
                }
                ApiActionType.AIDL_REMOVE_CONTEXT_MENU_BUTTONS -> {
                    aidlHelper.removeContextMenuButtons("Buttons", 18)
                }
                ApiActionType.AIDL_UPDATE_CONTEXT_MENU_BUTTONS -> {
                    aidlHelper.updateContextMenuButtons(
                            1,
                            "LeftText2",
                            "RightText2",
                            "ic_action_start_navigation",
                            "ic_action_start_navigation",
                            true,
                            true,
                            2,
                            "LeftText3",
                            "RightText3",
                            "ic_action_start_navigation",
                            "ic_action_start_navigation",
                            true,
                            true,
                            "Buttons",
                            packageName,
                            MAP_LAYER_ID,
                            18,
                            emptyList())
                }
                ApiActionType.AIDL_ARE_OSMAND_SETTINGS_CUSTOMIZED -> {
                    val settingsCustomized = aidlHelper.areOsmandSettingsCustomized(OSMAND_SHARED_PREFERENCES_NAME)
                    val text = if (settingsCustomized) {
                        "OsmAnd settings were customized"
                    } else {
                        "OsmAnd settings were not customized"
                    }
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
                }
                ApiActionType.AIDL_SET_CUSTOMIZATION -> {

                    val resId = R.drawable.ic_osmand_logo
                    val pack = resources.getResourcePackageName(resId)
                    val type = resources.getResourceTypeName(resId)
                    val entry = resources.getResourceEntryName(resId)
                    val logoUri = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://$pack/$type/$entry")

                    val navDrawerHeaderParams = NavDrawerHeaderParams(logoUri.toString(), packageName, DEMO_INTENT_URI)
                    val navDrawerFooterParams = NavDrawerFooterParams(packageName, DEMO_INTENT_URI, resources.getString(R.string.app_name))
                    val navDrawerItemsParams = SetNavDrawerItemsParams(packageName, listOf(NavDrawerItem(getString(R.string.set_customization), DEMO_INTENT_URI, "ic_action_settings", -1)))

                    val featuresEnabledIds = getFeaturesEnabledIds()
                    val featuresDisabledIds = getFeaturesDisabledIds()
                    val featuresEnabledPatterns = getFeaturesEnabledPatterns()
                    val featuresDisabledPatterns = getFeaturesDisabledPatterns()
                    val visibilityWidgetsParams = getVisibilityWidgetsParams()
                    val availabilityWidgetsParams = getAvailabilityWidgetsParams()
                    val settingsParams = getCustomOsmandSettingsParams()
                    val pluginParams = arrayListOf(PluginParams(OsmAndCustomizationConstants.PLUGIN_RASTER_MAPS, 1))

                    aidlHelper.setCustomization(settingsParams, navDrawerHeaderParams, navDrawerFooterParams,
                            navDrawerItemsParams, visibilityWidgetsParams, availabilityWidgetsParams, pluginParams,
                            featuresEnabledIds, featuresDisabledIds, featuresEnabledPatterns, featuresDisabledPatterns
                    )
                }
                ApiActionType.AIDL_SET_UI_MARGINS -> {
                    val profileKeys = listOf(APP_MODE_CAR, APP_MODE_BOAT)
                    val success = aidlHelper.setMapMargins(10, 20, 60, 20,
                            profileKeys)
                    if (success) {
                        Toast.makeText(this@MainActivity, "UI margins set for $profileKeys", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Failed to set UI margins", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.AIDL_REGISTER_FOR_VOICE_ROUTE_MESSAGES -> {
                    aidlHelper.setVoiceRouterNotifyListener(VoiceRouterNotifyListener { params ->
                        runOnUiThread {
                            if (params != null) {
                                Toast.makeText(this@MainActivity, "onVoiceRouterNotify " +
                                        "\ncmds: ${params.commands}" +
                                        "\nplayed: ${params.played}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    callbackKeys[KEY_NAV_VOICE_INFO_LISTENER] = aidlHelper.registerForVoiceRouterMessages(true, 0)
                }
                ApiActionType.AIDL_UNREGISTER_FROM_VOICE_ROUTE_MESSAGES -> {
                    if (callbackKeys.containsKey(KEY_NAV_VOICE_INFO_LISTENER)) {
                        aidlHelper.registerForVoiceRouterMessages(false, callbackKeys[KEY_NAV_VOICE_INFO_LISTENER]!!)
                        callbackKeys.remove(KEY_NAV_VOICE_INFO_LISTENER)
                        Toast.makeText(this@MainActivity, "Unsubscribed from voice updates", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "You need first to subscribe for voice updates from OsmAnd", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.AIDL_REMOVE_ALL_ACTIVE_MAP_MARKERS -> {
                    aidlHelper.removeAllActiveMapMarkers()
                }
                ApiActionType.AIDL_ADD_FIRST_MAP_WIDGET -> {
                    aidlHelper.addMapWidget("111", "ic_action_speed", "AIDL Speed", "widget_speed_day", "widget_speed_night", "10", "km/h", 50, getDemoIntent())
                }
                ApiActionType.AIDL_ADD_SECOND_MAP_WIDGET -> {
                    aidlHelper.addMapWidget("222", "ic_action_time", "AIDL Time", "widget_time_day", "widget_time_night", getTimeStr(), "", 51, getDemoIntent())
                }
                ApiActionType.AIDL_REMOVE_FIRST_MAP_WIDGET -> {
                    aidlHelper.removeMapWidget("111")
                }
                ApiActionType.AIDL_REMOVE_SECOND_MAP_WIDGET -> {
                    aidlHelper.removeMapWidget("222")
                }
                ApiActionType.AIDL_UPDATE_FIRST_MAP_WIDGET -> {
                    aidlHelper.updateMapWidget("111", "ic_action_speed", "AIDL Speed", "widget_speed_day", "widget_speed_night", "1" + counter++, "km/h", 50, getDemoIntent())
                }
                ApiActionType.AIDL_UPDATE_SECOND_MAP_WIDGET -> {
                    aidlHelper.updateMapWidget("222", "ic_action_time", "AIDL Time", "widget_time_day", "widget_time_night", getTimeStr(), "", 51, getDemoIntent())
                }
                ApiActionType.AIDL_PAUSE_NAVIGATION -> {
                    aidlHelper.pauseNavigation()
                }
                ApiActionType.AIDL_RESUME_NAVIGATION -> {
                    aidlHelper.resumeNavigation()
                }
                ApiActionType.AIDL_STOP_NAVIGATION -> {
                    aidlHelper.stopNavigation()
                }
                ApiActionType.AIDL_MUTE_NAVIGATION -> {
                    aidlHelper.muteNavigation()
                }
                ApiActionType.AIDL_UNMUTE_NAVIGATION -> {
                    aidlHelper.unmuteNavigation()
                }
                ApiActionType.AIDL_IMPORT_PROFILE -> {
                    val fileName = "Driving_test.osf"
                    val sharedDir = File(cacheDir, "share")
                    if (!sharedDir.exists()) {
                        sharedDir.mkdir()
                    }
                    val file = File(sharedDir, fileName)
                    val am: AssetManager = application.getAssets()
                    val inStream: InputStream = am.open(fileName)
                    val outStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var length = inStream.read(buffer)
                    while (length > 0) {
                        outStream.write(buffer, 0, length)
                        length = inStream.read(buffer)
                    }
                    inStream.close()
                    outStream.close()
                    val fileUri = FileProvider.getUriForFile(this, AUTHORITY, file)
                    val settingsTypeList = arrayListOf(AExportSettingsType.PROFILE)
                    val replace = true
                    val silent = true
                    aidlHelper.importProfile(fileUri, settingsTypeList, replace, silent)
                }
                ApiActionType.AIDL_EXPORT_PROFILE -> {
                    val profileKey = "car"
                    val settingsTypeList = arrayListOf(AExportSettingsType.QUICK_ACTIONS, AExportSettingsType.MAP_SOURCES)
                    val success = aidlHelper.exportProfile(profileKey, settingsTypeList)
                    if (success) {
                        Toast.makeText(this@MainActivity, "Profile $profileKey is exported", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Failed export profile $profileKey", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.AIDL_IS_FRAGMENT_OPEN -> {
                    val open = aidlHelper.isFragmentOpen
                    if (open) {
                        Toast.makeText(this@MainActivity, "Fragment is open", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Fragment is closed", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.AIDL_IS_MENU_OPEN -> {
                    val open = aidlHelper.isMenuOpen
                    if (open) {
                        Toast.makeText(this@MainActivity, "Menu is open", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Menu is closed", Toast.LENGTH_SHORT).show()
                    }
                }
                ApiActionType.INTENT_PAUSE_NAVIGATION -> {
                    osmandHelper.pauseNavigation()
                }
                ApiActionType.INTENT_RESUME_NAVIGATION -> {
                    osmandHelper.resumeNavigation()
                }
                ApiActionType.INTENT_STOP_NAVIGATION -> {
                    osmandHelper.stopNavigation()
                }
                ApiActionType.INTENT_MUTE_NAVIGATION -> {
                    osmandHelper.muteNavigation()
                }
                ApiActionType.INTENT_UNMUTE_NAVIGATION -> {
                    osmandHelper.umuteNavigation()
                }
                ApiActionType.AIDL_EXIT_APP -> {
                    aidlHelper.exitApp(false)
                }
                ApiActionType.AIDL_GET_TEXT -> {
                    val text =  aidlHelper.getText("daynight_mode_auto", Locale.GERMAN)
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
            // location depended types
            if (location != null) {
                when (apiActionType) {
                    ApiActionType.AIDL_ADD_FAVORITE -> {
                        aidlHelper.addFavorite(location.lat, location.lon, location.name,
                                location.name + " city", "Cities", "red", "", true)
                    }
                    ApiActionType.AIDL_UPDATE_FAVORITE -> {
                        aidlHelper.updateFavorite(location.lat, location.lon, location.name, "Cities",
                                location.lat, location.lon, location.name, location.name + " city", "Cities", "yellow", true)
                    }
                    ApiActionType.AIDL_REMOVE_FAVORITE -> {
                        aidlHelper.removeFavorite(location.lat, location.lon, location.name, "Cities")
                    }
                    ApiActionType.AIDL_ADD_MAP_MARKER -> {
                        aidlHelper.addMapMarker(location.lat, location.lon, location.name)
                    }
                    ApiActionType.AIDL_UPDATE_MAP_MARKER -> {
                        aidlHelper.updateMapMarker(location.lat, location.lon, location.name,
                                location.lat, location.lon, location.name + " " + counter++)
                    }
                    ApiActionType.AIDL_REMOVE_MAP_MARKER -> {
                        aidlHelper.removeMapMarker(location.lat, location.lon, location.name)
                    }
                    ApiActionType.AIDL_SHOW_MAP_POINT -> {
                        aidlHelper.showMapPoint(
                                MAP_LAYER_ID,
                                "id_" + location.name,
                                location.name.substring(0, 1),
                                location.name,
                                "City",
                                Color.GREEN,
                                ALatLon(location.lat, location.lon),
                                listOf("Big city", "Population: ..."),
                                mapOf(AMapPoint.POINT_SPEED_PARAM to "4.0", AMapPoint.POINT_TYPE_ICON_NAME_PARAM to "ic_type_address")
                        )
                    }
                    ApiActionType.AIDL_ADD_MAP_POINT -> {
                        aidlHelper.addMapPoint(
                                MAP_LAYER_ID,
                                "id_" + location.name,
                                location.name.substring(0, 1),
                                location.name,
                                "City",
                                Color.GREEN,
                                ALatLon(location.lat, location.lon),
                                listOf("Big city", "Population: ..."),
                                mapOf(AMapPoint.POINT_SPEED_PARAM to "4.0", AMapPoint.POINT_TYPE_ICON_NAME_PARAM to "ic_type_address")
                        )
                    }
                    ApiActionType.AIDL_UPDATE_MAP_POINT -> {
                        aidlHelper.updateMapPoint(
                                MAP_LAYER_ID,
                                "id_" + location.name,
                                location.name.substring(1, 2),
                                location.name,
                                "City",
                                Color.RED,
                                ALatLon(location.lat, location.lon),
                                listOf("Big city", "Population: unknown"),
                                mapOf(AMapPoint.POINT_SPEED_PARAM to "4.0", AMapPoint.POINT_TYPE_ICON_NAME_PARAM to "ic_type_address")
                        )
                    }
                    ApiActionType.AIDL_REMOVE_MAP_POINT -> {
                        aidlHelper.removeMapPoint(MAP_LAYER_ID, "id_" + location.name)
                    }
                    ApiActionType.AIDL_TAKE_PHOTO -> {
                        aidlHelper.takePhotoNote(location.lat, location.lon)
                    }
                    ApiActionType.AIDL_START_VIDEO_REC -> {
                        aidlHelper.startVideoRecording(location.lat, location.lon)
                    }
                    ApiActionType.AIDL_START_AUDIO_REC -> {
                        aidlHelper.startAudioRecording(location.lat, location.lon)
                    }
                    ApiActionType.AIDL_SET_MAP_LOCATION -> {
                        aidlHelper.setMapLocation(location.lat, location.lon, 16, 0f, true)
                    }
                    ApiActionType.AIDL_NAVIGATE -> {
                        aidlHelper.navigate(location.name + " start",
                                location.latStart, location.lonStart,
                                location.name + " finish", location.lat, location.lon,
                                "bicycle", true, true)
                    }
                    ApiActionType.AIDL_NAVIGATE_SEARCH -> {
                        val alert = AlertDialog.Builder(this)
                        val editText = EditText(this)
                        alert.setTitle("Enter Search Query")
                        alert.setView(editText)
                        alert.setPositiveButton("Navigate") { _, _ ->
                            val text = editText.text.toString()
                            Handler().postDelayed({
                                aidlHelper.navigateSearch(location.name + " start",
                                        location.latStart, location.lonStart,
                                        text, location.latStart, location.lonStart,
                                        "car", true, true)
                            }, delay)
                        }
                        alert.setNegativeButton("Cancel", null)
                        alert.show()
                    }
                    ApiActionType.AIDL_SEARCH -> {
                        val alert = AlertDialog.Builder(this)
                        val editText = EditText(this)
                        alert.setTitle("Enter Search Query")
                        alert.setView(editText)
                        alert.setPositiveButton("Search") { _, _ ->
                            progressDialog?.setTitle("Searching...")
                            progressDialog?.show()
                            val text = editText.text.toString()
                            aidlHelper.search(text, SearchParams.SEARCH_TYPE_ALL, location.latStart, location.lonStart, 1, 50)
                        }
                        alert.setNegativeButton("Cancel", null)
                        alert.show()
                    }
                    ApiActionType.INTENT_ADD_FAVORITE -> {
                        osmandHelper.addFavorite(location.lat, location.lon, location.name,
                                location.name + " city", "Cities", "red", true)
                    }
                    ApiActionType.INTENT_ADD_MAP_MARKER -> {
                        osmandHelper.addMapMarker(location.lat, location.lon, location.name)
                    }
                    ApiActionType.INTENT_SHOW_LOCATION -> {
                        osmandHelper.showLocation(location.lat, location.lon)
                    }
                    ApiActionType.INTENT_TAKE_PHOTO -> {
                        osmandHelper.takePhoto(location.lat, location.lon)
                    }
                    ApiActionType.INTENT_START_VIDEO_REC -> {
                        osmandHelper.recordVideo(location.lat, location.lon)
                    }
                    ApiActionType.INTENT_START_AUDIO_REC -> {
                        osmandHelper.recordAudio(location.lat, location.lon)
                    }
                    ApiActionType.INTENT_NAVIGATE -> {
                        osmandHelper.navigate(location.name + " start",
                                location.latStart, location.lonStart,
                                location.name + " finish", location.lat, location.lon,
                                "bicycle", true, true)
                    }
                    ApiActionType.INTENT_NAVIGATE_SEARCH -> {
                        val alert = AlertDialog.Builder(this)
                        val editText = EditText(this)
                        alert.setTitle("Enter Search Query")
                        alert.setView(editText)
                        alert.setPositiveButton("Navigate") { _, _ ->
                            val text = editText.text.toString()
                            osmandHelper.navigateSearch(location.name + " start",
                                    location.latStart, location.lonStart,
                                    text, location.latStart, location.lonStart,
                                    "car", true, true)
                        }
                        alert.setNegativeButton("Cancel", null)
                        alert.show()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mOsmAndHelper = OsmAndHelper(this, REQUEST_OSMAND_API, this)
        mAidlHelper = OsmAndAidlHelper(this.application, this)

        mAidlHelper!!.setSearchCompleteListener {
            runOnUiThread {
                progressDialog?.hide()
                showSearchResultsDialogFragment(it, lastLatitude, lastLongitude)
            }
        }

        progressDialog = ProgressDialog(this)

        setContentView(R.layout.activity_main)

        setupButtonsIcons()

        // Intents

        addFavoriteButton.setOnClickListener {
            showChooseLocationDialogFragment("Add favourite", ApiActionType.INTENT_ADD_FAVORITE, false)
        }
        addMapMarkerButton.setOnClickListener {
            showChooseLocationDialogFragment("Add map marker", ApiActionType.INTENT_ADD_MAP_MARKER, false)
        }

        showLocationButton.setOnClickListener {
            showChooseLocationDialogFragment("Show location", ApiActionType.INTENT_SHOW_LOCATION, false)
        }

        startAudioRecButton.setOnClickListener {
            showChooseLocationDialogFragment("Start audio recording", ApiActionType.INTENT_START_AUDIO_REC, false)
        }
        startVideoRecButton.setOnClickListener {
            showChooseLocationDialogFragment("Start video recording", ApiActionType.INTENT_START_VIDEO_REC, false)
        }
        takePhotoButton.setOnClickListener {
            showChooseLocationDialogFragment("Take photo", ApiActionType.INTENT_TAKE_PHOTO, false)
        }
        stopRecButton.setOnClickListener { mOsmAndHelper!!.stopAvRec() }
        startGpxRecButton.setOnClickListener {
            val args = Bundle()
            args.putString(ACTION_CODE_KEY, ActionType.START_GPX_REC.name)
            val closeAfterCommandDialogFragment = CloseAfterCommandDialogFragment()
            closeAfterCommandDialogFragment.arguments = args
            closeAfterCommandDialogFragment.show(supportFragmentManager, CloseAfterCommandDialogFragment.TAG)
        }
        stopGpxRecButton.setOnClickListener {
            val args = Bundle()
            args.putString(ACTION_CODE_KEY, ActionType.STOP_GPX_REC.name)
            val closeAfterCommandDialogFragment = CloseAfterCommandDialogFragment()
            closeAfterCommandDialogFragment.arguments = args
            closeAfterCommandDialogFragment.show(supportFragmentManager, CloseAfterCommandDialogFragment.TAG)
        }
        saveGpxButton.setOnClickListener {
            val args = Bundle()
            args.putString(ACTION_CODE_KEY, ActionType.SAVE_GPX.name)
            val closeAfterCommandDialogFragment = CloseAfterCommandDialogFragment()
            closeAfterCommandDialogFragment.arguments = args
            closeAfterCommandDialogFragment.show(supportFragmentManager, CloseAfterCommandDialogFragment.TAG)
        }
        clearGpxButton.setOnClickListener {
            val args = Bundle()
            args.putString(ACTION_CODE_KEY, ActionType.CLEAR_GPX.name)
            val closeAfterCommandDialogFragment = CloseAfterCommandDialogFragment()
            closeAfterCommandDialogFragment.arguments = args
            closeAfterCommandDialogFragment.show(supportFragmentManager, CloseAfterCommandDialogFragment.TAG)
        }
        showGpxButton.setOnClickListener {
            val args = Bundle()
            args.putInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY, REQUEST_SHOW_GPX_RAW_DATA)
            args.putInt(SEND_AS_URI_REQUEST_CODE_KEY, REQUEST_SHOW_GPX_URI)
            val openGpxDialogFragment = OpenGpxDialogFragment()
            openGpxDialogFragment.arguments = args
            openGpxDialogFragment.show(supportFragmentManager, OpenGpxDialogFragment.TAG)
        }
        navigateGpxButton.setOnClickListener {
            val args = Bundle()
            args.putInt(SEND_AS_RAW_DATA_REQUEST_CODE_KEY, REQUEST_NAVIGATE_GPX_RAW_DATA)
            args.putInt(SEND_AS_URI_REQUEST_CODE_KEY, REQUEST_NAVIGATE_GPX_URI)
            val openGpxDialogFragment = OpenGpxDialogFragment()
            openGpxDialogFragment.arguments = args
            openGpxDialogFragment.show(supportFragmentManager, OpenGpxDialogFragment.TAG)
        }
        navigateButton.setOnClickListener {
            showChooseLocationDialogFragment("Navigate to", ApiActionType.INTENT_NAVIGATE, false)
        }
        navigateSearchButton.setOnClickListener {
            showChooseLocationDialogFragment("Search and Navigate", ApiActionType.INTENT_NAVIGATE_SEARCH, false)
        }
        pauseNavigationButton.setOnClickListener {
            mOsmAndHelper!!.pauseNavigation()
        }

        resumeNavigationButton.setOnClickListener {
            mOsmAndHelper!!.resumeNavigation()
        }
        stopNavigationButton.setOnClickListener { mOsmAndHelper!!.stopNavigation() }
        muteNavigationButton.setOnClickListener { mOsmAndHelper!!.muteNavigation() }
        unmuteNavigationButton.setOnClickListener { mOsmAndHelper!!.umuteNavigation() }
        getInfoButton.setOnClickListener { mOsmAndHelper!!.getInfo() }
        importFileButton.setOnClickListener { requestChooseFile(REQUEST_IMPORT_FILE) }
        executeQuickAction.setOnClickListener { mOsmAndHelper!!.executeQuickAction(0) }
        getQuickActionInfo.setOnClickListener { mOsmAndHelper!!.getQuickActionInfo(1) }

        // AIDL


        // Markers and Favorites

        aidlAddMapMarkerButton.setOnClickListener {
            showChooseLocationDialogFragment("Add map marker", ApiActionType.AIDL_ADD_MAP_MARKER)
        }
        aidlRemoveMapMarkerButton.setOnClickListener {
            showChooseLocationDialogFragment("Remove map marker", ApiActionType.AIDL_REMOVE_MAP_MARKER)
        }
        aidlUpdateMapMarkerButton.setOnClickListener {
            showChooseLocationDialogFragment("Update map marker", ApiActionType.AIDL_UPDATE_MAP_MARKER)
        }
        aidlRemoveAllActiveMapMarkersButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REMOVE_ALL_ACTIVE_MAP_MARKERS)
        }

        aidlAddFavoriteGroupButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_ADD_FAVORITE_GROUP)
        }
        aidlUpdateFavoriteGroupButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_UPDATE_FAVORITE_GROUP)
        }
        aidlRemoveFavoriteGroupButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REMOVE_FAVORITE_GROUP)
        }

        aidlAddFavoriteButton.setOnClickListener {
            showChooseLocationDialogFragment("Add favourite", ApiActionType.AIDL_ADD_FAVORITE)
        }
        aidlRemoveFavoriteButton.setOnClickListener {
            showChooseLocationDialogFragment("Remove favourite", ApiActionType.AIDL_REMOVE_FAVORITE)
        }
        aidlUpdateFavoriteButton.setOnClickListener {
            showChooseLocationDialogFragment("Update favourite", ApiActionType.AIDL_UPDATE_FAVORITE)
        }


        // Map layers and points

        aidlAddMapPointButton.setOnClickListener {
            showChooseLocationDialogFragment("Add map point", ApiActionType.AIDL_ADD_MAP_POINT)
        }
        aidlRemoveMapPointButton.setOnClickListener {
            showChooseLocationDialogFragment("Remove map point", ApiActionType.AIDL_REMOVE_MAP_POINT)
        }
        aidlUpdateMapPointButton.setOnClickListener {
            showChooseLocationDialogFragment("Update map point", ApiActionType.AIDL_UPDATE_MAP_POINT)
        }
        aidlShowMapPointButton.setOnClickListener {
            showChooseLocationDialogFragment("Show map point", ApiActionType.AIDL_SHOW_MAP_POINT)
        }

        aidlAddMapLayerButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_ADD_MAP_LAYER)
        }
        aidlRemoveMapLayerButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REMOVE_MAP_LAYER)
        }
        aidlUpdateMapLayerButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_UPDATE_MAP_LAYER)
        }
        aidlRefreshMapButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REFRESH_MAP)
        }

        // GPX and SQLITEDB Files

        aidlImportGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_IMPORT_GPX, false)
        }
        aidlShowGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_SHOW_GPX)
        }
        aidlHideGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_HIDE_GPX)
        }
        aidlGetActiveGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_GET_ACTIVE_GPX_FILES, false)
        }
        aidlGetImportedGpxButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_IMPORTED_GPX_FILES)
        }

        aidlRemoveGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REMOVE_GPX)
        }

        aidlGetSqliteDbFilesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_SQLITEDB_FILES)
        }

        aidlGetActiveSqliteDbFilesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_ACTIVE_SQLITEDB_FILES)
        }

        aidlShowSqliteDbFileButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SHOW_SQLITEDB_FILE)
        }

        aidlHideSqliteDbFileButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_HIDE_SQLITEDB_FILE)
        }

        aidlGetBitmapForGpxButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_BITMAP_FOR_GPX)
        }

        aidlCopyFileButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_COPY_FILE_TO_OSMAND)
        }


        // Location

        aidlSetMapLocationButton.setOnClickListener {
            showChooseLocationDialogFragment("Set map location", ApiActionType.AIDL_SET_MAP_LOCATION)
        }
        aidlStartGpxRecordingButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_START_GPX_REC)
        }
        aidlStopGpxRecordingButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_STOP_GPX_REC)
        }

        // Saving photo/audio/video

        aidlTakePhotoNoteButton.setOnClickListener {
            showChooseLocationDialogFragment("Take photo", ApiActionType.AIDL_TAKE_PHOTO)
        }
        aidlStartVideoRecordingButton.setOnClickListener {
            showChooseLocationDialogFragment("Start video recording", ApiActionType.AIDL_START_VIDEO_REC)
        }
        aidlStartAudioRecordingButton.setOnClickListener {
            showChooseLocationDialogFragment("Start audio recording", ApiActionType.AIDL_START_AUDIO_REC)
        }
        aidlStopRecordingButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_STOP_REC)
        }


        // Navigation

        aidlNavigateButton.setOnClickListener {
            showChooseLocationDialogFragment("Navigate to", ApiActionType.AIDL_NAVIGATE)
        }
        aidlNavigateGpxButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_NAVIGATE_GPX, delayed = false)
        }

        aidlPauseNavigationButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_PAUSE_NAVIGATION)
        }

        aidlResumeNavigationButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_RESUME_NAVIGATION)
        }

        aidlStopNavigationButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_STOP_NAVIGATION)
        }

        aidlMuteNavigationButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_MUTE_NAVIGATION)
        }

        aidlUnmuteNavigationButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_UNMUTE_NAVIGATION)
        }

        aidlSearchButton.setOnClickListener {
            showChooseLocationDialogFragment("Search here", ApiActionType.AIDL_SEARCH, false)
        }

        aidlNavigateSearchButton.setOnClickListener {
            showChooseLocationDialogFragment("Search and navigate to", ApiActionType.AIDL_NAVIGATE_SEARCH, false)
        }

        aidlRegisterForNavigationUpdatesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REGISTER_FOR_NAV_UPDATES)
        }
        aidlUnregisterForNavigationUpdatesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_UNREGISTER_FOR_NAV_UPDATES)
        }
        aidlGetBlockedRoads.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_AVOID_ROADS)
        }
        aidlAddBlockedRoad.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_ADD_AVOID_ROAD)
        }
        aidlRemoveBlockedRoads.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REMOVE_AVOID_ROAD)
        }

        aidlRegisterForVoiceRouterMessagesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REGISTER_FOR_VOICE_ROUTE_MESSAGES)
        }
        aidlUnRegisterForVoiceRouterMessagesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_UNREGISTER_FROM_VOICE_ROUTE_MESSAGES)
        }
        // OsmAnd Customization


        aidlAddFirstMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_ADD_FIRST_MAP_WIDGET)
        }
        aidlAddSecondMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_ADD_SECOND_MAP_WIDGET)
        }
        aidlUpdateFirstMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_UPDATE_FIRST_MAP_WIDGET)
        }
        aidlUpdateSecondMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_UPDATE_SECOND_MAP_WIDGET)
        }
        aidlRemoveFirstMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REMOVE_FIRST_MAP_WIDGET)
        }
        aidlRemoveSecondMapWidgetButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_REMOVE_SECOND_MAP_WIDGET)
        }


        aidlSetNavDrawerItemsButton.setOnClickListener {
            execApiAction(ApiActionType.AIDL_SET_NAV_DRAWER_ITEMS)
        }

        aidlRegisterForUpdatesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REGISTER_FOR_UPDATES)
        }

        aidlUnregisterFromUpdatesButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_UNREGISTER_FORM_UPDATES)
        }

        aidlHideDrawerProfile.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_HIDE_DRAWER_PROFILE)
        }

        aidlSetEnabledIdsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_ENABLED_UI_IDS)
        }

        aidlSetDisabledIdsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_DISABLED_UI_IDS)
        }

        aidlSetEnabledPatternsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_ENABLED_MENU_PATTERNS)
        }

        aidlSetDisabledPatternsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_DISABLED_MENU_PATTERNS)
        }

        aidlRegWidgetVisibilityButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REG_WIDGET_VISIBILITY)
        }

        aidlRegWidgetAvailabilityButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REG_WIDGET_AVAILABILITY)
        }

        aidlCustomizeOsmandSettingsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_CUSTOMIZE_OSMAND_SETTINGS)
        }

        aidlSetNavDrawerLogoButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_NAV_DRAWER_LOGO)
        }

        aidlSetNavDrawerFooterButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_NAV_DRAWER_FOOTER)
        }

        aidlRestoreOsmandButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_RESTORE_OSMAND)
        }

        aidlChangePluginStateButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_CHANGE_PLUGIN_STATE)
        }

        aidlRegisterForOsmandInitListenerButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REGISTER_FOR_OSMAND_INITIALIZATION)
        }

        aidlAddContextMenuButtonsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_ADD_CONTEXT_MENU_BUTTONS)
        }

        aidlRemoveContextMenuButtonsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_REMOVE_CONTEXT_MENU_BUTTONS)
        }

        aidlUpdateContextMenuButtonsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_UPDATE_CONTEXT_MENU_BUTTONS)
        }

        aidlAreOsmandSettingsCustomizedButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_ARE_OSMAND_SETTINGS_CUSTOMIZED)
        }

        aidlSetCustomizationButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_CUSTOMIZATION)
        }
        aidlSetUIMarginsButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_SET_UI_MARGINS)
        }
        aidlImportProfileButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_IMPORT_PROFILE)
        }
        aidlExportProfileButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_EXPORT_PROFILE)
        }
        aidlIsFragmentOpen.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_IS_FRAGMENT_OPEN)
        }
        aidlIsMenuOpen.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_IS_MENU_OPEN)
        }
        aidlExitAppButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_EXIT_APP)
        }
        aidlGetTextButton.setOnClickListener {
            execApiActionImpl(ApiActionType.AIDL_GET_TEXT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OSMAND_API) {
            val sb = StringBuilder()
            sb.append("ResultCode = <b>").append(resultCodeStr(resultCode)).append("</b>")
            if (data != null) {
                val extras = data.extras
                if (extras != null && extras.size() > 0) {
                    for (key in extras.keySet()) {
                        val value = extras.get(key)
                        if (sb.isNotEmpty()) {
                            sb.append("<br>")
                        }
                        sb.append(key).append(" = <b>").append(value).append("</b>")
                    }
                }
            }
            showOsmandInfoDialog(sb.toString())
        }
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_NAVIGATE_GPX_RAW_DATA -> {
                    handleGpxFileAsString(data!!) { result -> mOsmAndHelper!!.navigateRawGpx(true, result) }
                }
                REQUEST_NAVIGATE_GPX_URI -> {
                    handleFileUri(data!!, GPX_FILE_NAME) { result -> mOsmAndHelper!!.navigateGpxUri(true, result) }
                }
                REQUEST_SHOW_GPX_RAW_DATA -> {
                    handleGpxFileAsString(data!!) { result -> mOsmAndHelper!!.showRawGpx(result) }
                }
                REQUEST_SHOW_GPX_URI -> {
                    handleFileUri(data!!, GPX_FILE_NAME) { result -> mOsmAndHelper!!.showGpxUri(result) }
                }
                REQUEST_SHOW_GPX_RAW_DATA_AIDL -> {
                    Handler().postDelayed({
                        val color = GPX_COLORS[((GPX_COLORS.size - 1) * Math.random()).toInt()]
                        handleGpxFileAsString(data!!) { data -> mAidlHelper!!.importGpxFromData(data, GPX_FILE_NAME, color, true) }
                    }, delay)
                }
                REQUEST_SHOW_GPX_URI_AIDL -> {
                    Handler().postDelayed({
                        val color = GPX_COLORS[((GPX_COLORS.size - 1) * Math.random()).toInt()]
                        handleFileUri(data!!, GPX_FILE_NAME) { data -> mAidlHelper!!.importGpxFromUri(data, GPX_FILE_NAME, color, true) }
                    }, delay)
                }
                REQUEST_NAVIGATE_GPX_RAW_DATA_AIDL -> {
                    Handler().postDelayed({
                        handleGpxFileAsString(data!!) { data -> mAidlHelper!!.navigateGpxFromData(data, true, true) }
                    }, delay)
                }
                REQUEST_NAVIGATE_GPX_URI_AIDL -> {
                    Handler().postDelayed({
                        handleFileUri(data!!, GPX_FILE_NAME) { data -> mAidlHelper!!.navigateGpxFromUri(data, true, true) }
                    }, delay)
                }
                REQUEST_GET_GPX_BITMAP_URI_AIDL -> {
                    Handler().postDelayed({
                        handleFileUri(data!!, GPX_FILE_NAME) { data ->
                            val mgr: WindowManager? = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
                            if (mgr != null) {
                                val dm = DisplayMetrics()
                                mgr.defaultDisplay.getMetrics(dm)
                                mAidlHelper!!.getBitmapForGpx(data, dm.density, 350, 350, Color.RED)
                            }
                        }
                    }, delay)
                }
                REQUEST_COPY_FILE -> {
                    val fileName = File(data!!.data!!.path!!).name
                    handleFileUri(data, fileName) { result ->
                        val fileCopiedSuccessfully = mAidlHelper!!.fileImportImpl(result, "tracks/", fileName)
                        Toast.makeText(this, "File copied: $fileCopiedSuccessfully", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_IMPORT_FILE -> {
                    handleFileUri(data!!) { result -> mOsmAndHelper!!.importFile(result) }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        mAidlHelper!!.cleanupResources()
        super.onDestroy()
    }

    private fun setupButtonsIcons() {
        setDrawable(addFavoriteButton, R.drawable.ic_action_fav_dark)
        setDrawable(addMapMarkerButton, R.drawable.ic_action_flag_dark)
        setDrawable(showLocationButton, R.drawable.ic_action_flag_dark)
        setDrawable(startAudioRecButton, R.drawable.ic_action_micro_dark)
        setDrawable(startVideoRecButton, R.drawable.ic_action_video_dark)
        setDrawable(stopRecButton, R.drawable.ic_action_rec_stop)
        setDrawable(takePhotoButton, R.drawable.ic_action_photo_dark)
        setDrawable(startGpxRecButton, R.drawable.ic_action_play)
        setDrawable(stopGpxRecButton, R.drawable.ic_action_rec_stop)
        setDrawable(showGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(navigateGpxButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(navigateButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(navigateSearchButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(getInfoButton, R.drawable.ic_action_gabout_dark)
        setDrawable(pauseNavigationButton, R.drawable.ic_pause)
        setDrawable(resumeNavigationButton, R.drawable.ic_action_play)
        setDrawable(stopNavigationButton, R.drawable.ic_action_rec_stop)
        setDrawable(saveGpxButton, R.drawable.ic_type_file)
        setDrawable(clearGpxButton, R.drawable.ic_action_settings)
        setDrawable(muteNavigationButton, R.drawable.ic_action_micro_dark)
        setDrawable(unmuteNavigationButton, R.drawable.ic_action_micro_dark)
        setDrawable(importFileButton, R.drawable.ic_action_copy)
        setDrawable(aidlImportProfileButton, R.drawable.ic_action_import)
        setDrawable(aidlExportProfileButton, R.drawable.ic_action_export)
        setDrawable(executeQuickAction, R.drawable.ic_action_play)
        setDrawable(getQuickActionInfo, R.drawable.ic_action_gabout_dark)

        setDrawable(aidlAddMapMarkerButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlRemoveMapMarkerButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlUpdateMapMarkerButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlRemoveAllActiveMapMarkersButton, R.drawable.ic_action_flag_dark)

        setDrawable(aidlAddFavoriteGroupButton, R.drawable.ic_action_fav_dark)
        setDrawable(aidlRemoveFavoriteGroupButton, R.drawable.ic_action_fav_dark)
        setDrawable(aidlUpdateFavoriteGroupButton, R.drawable.ic_action_fav_dark)
        setDrawable(aidlAddFavoriteButton, R.drawable.ic_action_fav_dark)
        setDrawable(aidlRemoveFavoriteButton, R.drawable.ic_action_fav_dark)
        setDrawable(aidlUpdateFavoriteButton, R.drawable.ic_action_fav_dark)

        setDrawable(aidlAddMapPointButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlRemoveMapPointButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlUpdateMapPointButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlShowMapPointButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlAddMapLayerButton, R.drawable.ic_action_layers_dark)
        setDrawable(aidlRemoveMapLayerButton, R.drawable.ic_action_layers_dark)
        setDrawable(aidlUpdateMapLayerButton, R.drawable.ic_action_layers_dark)
        setDrawable(aidlRefreshMapButton, R.drawable.ic_action_refresh_dark)

        setDrawable(aidlImportGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(aidlShowGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(aidlHideGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(aidlGetActiveGpxButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlGetImportedGpxButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlGetBitmapForGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(aidlGetSqliteDbFilesButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlGetActiveSqliteDbFilesButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlShowSqliteDbFileButton, R.drawable.ic_type_file)
        setDrawable(aidlHideSqliteDbFileButton, R.drawable.ic_type_file)
        setDrawable(aidlCopyFileButton, R.drawable.ic_action_copy)

        setDrawable(aidlSetMapLocationButton, R.drawable.ic_action_flag_dark)
        setDrawable(aidlStartGpxRecordingButton, R.drawable.ic_action_play)
        setDrawable(aidlStopGpxRecordingButton, R.drawable.ic_action_rec_stop)

        setDrawable(aidlTakePhotoNoteButton, R.drawable.ic_action_photo_dark)
        setDrawable(aidlStartVideoRecordingButton, R.drawable.ic_action_video_dark)
        setDrawable(aidlStartAudioRecordingButton, R.drawable.ic_action_micro_dark)
        setDrawable(aidlStopRecordingButton, R.drawable.ic_action_rec_stop)

        setDrawable(aidlNavigateButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlNavigateGpxButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlRemoveGpxButton, R.drawable.ic_action_polygom_dark)
        setDrawable(aidlPauseNavigationButton, R.drawable.ic_pause)
        setDrawable(aidlResumeNavigationButton, R.drawable.ic_action_play)
        setDrawable(aidlStopNavigationButton, R.drawable.ic_action_rec_stop)
        setDrawable(aidlMuteNavigationButton, R.drawable.ic_action_micro_dark)
        setDrawable(aidlUnmuteNavigationButton, R.drawable.ic_action_micro_dark)
        setDrawable(aidlSearchButton, R.drawable.ic_action_search_dark)
        setDrawable(aidlNavigateSearchButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlRegisterForNavigationUpdatesButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlUnregisterForNavigationUpdatesButton, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlGetBlockedRoads, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlAddBlockedRoad, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlRemoveBlockedRoads, R.drawable.ic_action_gdirections_dark)
        setDrawable(aidlRegisterForVoiceRouterMessagesButton, R.drawable.ic_action_micro_dark)
        setDrawable(aidlUnRegisterForVoiceRouterMessagesButton, R.drawable.ic_action_micro_dark)

        setDrawable(aidlAddFirstMapWidgetButton, R.drawable.ic_action_settings)
        setDrawable(aidlRemoveFirstMapWidgetButton, R.drawable.ic_action_settings)
        setDrawable(aidlUpdateFirstMapWidgetButton, R.drawable.ic_action_settings)
        setDrawable(aidlAddSecondMapWidgetButton, R.drawable.ic_action_settings)
        setDrawable(aidlRemoveSecondMapWidgetButton, R.drawable.ic_action_settings)
        setDrawable(aidlUpdateSecondMapWidgetButton, R.drawable.ic_action_settings)

        setDrawable(aidlRegisterForUpdatesButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlUnregisterFromUpdatesButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlSetEnabledIdsButton, R.drawable.ic_action_settings)
        setDrawable(aidlHideDrawerProfile, R.drawable.ic_action_hide)
        setDrawable(aidlSetDisabledIdsButton, R.drawable.ic_action_settings)
        setDrawable(aidlSetEnabledPatternsButton, R.drawable.ic_action_settings)
        setDrawable(aidlSetDisabledPatternsButton, R.drawable.ic_action_settings)
        setDrawable(aidlRegWidgetVisibilityButton, R.drawable.ic_action_settings)
        setDrawable(aidlRegWidgetAvailabilityButton, R.drawable.ic_action_settings)
        setDrawable(aidlCustomizeOsmandSettingsButton, R.drawable.ic_action_settings)

        setDrawable(aidlSetNavDrawerLogoButton, R.drawable.ic_action_settings)
        setDrawable(aidlSetNavDrawerFooterButton, R.drawable.ic_action_settings)
        setDrawable(aidlSetNavDrawerItemsButton, R.drawable.ic_navigation_drawer)
        setDrawable(aidlRestoreOsmandButton, R.drawable.ic_action_settings)
        setDrawable(aidlChangePluginStateButton, R.drawable.ic_action_settings)
        setDrawable(aidlRegisterForOsmandInitListenerButton, R.drawable.ic_action_gabout_dark)

        setDrawable(aidlAddContextMenuButtonsButton, R.drawable.ic_action_settings)
        setDrawable(aidlRemoveContextMenuButtonsButton, R.drawable.ic_action_settings)
        setDrawable(aidlUpdateContextMenuButtonsButton, R.drawable.ic_action_settings)
        setDrawable(aidlAreOsmandSettingsCustomizedButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlSetCustomizationButton, R.drawable.ic_action_settings)
        setDrawable(aidlIsFragmentOpen, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlIsMenuOpen, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlSetUIMarginsButton, R.drawable.ic_action_settings)

        setDrawable(aidlExitAppButton, R.drawable.ic_action_gabout_dark)
        setDrawable(aidlGetTextButton, R.drawable.ic_action_gabout_dark)
    }

    private fun getDemoIntent(): Intent? {
        val startDemoIntent = packageManager.getLaunchIntentForPackage(packageName)
        startDemoIntent?.addCategory(Intent.CATEGORY_LAUNCHER)

        return startDemoIntent
    }

    private fun handleGpxFileAsString(data: Intent, action: (String) -> Unit) {
        try {
            val gpxParceDescriptor = contentResolver.openFileDescriptor(data.data!!, "r")
            val fileDescriptor = gpxParceDescriptor?.fileDescriptor
            val inputStreamReader = InputStreamReader(FileInputStream(fileDescriptor))
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder = StringBuilder()
            bufferedReader.lineSequence().forEach { string: String -> stringBuilder.append(string) }
            inputStreamReader.close()
            action(stringBuilder.toString())
        } catch (e: Throwable) {
            Log.e(TAG, "", e)
        }
    }

    private fun handleFileUri(data: Intent, action: (Uri) -> Unit) {
        val fileName = Utils.getNameFromContentUri(data.data!!, this)
        fileName?.let {
            handleFileUri(data, it, action)
        }
    }

    private fun handleFileUri(data: Intent, fileName: String, action: (Uri) -> Unit) {
        try {
            val parceDescriptor = contentResolver.openFileDescriptor(data.data!!, "r")
            val fileDescriptor = parceDescriptor?.fileDescriptor
            val inputStream = FileInputStream(fileDescriptor)
            val sharedDir = File(cacheDir, "share")
            if (!sharedDir.exists()) {
                sharedDir.mkdir()
            }
            val file = File(sharedDir, fileName)
            file.copyInputStreamToFile(inputStream)
            inputStream.close()
            val fileUri = FileProvider.getUriForFile(this, AUTHORITY, file)
            action(fileUri)
        } catch (e: NullPointerException) {
            Log.e(TAG, "", e)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "", e)
        }
    }

    fun requestChooseFile(requestCode: Int) {
        var intent: Intent
        if (Build.VERSION.SDK_INT < 19) {
            intent = Intent(Intent.ACTION_GET_CONTENT)
        } else {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        }
        intent.type = "*/*"
        intent = Intent.createChooser(intent, "Choose a file")

        val osmandHelper = this.mOsmAndHelper
        if (osmandHelper != null && osmandHelper.isIntentSafe(intent)) {
            this.startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(this, "You need an app capable of selecting files like ES Explorer", Toast.LENGTH_LONG).show()
        }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                Log.d(TAG, input.copyTo(fileOut).toString())
            }
        }
    }

    override fun osmandMissing() {
        OsmAndMissingDialogFragment().show(supportFragmentManager, null)
    }

    private fun setDrawable(button: Button, drawableRes: Int) {
        val icon = ContextCompat.getDrawable(this, drawableRes)
        if (icon != null) {
            DrawableCompat.setTint(icon, ContextCompat.getColor(this, R.color.iconColor))
            val compatIcon = DrawableCompat.wrap(icon)
            DrawableCompat.setTint(compatIcon, ContextCompat.getColor(this, R.color.iconColor))
            button.setCompoundDrawablesWithIntrinsicBounds(compatIcon, null, null, null)
        }
    }

    private fun getTimeStr(): String {
        val time = System.currentTimeMillis()
        val seconds = (time / 1000) % 60
        val minutes = time / (1000 * 60) % 60
        val hours = time / (1000 * 60 * 60) % 24
        return String.format("%d:%02d:%02d", hours, minutes, seconds)
    }

    private fun resultCodeStr(resultCode: Int): String {
        when (resultCode) {
            Activity.RESULT_OK -> return "OK"
            Activity.RESULT_CANCELED -> return "Canceled"
            Activity.RESULT_FIRST_USER -> return "First user"
            OsmAndHelper.RESULT_CODE_ERROR_UNKNOWN -> return "Unknown error"
            OsmAndHelper.RESULT_CODE_ERROR_NOT_IMPLEMENTED -> return "Feature is not implemented"
            OsmAndHelper.RESULT_CODE_ERROR_GPX_NOT_FOUND -> return "GPX not found"
            OsmAndHelper.RESULT_CODE_ERROR_INVALID_PROFILE -> return "Invalid profile"
            OsmAndHelper.RESULT_CODE_ERROR_PLUGIN_INACTIVE -> return "Plugin inactive"
            OsmAndHelper.RESULT_CODE_ERROR_EMPTY_SEARCH_QUERY -> return "Empty search query"
            OsmAndHelper.RESULT_CODE_ERROR_SEARCH_LOCATION_UNDEFINED -> return "Search location undefined"
            OsmAndHelper.RESULT_CODE_ERROR_QUICK_ACTION_NOT_FOUND -> return "Quick action not found"
        }
        return "" + resultCode
    }

    private fun showChooseLocationDialogFragment(title: String, apiActionType: ApiActionType, delayed: Boolean = true) {
        val args = Bundle()
        args.putString(ChooseLocationDialogFragment.TITLE_KEY, title)
        args.putString(ChooseLocationDialogFragment.API_ACTION_CODE_KEY, apiActionType.name)
        args.putBoolean(ChooseLocationDialogFragment.DELAYED_KEY, delayed)
        val chooseLocationDialogFragment = ChooseLocationDialogFragment()
        chooseLocationDialogFragment.arguments = args
        chooseLocationDialogFragment.show(supportFragmentManager, ChooseLocationDialogFragment.TAG)
    }

    private fun showSearchResultsDialogFragment(resultSet: List<SearchResult>, latitude: Double, longitude: Double) {
        val args = Bundle()
        args.putParcelableArrayList(SearchResultsDialogFragment.RESULT_SET_KEY, ArrayList(resultSet))
        args.putDouble(SearchResultsDialogFragment.LATITUDE_KEY, latitude)
        args.putDouble(SearchResultsDialogFragment.LONGITUDE_KEY, longitude)
        val searchResultsDialogFragment = SearchResultsDialogFragment()
        searchResultsDialogFragment.arguments = args
        searchResultsDialogFragment.show(supportFragmentManager, SearchResultsDialogFragment.TAG)
    }

    private fun getFeaturesEnabledIds(): List<String> {
        return listOf(
                OsmAndCustomizationConstants.MAP_CONTEXT_MENU_MEASURE_DISTANCE,
                OsmAndCustomizationConstants.GPX_FILES_ID,
                OsmAndCustomizationConstants.MAP_SOURCE_ID,
                OsmAndCustomizationConstants.OVERLAY_MAP,
                OsmAndCustomizationConstants.UNDERLAY_MAP,
                OsmAndCustomizationConstants.CONTOUR_LINES
        )
    }

    private fun getFeaturesDisabledIds(): List<String> {
        return listOf(
                OsmAndCustomizationConstants.LAYERS_HUD_ID,
                OsmAndCustomizationConstants.ROUTE_PLANNING_HUD_ID,
                OsmAndCustomizationConstants.QUICK_SEARCH_HUD_ID,
                OsmAndCustomizationConstants.SETTINGS_NAVIGATION_ID,
                OsmAndCustomizationConstants.SETTINGS_CONFIGURE_PROFILE_ID,
                OsmAndCustomizationConstants.NAVIGATION_SOUND_ID,
                OsmAndCustomizationConstants.NAVIGATION_OTHER_SETTINGS_ID
        )
    }

    private fun getFeaturesDisabledPatterns(): List<String> {
        return listOf(
                OsmAndCustomizationConstants.DRAWER_PLUGINS_ID,
                OsmAndCustomizationConstants.DRAWER_SETTINGS_ID,
                OsmAndCustomizationConstants.DRAWER_HELP_ID,
                OsmAndCustomizationConstants.DRAWER_BUILDS_ID,
                OsmAndCustomizationConstants.DRAWER_DIVIDER_ID,
                OsmAndCustomizationConstants.DRAWER_DOWNLOAD_MAPS_ID,
                OsmAndCustomizationConstants.DRAWER_SWITCH_PROFILE_ID,
                OsmAndCustomizationConstants.DRAWER_CONFIGURE_PROFILE_ID,
                OsmAndCustomizationConstants.DRAWER_CONFIGURE_MAP_ID,
                OsmAndCustomizationConstants.MAP_CONTEXT_MENU_ACTIONS,
                OsmAndCustomizationConstants.CONFIGURE_MAP_ITEM_ID_SCHEME
        )
    }

    private fun getFeaturesEnabledPatterns(): List<String> {
        return listOf(
                OsmAndCustomizationConstants.DRAWER_DASHBOARD_ID,
                OsmAndCustomizationConstants.DRAWER_SEARCH_ID,
                OsmAndCustomizationConstants.DRAWER_DIRECTIONS_ID,
                OsmAndCustomizationConstants.DRAWER_CONFIGURE_SCREEN_ID,
                OsmAndCustomizationConstants.DRAWER_OSMAND_LIVE_ID,
                OsmAndCustomizationConstants.DRAWER_TRAVEL_GUIDES_ID
        )
    }

    private fun getVisibilityWidgetsParams(): java.util.ArrayList<SetWidgetsParams> {
        return arrayListOf(
                SetWidgetsParams("next_turn", appModesPedestrianBicycle),
                SetWidgetsParams("next_turn_small", appModesPedestrian),
                SetWidgetsParams("next_next_turn", appModesPedestrianBicycle),
                SetWidgetsParams("intermediate_distance", appModesAll),
                SetWidgetsParams("distance", appModesAll),
                SetWidgetsParams("time", appModesAll),
                SetWidgetsParams("intermediate_time", appModesAll),
                SetWidgetsParams("speed", appModesPedestrianBicycle),
                SetWidgetsParams("max_speed", listOf(APP_MODE_CAR)),
                SetWidgetsParams("altitude", appModesPedestrianBicycle),
                SetWidgetsParams("gps_info", listOf(APP_MODE_BOAT)),
                SetWidgetsParams("bearing", listOf(APP_MODE_BOAT)),
                SetWidgetsParams("ruler", appModesAll),
                SetWidgetsParams("config", appModesNone),
                SetWidgetsParams("layers", appModesNone),
                SetWidgetsParams("compass", appModesNone),
                SetWidgetsParams("street_name", appModesExceptAirBoatDefault),
                SetWidgetsParams("back_to_location", appModesAll),
                SetWidgetsParams("monitoring_services", appModesNone),
                SetWidgetsParams("bgService", appModesNone)
        )
    }

    private fun getAvailabilityWidgetsParams(): java.util.ArrayList<SetWidgetsParams> {
        return arrayListOf(
                SetWidgetsParams("next_turn", appModesPedestrianBicycle),
                SetWidgetsParams("next_turn_small", appModesPedestrianBicycle),
                SetWidgetsParams("next_next_turn", appModesExceptAirBoatDefault),
                SetWidgetsParams("intermediate_distance", appModesAll),
                SetWidgetsParams("distance", appModesAll),
                SetWidgetsParams("time", appModesAll),
                SetWidgetsParams("intermediate_time", appModesAll),
                SetWidgetsParams("map_marker_1st", appModesNone),
                SetWidgetsParams("map_marker_2nd", appModesNone)
        )
    }

    private fun getCustomOsmandSettingsParams(): OsmandSettingsParams {
        val bundle = Bundle().apply {
            putString("available_application_modes", "$APP_MODE_BOAT,")
            putString("application_mode", APP_MODE_BOAT)
            putString("default_application_mode_string", APP_MODE_BOAT)
            putBoolean("driving_region_automatic", false)
            putBoolean("show_osmand_welcome_screen", false)
            putBoolean("show_coordinates_widget", true)
            putBoolean("show_compass_ruler", true)
            putString("map_info_controls", "ruler;")
            putString("default_metric_system", METRIC_CONST_NAUTICAL_MILES)
            putString("default_speed_system", SPEED_CONST_NAUTICALMILES_PER_HOUR)
        }
        return OsmandSettingsParams(OSMAND_SHARED_PREFERENCES_NAME, bundle)
    }

    private fun showOsmandInfoDialog(infoText: String) {
        val args = Bundle()
        args.putString(OsmAndInfoDialog.INFO_KEY, infoText)
        val infoDialog = OsmAndInfoDialog()
        infoDialog.arguments = args
        supportFragmentManager.beginTransaction().add(infoDialog, null).commitAllowingStateLoss()
    }
}