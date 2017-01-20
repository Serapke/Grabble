package com.grabble.android.mantas;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Mantas on 18/01/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class MapsActivityTest {

    private static final LatLng coord1 = new LatLng(55.944032, -3.191879);
    private static final LatLng coord2 = new LatLng(55.944031, -3.191879);


    MapsActivity mapsActivity;

    @Before
    public void initialize() {
        mapsActivity = new MapsActivity();
    }

    @Test
    public void isAlreadyCollected_WithOneMatchingCollectedLetter_ReturnsTrue() {
        Placemark p = new Placemark("some letter", 'A', coord1);

        List<LatLng> collectedLetters = new ArrayList<>();
        collectedLetters.add(coord1);

        assertTrue(mapsActivity.isAlreadyCollected(p, collectedLetters));
    }

    @Test
    public void isAlreadyCollected_WithNoMatchingCollectedLetters_ReturnsFalse() {
        Placemark p = new Placemark("some letter", 'A', coord1);

        assertFalse(mapsActivity.isAlreadyCollected(p, null));

        List<LatLng> collectedLetters = new ArrayList<>();
        collectedLetters.add(coord2);

        assertFalse(mapsActivity.isAlreadyCollected(p, collectedLetters));
    }


}
