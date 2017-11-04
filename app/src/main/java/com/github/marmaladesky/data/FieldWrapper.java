package com.github.marmaladesky.data;

import java.io.Serializable;

public class FieldWrapper implements Serializable {

    private Field field;

    private String property;
    private Entry entry;

    public FieldWrapper(Field field) {
        this.field = field;
    }

    public FieldWrapper(String property, Entry entry) {
        this.property = property;
        this.entry = entry;
    }

    public String getFieldValue() {
        if(field != null) {
            return field.getValue();
        } else if (entry != null) {
            switch (property) {
                case Entry.PROPERTY_NAME: return entry.getName();
                case Entry.PROPERTY_DESCRIPTION: return entry.getDescription();
                case Entry.PROPERTY_NOTES: return entry.getNotes();
            }
            return null;
        } else
            return "";
    }

    public void setFieldValue(String newVal) throws Exception {
        if(field != null) {
            field.setValue(newVal);
        } else if (entry != null) {
            switch (property) {
                case Entry.PROPERTY_NAME: entry.setName(newVal); break;
                case Entry.PROPERTY_DESCRIPTION: entry.setDescription(newVal); break;
                case Entry.PROPERTY_NOTES: entry.setNotes(newVal); break;
                default: throw new Exception("Unknown Entity property " + property);
            }
        }
    }

    public String getUuid() throws Exception {
        if(field != null) {
            return field.getUuid();
        } else {
            switch (property) {
                case Entry.PROPERTY_NAME: return entry.getUuidName();
                case Entry.PROPERTY_DESCRIPTION: return entry.getUuidDescription();
                case Entry.PROPERTY_NOTES: return entry.getUuidNotes();
            }
        }
        throw new Exception("Unknown state");
    }

}
