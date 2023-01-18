package main.java.net.osmand.osmandapidemo.util;

import android.content.Context;
import androidx.annotation.NonNull;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ToastQueue {

	private final ArrayList<WeakReference<Toast>> toasts = new ArrayList<>();

	public void showToast(@NonNull Context context, @NonNull String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toasts.add(new WeakReference<>(toast));
		toast.show();

		//Clean up WeakReference objects itself
		ArrayList<WeakReference<Toast>> nullToasts = new ArrayList<>();
		for (WeakReference<Toast> weakToast : toasts) {
			if (weakToast.get() == null) nullToasts.add(weakToast);
		}
		toasts.remove(nullToasts);
	}

	public void cancelAll() {
		for (WeakReference<Toast> weakToast : toasts) {
			if (weakToast.get() != null) weakToast.get().cancel();
		}
		toasts.clear();
	}

}
