package com.github.marmaladesky.data;

import org.simpleframework.xml.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Root
@Order(elements = {"name", "description", "updated", "notes", "field"})
public class Entry implements Serializable {

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_UPDATED = "updated";
    public static final String PROPERTY_NOTES = "notes";

    public static final String TYPE_FOLDER = "folder";

    private final String uuid = UUID.randomUUID().toString();

    @Attribute(name="type")
    public String type;

    @Element(name = PROPERTY_NAME)
    private String name;
    private boolean isNameUpdated;
    private final String uuidName = UUID.randomUUID().toString();

    @Element(name = PROPERTY_DESCRIPTION, required = false)
    private String description;
    private boolean isDescriptionUpdated;
    private final String uuidDescription = UUID.randomUUID().toString();

    @Element(name = PROPERTY_UPDATED)
    public long updated;

    @Element(name = PROPERTY_NOTES, required = false)
    private String notes;
    private boolean isNotesUpdated;
    private final String uuidNotes = UUID.randomUUID().toString();

    @ElementList(inline = true, required = false)
    public List<Field> fields;

    @ElementList(name = "entry", inline = true, required = false)
    public List<Entry> list;

    public Entry(@Attribute(name="type") String type,
                 @Element(name = PROPERTY_NAME) String name,
                 @Element(name = PROPERTY_DESCRIPTION, required = false) String description,
                 @Element(name = PROPERTY_NOTES, required = false) String notes,
                 @ElementList(inline = true, required = false) List<Field> fields,
                 @Element(name = PROPERTY_UPDATED) long updated,
                 @ElementList(name = "entry", inline = true, required = false) List<Entry> list) {
        this.type = type;
        this.name = name;
        this.description = description != null ? description : "";
        this.notes = notes != null ? notes : "";
        this.fields = fields;
        this.updated = updated;
        this.list = list;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        isDescriptionUpdated = true;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        isNameUpdated = true;
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        isNotesUpdated = true;
        this.notes = notes;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUuidName() {
        return uuidName;
    }

    public String getUuidDescription() {
        return uuidDescription;
    }

    public String getUuidNotes() {
        return uuidNotes;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isEdited() {
        boolean isSelfEdited = isNameUpdated || isDescriptionUpdated || isNotesUpdated;
        if(isSelfEdited) return true;

        if(fields != null) for(Field f : fields) if(f.isUpdated()) return true;

        if(list != null) for(Entry e : list) if(e.isEdited()) return true;

        return false;
    }

    public void cleanUpdateStatus() {
        isNameUpdated = false;
        isDescriptionUpdated = false;
        isNotesUpdated = false;
        if(fields != null) for(Field f : fields) f.cleanUpdateStatus();
        if(list != null) for(Entry e : list) e.cleanUpdateStatus();
    }
}
