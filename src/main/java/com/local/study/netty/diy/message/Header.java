package com.local.study.netty.diy.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Header implements Serializable{

    private static final long serialVersionUID = 1L;

    private int crc = 0xabef0101;

    private int length;

    private byte type;

    private byte priority;

    private Map<String ,Object> attachment = new HashMap<>();

    public int getCrc() {
        return crc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crc=" + crc +
                ", length=" + length +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
