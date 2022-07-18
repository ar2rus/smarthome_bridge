package com.gargon.smarthome.controller.server.utils;

import com.gargon.smarthome.controller.server.ResponseFormatType;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;

import java.util.ArrayList;
import java.util.Arrays;

public class CommonUtils {

    public static String getString(String value, String defaultValue){
        if (value == null || value.isEmpty()){
            return defaultValue;
        }
        return value;
    }

}
