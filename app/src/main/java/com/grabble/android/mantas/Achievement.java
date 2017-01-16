package com.grabble.android.mantas;

/**
 * Created by Mantas on 09/11/2016.
 */

public class Achievement {
    private String title;
    private Integer imageId;
    private boolean unlocked = false;

    public Achievement(String title, Integer id) {
        this.title = title;
        this.imageId = id;
    }

    public Achievement(String title, Integer id, boolean unlocked) {
        this.title = title;
        this.imageId = id;
        this.unlocked = unlocked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLocked() { return !this.unlocked; }

    public Integer getImageId() {
        return imageId;
    }

}
