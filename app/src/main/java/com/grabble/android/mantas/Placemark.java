package com.grabble.android.mantas;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mantas on 19/10/2016.
 */

public class Placemark {
    private final String name;
    private final char letter;
    private final LatLng coord;

    public Placemark(String name, char letter, LatLng coord) {
        this.name = name;
        this.letter = letter;
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public char getLetter() {
        return letter;
    }

    public LatLng getCoord() {
        return coord;
    }
}
