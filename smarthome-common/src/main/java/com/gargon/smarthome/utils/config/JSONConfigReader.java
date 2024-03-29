package com.gargon.smarthome.utils.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;


public class JSONConfigReader {

    public static JSONObject read(String fileName) {
        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
            jsonData = jsonData.replaceAll("/\\*.*?\\*/", "");  //  /*blalba*/ comments replace
        } catch (IOException e) {
            Logger.getLogger(JSONConfigReader.class.getName()).log(Level.SEVERE, "Read config file error", e);
            jsonData = null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(JSONConfigReader.class.getName()).log(Level.SEVERE, "Config file close error", ex);
            }
        }

        try {
            if (jsonData != null) {
                return new JSONObject(jsonData);
            }
        } catch (Exception e) {
            Logger.getLogger(JSONConfigReader.class.getName()).log(Level.SEVERE, "JSON config file parse error", e);
        }
        return null;
    }

}
