package de.igloffstein.maik.aRevelation;

import android.content.Context;

/**
 * Created by OldSparkyMI on 21.11.17.
 */

public enum EntryType {
    CREDITCARD, CRYPTOKEY, DATABASE, DOOR, EMAIL, FTP, GENERIC, PHONE, SHELL, REMOTEDESKTOP, VNC, WEBSITE, FOLDER;

    public static EntryType getFromPosition(int position){
        EntryType[] entryTypes = EntryType.values();
        return (position >= 0 && position <= EntryType.values().length) ? entryTypes[position] : null;
    }

    public static String[] getTranslatedEntryTypes(Context context){
        final EntryType[] entryTypes = EntryType.values();
        String[] translatedEntryTypes = new String[entryTypes.length];
        int idx = 0;
        for(EntryType entryType : EntryType.values()) {
            int id = context.getResources().getIdentifier(entryType.toString().toLowerCase(), "string", context.getPackageName());
            translatedEntryTypes[idx++] = ((id > 0) ? context.getString(id) : entryType.toString());
        }
        return translatedEntryTypes;
    }
}
