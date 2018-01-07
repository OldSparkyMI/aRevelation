package de.igloffstein.maik.arevelation.helpers;

import com.github.marmaladesky.data.Entry;
import com.github.marmaladesky.data.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.igloffstein.maik.arevelation.enums.EntryType;
import de.igloffstein.maik.arevelation.enums.FieldType;

/**
 * Class with miscellaneous functions
 *
 * Created by OldSparkyMI on 22.11.17.
 */

public class EntryHelper {

    public static Entry newEntry(EntryType entryType) {
        List<Field> fields = new ArrayList<>(); //switch case which to fill this
        List<Entry> list = new ArrayList<>();   //in case of directory

        Entry entry = new Entry(entryType.toString(),
                null,
                null,
                null,
                fields,
                new Date().getTime(),
                list);

        addFields(entryType, fields);

        return entry;
    }

    private static void addFields(EntryType entryType, List<Field> fields) {

        if (fields != null) {
            switch (entryType) {
                case CREDITCARD: //
                    fields.add(new Field("", FieldType.CREDITCARD_CARDTYPE.getId()));
                    fields.add(new Field("", FieldType.CREDITCARD_CARDNUMBER.getId()));
                    fields.add(new Field("", FieldType.CREDITCARD_EXPIRYDATE.getId()));
                    fields.add(new Field("", FieldType.CREDITCARD_CCV.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PIN.getId()));
                    break;
                case CRYPTOKEY:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_CERTIFICATE.getId()));
                    fields.add(new Field("", FieldType.GENERIC_KEYFILE.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case DATABASE:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    fields.add(new Field("", FieldType.GENERIC_DATABASE.getId()));
                    break;
                case DOOR:
                    fields.add(new Field("", FieldType.GENERIC_LOCATION.getId()));
                    fields.add(new Field("", FieldType.GENERIC_CODE.getId()));
                    break;
                case EMAIL:
                    fields.add(new Field("", FieldType.GENERIC_EMAIL.getId()));
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case FTP:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PORT.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case GENERIC:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case PHONE:
                    fields.add(new Field("", FieldType.PHONE_PHONENUMBER.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PIN.getId()));
                    break;
                case SHELL:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_DOMAIN.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case REMOTEDESKTOP:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PORT.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case VNC:
                    fields.add(new Field("", FieldType.GENERIC_HOSTNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PORT.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
                case WEBSITE:
                    fields.add(new Field("", FieldType.GENERIC_URL.getId()));
                    fields.add(new Field("", FieldType.GENERIC_USERNAME.getId()));
                    fields.add(new Field("", FieldType.GENERIC_EMAIL.getId()));
                    fields.add(new Field("", FieldType.GENERIC_PASSWORD.getId()));
                    break;
            }
        }
    }
}
