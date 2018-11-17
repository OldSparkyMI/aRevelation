package de.igloffstein.maik.arevelation.helpers;

import android.app.Activity;
import android.view.View;

import com.github.marmaladesky.R;

public class FabHelper {

    public static void showFabIcon(Activity activity) {
        if (activity != null) {
            activity.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        }
    }

    public static void hideFabIcon(Activity activity) {
        if (activity != null) {
            activity.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        }
    }
}
