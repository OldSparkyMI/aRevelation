package de.igloffstein.maik.arevelation.enums;

import android.content.Context;

/**
 * Enum for available entry types in revelation and aRevelation
 *
 * Created by OldSparkyMI on 21.11.17.
 */

public enum EntryType {
    CREDITCARD, CRYPTOKEY, DATABASE, DOOR, EMAIL, FTP, GENERIC, PHONE, SHELL, REMOTEDESKTOP, VNC, WEBSITE, FOLDER, UNKNOWN;

    public static EntryType getFromPosition(int position){
        EntryType[] entryTypes = EntryType.values();
        // don't count unknown entry state (-1)
        return (position >= 0 && position < EntryType.values().length-1) ? entryTypes[position] : UNKNOWN;
    }

    public static String[] getTranslatedEntryTypes(Context context){
        final EntryType[] entryTypes = EntryType.values();
        String[] translatedEntryTypes = new String[entryTypes.length-1];
        int idx = 0;
        for(EntryType entryType : EntryType.values()) {
            if (entryType != EntryType.UNKNOWN) {
                int id = context.getResources().getIdentifier(entryType.toString().toLowerCase(), "string", context.getPackageName());
                translatedEntryTypes[idx++] = ((id > 0) ? context.getString(id) : entryType.toString());
            }
        }
        return translatedEntryTypes;
    }
}
