package com.github.marmaladesky.data;

import android.content.ContentResolver;
import android.net.Uri;
import com.github.marmaladesky.Cryptographer;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Persister;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Root(name="revelationdata")
@Order(attributes = {"version", "dataversion"})
public class RevelationData implements Serializable {

    private final String uuid = UUID.randomUUID().toString();

    @Attribute(name="version")
    private String version;

    @Attribute(name="dataversion")
    private String dataversion;

    @ElementList(inline = true)
    private List<Entry> list;

    public RevelationData(@Attribute(name="version") String version,
                          @Attribute(name="dataversion") String dataversion,
                          @ElementList(inline = true) List<Entry> list) {
        this.version = version;
        this.dataversion = dataversion;
        this.list = list;
    }

    public Entry getEntryById(String uuid) {
        return getEntryById(list, uuid);
    }

    private static Entry getEntryById(List<Entry> list, String uuid) {
        if(list != null)
            for(Entry e : list) {
                if(e.type.equals(Entry.TYPE_FOLDER)) {
                    Entry n = getEntryById(e.list, uuid);
                    if(n != null)
                        return n;
                }
                if(e.getUuid().equals(uuid))
                    return e;
            }
        return null;
    }

    public FieldWrapper getFieldById(String uuid) throws Exception {
        FieldWrapper fw = getFieldById(uuid, list);
        if(fw != null)
            return fw;
        else
            throw new Exception("Cannot find field with id=" + uuid);
    }

    private static FieldWrapper getFieldById(String uuid, List<Entry> entries) {
        for(Entry e : entries) {
            if(e.list != null) {
                FieldWrapper fw = getFieldById(uuid, e.list);
                if(fw != null) return fw;
            }
            if(e.fields != null) {
                for (Field f : e.fields) {
                    if (e.getUuidName().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_NAME, e);
                    } else if (e.getUuidDescription().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_DESCRIPTION, e);
                    } else if (e.getUuidNotes().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_NOTES, e);
                    } else if (f != null && f.getUuid().equals(uuid))
                        return new FieldWrapper(f);
                }
            } else {
                if (e.getUuidName().equals(uuid)) {
                    return new FieldWrapper(Entry.PROPERTY_NAME, e);
                } else if (e.getUuidDescription().equals(uuid)) {
                    return new FieldWrapper(Entry.PROPERTY_DESCRIPTION, e);
                } else if (e.getUuidNotes().equals(uuid)) {
                    return new FieldWrapper(Entry.PROPERTY_NOTES, e);
                }
            }
        }
        return null;
    }

    public List<Entry> getEntryGroupById(String uuid) throws Exception {
        if(this.uuid.equals(uuid)) {
            return list;
        } else {
            List<Entry> l = getEntryGroupById(list, uuid);
            if(l != null)
                return getEntryGroupById(list, uuid);
            else
                throw new Exception("Cannot find group with id = " + uuid);
        }
    }

    private static List<Entry> getEntryGroupById(List<Entry> entries, String uuid) {
        for(Entry e : entries) {
            if(e.type.equals(Entry.TYPE_FOLDER)) {
                if(e.getUuid().equals(uuid)) {
                    return e.list;
                } else {
                    List<Entry> l = getEntryGroupById(e.list, uuid);
                    if (l != null) return l;
                }
            }
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isEdited() {
        for(Entry e : list) if(e.isEdited()) return true;
        return false;
    }

    public void save(String file, String password, ContentResolver contentResolver) throws Exception {
        Serializer serializer = new Persister();
        Writer writer = new StringWriter();
        serializer.write(this, writer);
        byte[] encrypted = Cryptographer.encrypt(writer.toString(), password);

        OutputStream fop = contentResolver.openOutputStream(Uri.parse(file));
        fop.write(encrypted);
        fop.close();
        cleanUpdateStatus();
    }

    private void cleanUpdateStatus() {
        for(Entry e : list) e.cleanUpdateStatus();
    }
}
