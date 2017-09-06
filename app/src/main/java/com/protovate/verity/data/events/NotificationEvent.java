package com.protovate.verity.data.events;

/**
 * Created by Yan on 8/19/15.
 */
public class NotificationEvent {
    private boolean notification;

    public NotificationEvent(boolean notification) {
        this.notification = notification;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
