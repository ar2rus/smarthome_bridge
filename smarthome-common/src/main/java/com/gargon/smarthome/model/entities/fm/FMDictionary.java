package com.gargon.smarthome.model.entities.fm;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class FMDictionary {

    private Map<Float, String> stationList = null;

    private static FMDictionary instance;

    private FMDictionary(Map<Float, String> stationList) {
        if (stationList != null) {
            this.stationList = new TreeMap<>(stationList);
        }
    }

    public static synchronized void init(Map<Float, String> stationList) {
        if (instance == null) {
            instance = new FMDictionary(stationList);
        }
    }

    public static synchronized void init(String resourceName) {
        if (instance == null) {
            try {
                Properties fmProp = new Properties();
                InputStream stream = FMDictionary.class.getResourceAsStream(resourceName);
                fmProp.load(new InputStreamReader(stream, Charset.forName("UTF-8")));

                Map<Float, String> stationList = new HashMap<>();
                for (String key : fmProp.stringPropertyNames()) {
                    try {
                        stationList.put(Float.parseFloat(key), fmProp.getProperty(key));
                    } catch (Exception e) {
                        System.out.println("Can't read prop as station frequency: '" + key + "'");
                    }
                }
                init(stationList);
            } catch (Exception e) {
                System.out.println("Error while loading " + resourceName + ": " + e.getMessage());
            }
        }
    }

    public static FMDictionary getInstance() {
        return instance;
    }

    public Map<Float, String> getStationList() {
        return stationList;
    }

}
