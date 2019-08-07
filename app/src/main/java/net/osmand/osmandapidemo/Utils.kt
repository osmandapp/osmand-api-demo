package main.java.net.osmand.osmandapidemo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
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

	fun getFileSize(ctx: Context, uri: Uri): Long {
		val cursor = ctx.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
		var size: Long = -1
		cursor?.use {
			if (it.moveToFirst()) {
				val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
				if (sizeIndex != -1 && !it.isNull(sizeIndex)) {
					size = it.getLong(sizeIndex)
				}
			}
		}
		return size
	}

	fun getNameFromContentUri(contentUri : Uri, ctx : Context) : String? {
		val name : String?
		val returnCursor : Cursor = ctx.getContentResolver().query(contentUri, null, null, null, null);
		if (returnCursor != null && returnCursor.moveToFirst()) {
			var columnIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			if (columnIndex != -1) {
				name = returnCursor.getString(columnIndex)
			} else {
				name = contentUri.getLastPathSegment()
			}
		} else {
			name = null;
		}
		if (returnCursor != null && !returnCursor.isClosed()) {
			returnCursor.close();
		}
		return name;
	}
}
