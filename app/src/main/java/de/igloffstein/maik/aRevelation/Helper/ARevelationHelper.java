package de.igloffstein.maik.aRevelation.Helper;

import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

/**
 * Created by OldSparkyMI on 19.11.17.
 */

public class ARevelationHelper {

    public static Locale getLocale(Resources resources) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? resources.getConfiguration().getLocales().getFirstMatch(resources.getAssets().getLocales())
                : resources.getConfiguration().locale;
    }
}
