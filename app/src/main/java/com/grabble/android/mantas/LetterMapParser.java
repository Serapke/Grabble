package com.grabble.android.mantas;

import android.content.Context;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mantas on 19/10/2016.
 */

/**
 *  Used to parse the response from letter map provider to List of Placemarks
 *
 *  Most of the code is taken from SELP lecture notes.
 */
public class LetterMapParser {
    private static final String ns = null;

    private Context context;

    LetterMapParser(Context context) {
        this.context = context;
    }

    List<Placemark> parse(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        parser.nextTag();
        return readFeed(parser);
    }

    private List<Placemark> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Placemark> letters = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, context.getString(R.string.xml_file_format));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(context.getString(R.string.xml_placemark_key))) {
                letters.add(readPlacemark(parser));
            } else {
                skip(parser);
            }
        }

        return letters;
    }

    private Placemark readPlacemark(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, context.getString(R.string.xml_placemark_key));
        String name = null;
        char letter = ' ';
        LatLng coord = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(context.getString(R.string.xml_placemark_name))) {
                name = readName(parser);
            } else if (tag.equals(context.getString(R.string.xml_placemark_description))) {
                letter = readLetter(parser);
            } else if (tag.equals(context.getString(R.string.xml_placemark_point))) {
                coord = readCoord(parser);
            } else {
                skip(parser);
            }
        }
        return new Placemark(name, letter, coord);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, context.getString(R.string.xml_placemark_name));
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, context.getString(R.string.xml_placemark_name));
        return name;
    }

    private char readLetter(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, context.getString(R.string.xml_placemark_description));
        char letter = readText(parser).charAt(0);
        parser.require(XmlPullParser.END_TAG, ns, context.getString(R.string.xml_placemark_description));
        return letter;
    }

    private LatLng readCoord(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, context.getString(R.string.xml_placemark_point));
        LatLng placemark;
        String coordsString = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            if (tag.equals(context.getString(R.string.xml_placemark_point_coordinates))) {
                coordsString = readText(parser);
            } else {
                skip(parser);
            }
        }
        if (coordsString == null) return null;
        String[] coords = coordsString.split(",");
        double lng = Double.parseDouble(coords[0]);
        double lat = Double.parseDouble(coords[1]);
        placemark = new LatLng(lat, lng);
        return placemark;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
