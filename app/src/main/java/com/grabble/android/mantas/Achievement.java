package com.grabble.android.mantas;

/**
 * Created by Mantas on 09/11/2016.
 */

public class Achievement {
    private String title;
    private Integer imageId;

    public Achievement(String t, Integer id) {
        title = t;
        imageId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
