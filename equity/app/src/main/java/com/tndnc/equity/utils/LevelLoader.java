package com.tndnc.equity.utils;

import android.content.res.AssetManager;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.models.Level;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    private static Level lastLoadedLevel;

    public static void loadAllLevels(GameApplication app) {
        AssetManager assetManager = app.getAssets();

        try (InputStream in = assetManager.open("levels.xml")) {

            // initialise xml parser
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            // load levels container
            SparseArray<List<Level>> levels = app.getLevelsBySize();
            // parse xml
            parser.require(XmlPullParser.START_TAG, null, "levels");
            while(parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;

                String name = parser.getName();
                if (name.equals("level")) {
                    Level level = parseLevel(parser);
                    if (lastLoadedLevel != null) lastLoadedLevel.setNextLevel(level);

                    levels.get(level.getSize()).add(level);
                    Log.d("LevelLoader", "Level loaded: " + level);
                    if (lastLoadedLevel != null)
                        Log.d("LevelLoader", lastLoadedLevel.getId() + "-->" + lastLoadedLevel.getNextLevel().getId());
                    lastLoadedLevel = level;
                } else {
                    skip(parser);
                }
            }

            lastLoadedLevel = null;
            // close level file
            in.close();

        } catch (IOException | XmlPullParserException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static Level parseLevel(XmlPullParser parser) throws XmlPullParserException,
            IOException {

        String id;
        int size = 0;
        List<List<Integer>> preferences = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "level");
        id = parser.getAttributeValue(null, "name");
        size = Integer.parseInt(parser.getAttributeValue(null, "size"));
//        Log.d("LevelLoader", "Parsing level " + id);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            preferences.add(parsePreferences(parser));
        }

        parser.next();

        return new Level(id, size, preferences);
    }

    private static List<Integer> parsePreferences(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<Integer> prefs_for_agent = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, null, "preferences");
        String[] order = parser.getAttributeValue(null, "order").split("");

        for (String o : order) {
            if(o.equals("")) continue;
            prefs_for_agent.add(Integer.parseInt(o));
        }

        parser.next();

//        Log.d("LevelLoader", "Preference list: " + prefs_for_agent);
        return prefs_for_agent;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
