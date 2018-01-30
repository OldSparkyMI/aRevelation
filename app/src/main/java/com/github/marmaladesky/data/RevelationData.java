package com.github.marmaladesky.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.marmaladesky.Cryptographer;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Root(name = "revelationdata")
@Order(attributes = {"version", "dataversion"})
public class RevelationData implements Serializable {

    private final String LOG_TAG = RevelationData.class.getSimpleName();
    private final String uuid = UUID.randomUUID().toString();
    private boolean edited = false;

    @Attribute(name = "version")
    private String version;

    @Attribute(name = "dataversion")
    private String dataversion;

    @Getter
    @ElementList(inline = true)
    private List<Entry> entries;

    public RevelationData(@Attribute(name = "version") String version,
                          @Attribute(name = "dataversion") String dataversion,
                          @ElementList(inline = true) List<Entry> entries) {
        this.version = version;
        this.dataversion = dataversion;
        this.entries = entries;
    }

    public Entry getEntryById(String uuid) {
        return getEntryById(entries, uuid);
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    /**
     * Removes a given entry, specified by uuid
     * @param uuid unique identifier of an entry
     * @return element successfully removed?
     */
    public boolean removeEntryById(String uuid) {
        if (entries != null)
            for (Entry e : entries) {
                if (e.type.equals(Entry.TYPE_FOLDER)) {
                    Entry n = getEntryById(e.list, uuid);
                    if (n != null) {
                        this.edited = true;
                        return entries.remove(n);
                    }
                }
                if (e.getUuid().equals(uuid)) {
                    this.edited = true;
                    return entries.remove(e);
                }
            }
        return false;
    }

    private static Entry getEntryById(List<Entry> list, String uuid) {
        if (list != null)
            for (Entry e : list) {
                if (e.type.equals(Entry.TYPE_FOLDER)) {
                    Entry n = getEntryById(e.list, uuid);
                    if (n != null)
                        return n;
                }
                if (e.getUuid().equals(uuid))
                    return e;
            }
        return null;
    }

    public FieldWrapper getFieldById(String uuid) throws Exception {
        FieldWrapper fw = getFieldById(uuid, entries);
        if (fw != null)
            return fw;
        else
            throw new Exception("Cannot find field with id=" + uuid);
    }

    private static FieldWrapper getFieldById(String uuid, List<Entry> entries) {
        for (Entry e : entries) {
            if (e.list != null && e.list.size() > 0) {
                FieldWrapper fw = getFieldById(uuid, e.list);
                if (fw != null) return fw;
            }
            if (e.fields != null && e.fields.size() > 0) {
                for (Field f : e.fields) {
                    if (e.getUuidName().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_NAME, e);
                    } else if (e.getUuidDescription().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_DESCRIPTION, e);
                    } else if (e.getUuidNotes().equals(uuid)) {
                        return new FieldWrapper(Entry.PROPERTY_NOTES, e);
                    } else if (f != null && f.getUuid().equals(uuid))
                        return new FieldWrapper(f, e);
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
        if (this.uuid.equals(uuid)) {
            return entries;
        } else {
            List<Entry> l = getEntryGroupById(entries, uuid);
            if (l != null)
                return getEntryGroupById(entries, uuid);
            else
                throw new Exception("Cannot find group with id = " + uuid);
        }
    }

    private static List<Entry> getEntryGroupById(List<Entry> entries, String uuid) {
        if (entries != null) {  // empty folder
            for (Entry e : entries) {
                if (e.type.equals(Entry.TYPE_FOLDER)) {
                    if (e.getUuid().equals(uuid)) {
                        return e.list;
                    } else {
                        List<Entry> l = getEntryGroupById(e.list, uuid);
                        if (l != null) return l;
                    }
                }
            }
        }
        return null;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isEdited() {

        if (edited) {
            // something is deleted
            return true;
        }

        for (Entry e : entries) {
            if (e.isEdited()) {
                // something is modified
                return true;
            }
        }
        return false;
    }

    public void save(Context context, String file, String password) throws Exception {
        if (entries.size() > 0) {
            // Revelation 0.4.14 (Desktop version) needs the xml header declaration
            Serializer serializer = new Persister(new Format("<?xml version=\"1.0\" encoding= \"UTF-8\" ?>"));
            Writer writer = new StringWriter();
            serializer.write(this, writer);
            byte[] encrypted = Cryptographer.encrypt(writer.toString(), password);

            if (file != null && !"".equals(file)) {
                try (OutputStream fop = context.getContentResolver().openOutputStream(Uri.parse(file))) {
                    fop.write(encrypted);
                    edited = false;
                    cleanUpdateStatus();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Log.d(LOG_TAG, "No content to save");
        }
    }

    private void cleanUpdateStatus() {
        for (Entry e : entries) e.cleanUpdateStatus();
    }
}
