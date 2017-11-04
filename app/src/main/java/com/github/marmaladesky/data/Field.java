package com.github.marmaladesky.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.io.Serializable;
import java.util.UUID;

@Root
public class Field implements Serializable {

    private String uuid = UUID.randomUUID().toString();

    @Attribute(name="id")
    public String id;

    @Text(required = false)
    private String value;

    private boolean isUpdated;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        isUpdated = true;
        this.value = value;
    }

    public Field(@Text(required = false) String value, @Attribute(name="id") String id) {
        this.value = value;
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void cleanUpdateStatus() {
        isUpdated = false;
    }
}
