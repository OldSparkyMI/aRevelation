package de.igloffstein.maik.arevelation.helpers;

import android.util.Log;

import com.github.marmaladesky.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the current shown subfolder.
 * If the user enters a folder, the parent entry can be retrieved here!
 */
public class EntryStateHelper {

    private static final String LOG_TAG = EntryStateHelper.class.getSimpleName();
    private static final List<Entry> entryState = new ArrayList<>();

    /**
     * Clears the entrystate
     */
    public static void clear() {
        entryState.clear();
    }

    public static boolean remove(Entry entry) {
        if (entry != null) {
            return entryState.remove(entry);
        }

        return false;
    }

    /**
     * Removes the last entry from the state
     * @return the last entry
     */
    public static Entry remove() {
        Log.d(LOG_TAG, "removeLastEntry before: " + entryState.size());
        Entry e = null;
        if (entryState.size() > 0) {
            e = entryState.remove(entryState.size() - 1);
        }
        Log.d(LOG_TAG, "removeLastEntry after: " + entryState.size());
        return e;
    }

    /**
     * Returns the last entry from the state
     * @return the last entry
     */
    public static Entry get() {
        Log.d(LOG_TAG,"getLastEntry before: " + entryState.size());
        Entry e = entryState.size() > 0 ? entryState.get(entryState.size() - 1) : null;
        Log.d(LOG_TAG,"getLastEntry after: " + entryState.size());
        return e;
    }

    /**
     * Adds the given entry to the state (new last element)
     * @param e an entry for the state
     */
    public static void add(Entry e) {
        Log.d(LOG_TAG,"addToEntryState before: " + entryState.size());
        if (e != null && !isInList(e)) {
            entryState.add(e);
        }
        Log.d(LOG_TAG,"addToEntryState after: " + entryState.size());
    }

    /**
     * Checks if an given entry is already in the list
     * @param e check if this entry already exists
     * @return true = entry already in list | false = not in list
     */
    private static boolean isInList(Entry e) {
        String uuid = e.getUuid();
        if (uuid != null && !"".equals(uuid)) {
            for (int idx = 0; idx < entryState.size(); idx++) {
                if (uuid.equals(entryState.get(idx).getUuid())) {
                    return true;
                }
            }
        }
        return false;
    }
}
