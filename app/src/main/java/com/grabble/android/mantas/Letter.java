package com.grabble.android.mantas;

/**
 * Created by Mantas on 13/11/2016.
 */

public class Letter {
    private Character value;
    private Integer count;

    public Letter(Character v, Integer c) {
        value = v;
        count = c;
    }

    public Character getValue() {
        return value;
    }

    public void setValue(Character value) {
        this.value = value;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
