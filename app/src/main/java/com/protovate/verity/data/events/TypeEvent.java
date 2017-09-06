package com.protovate.verity.data.events;

/**
 * Created by Yan on 7/13/15.
 */
public class TypeEvent {
    public static final int TYPE_PHOTO = 100;
    public static final int TYPE_VIDEO = 200;
    private int type;

    public TypeEvent(int type) {
        this.type = type;
    }

    public TypeEvent() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
