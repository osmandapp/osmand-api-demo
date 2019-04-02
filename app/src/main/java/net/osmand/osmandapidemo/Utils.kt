package main.java.net.osmand.osmandapidemo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.text.MessageFormat

object Utils {

	private const val METERS_IN_KILOMETER = 1000f

	private fun toRadians(angdeg: Double): Double {
		return angdeg / 180.0 * Math.PI
	}

	/**
	 * Gets distance in meters
	 */
	fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
		val R = 6372.8 // for haversine use R = 6372.8 km instead of 6371 km
		val dLat = toRadians(lat2 - lat1)
		val dLon = toRadians(lon2 - lon1)
		val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2)
		return 2.0 * R * 1000.0 * Math.asin(Math.sqrt(a))
	}

	fun getFormattedDistance(meters: Double): String {
		val format1 = "{0,number,0.0} "
		val format2 = "{0,number,0.00} "

		val mainUnitStr = "km"
		val mainUnitInMeters = METERS_IN_KILOMETER

		return when {
			meters >= 100 * mainUnitInMeters -> "${(meters / mainUnitInMeters + 0.5).toInt()} $mainUnitStr"
			meters > 9.99f * mainUnitInMeters -> MessageFormat.format(format1 + mainUnitStr, meters / mainUnitInMeters).replace('\n', ' ')
			meters > 0.999f * mainUnitInMeters -> MessageFormat.format(format2 + mainUnitStr, meters / mainUnitInMeters).replace('\n', ' ')
			else -> "${(meters + 0.5).toInt()} m"
		}
	}

	fun resourceToUri(ctx: Context, resId: Int): Uri {
		val pack = ctx.resources.getResourcePackageName(resId)
		val type = ctx.resources.getResourceTypeName(resId)
		val entry = ctx.resources.getResourceEntryName(resId)
		return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://$pack/$type/$entry")
	}
}
