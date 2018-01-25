package de.igloffstein.maik.arevelation.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.github.marmaladesky.ARevelation;
import com.github.marmaladesky.R;
import com.github.marmaladesky.RevelationListViewFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Helper class with some static function
 *
 * Created by OldSparkyMI on 19.11.17.
 */

public class ARevelationHelper {

    private static final String LOG_TAG = ARevelationHelper.class.getSimpleName();

    /**
     * Returns the settings time for
     * R.string.pref_title_lock_in_background: App to background -> lock file
     *
     * @param context the application context
     * @return the value
     */
    public static Integer preferenceLockInBackground(Context context){
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("preference_lock_in_background", "0"));
    }

    public static Locale getLocale(Resources resources) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? resources.getConfiguration().getLocales().getFirstMatch(resources.getAssets().getLocales())
                : resources.getConfiguration().locale;
    }

    /**
     * From https://developer.android.com/training/data-storage/files.html#WriteInternalStorage
     *
     * @param context the application context
     * @param uri     the uri to the revelation file (in our case the content provider uri)
     * @return returns the backup file path
     */
    private static File getTempFile(Context context, String uri) {
        File file = null;
        try {
            String fileName = Uri.parse(uri).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            Log.d(LOG_TAG, e.getLocalizedMessage());
        }
        return file;
    }

    private static String getFilenameFromRevelationFile(String url) {
        // I got something like: content://com.android.externalstorage.documents/document/primary%3AUser%2FPasswords%2Ftest
        String fileName = Uri.parse(url).getLastPathSegment().contains("/") ? "/" + Uri.parse(url).getLastPathSegment() : url;
        // now I got something like primary%3AUser%2FPasswords%2Ftest
        // and to match an uri, we have to add some silly protocol
        return Uri.parse("content://" + fileName).getLastPathSegment();
    }

    /**
     * Returns a path for the backup file
     * In most cases before saving a file
     *
     * @param context the application context
     * @param uri     the uri to the revelation file (in our case the content provider uri)
     * @return returns a File class to the backup file
     */
    private static String getRevelationBackupFile(Context context, String uri) {
        return getTempFile(context, getFilenameFromRevelationFile(uri) + ARevelation.BACKUP_FILE_ENDING).getPath();
    }

    public static String backupFile(Context context, String file) throws IOException {
        String backupFile = null;
        if (file != null && file != "") {
            backupFile = ARevelationHelper.getRevelationBackupFile(context, file);
            ARevelationHelper.copyFileUsingStream(file, backupFile, context.getContentResolver());
        }

        return backupFile;
    }

    public static void restoreFile(Context context, String currentFile, String backupFile) throws IOException {
        ARevelationHelper.copyFileUsingStream(backupFile, currentFile, context.getContentResolver());
    }

    private static void copyFileUsingStream(String source, String dest, ContentResolver contentResolver) throws IOException, NullPointerException {
        try (
            InputStream is = source.startsWith("content://") ? contentResolver.openInputStream(Uri.parse(source)) : new FileInputStream(source);
            OutputStream os = dest.startsWith("content://") ? contentResolver.openOutputStream(Uri.parse(dest)) : new FileOutputStream(dest)
        ) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    /**
     * Detach the target fragmentManager and reattach it instantly
     * @param fragmentManager
     * @param targetFragment
     */
    public static void redrawRevelationListViewFragment(FragmentManager fragmentManager, Fragment targetFragment){
        // Amazing piece of shit, but I don't know how to do it in another way
        fragmentManager.beginTransaction()
                .detach(targetFragment)
                .commit();
        fragmentManager.beginTransaction()
                .attach(targetFragment)
                .commit();
    }
}
