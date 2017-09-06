package com.protovate.verity.data;

import com.protovate.verity.data.responses.Invitations;

/**
 * Created by Yan on 7/31/15.
 */
public class Notification {
    private boolean isHeader;
    private Invitations.Data.Item item;

    public Notification(Invitations.Data.Item item, boolean isHeader) {
        this.isHeader = isHeader;
        this.item = item;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public Invitations.Data.Item getItem() {
        return item;
    }
}
