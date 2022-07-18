package com.gargon.smarthome.model.commands.info;

import com.gargon.smarthome.model.commands.InfoCommand;

import java.util.ArrayList;
import java.util.List;

public class SwitchInfoCommand implements InfoCommand<List<Integer>> {

    public static List<Integer> info(byte[] data) {
        List<Integer> switches = null;
        if (data.length == 1) {
            switches = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                if (((data[0] >> i) & 1) == 1) {
                    switches.add(i + 1);
                }
            }
        }
        return switches;
    }

    @Override
    public List<Integer> getInfo(byte[] data) {
        return info(data);
    }

    @Override
    public String toString(List<Integer> info) {
        if (info.isEmpty()) {
            return "Отключены все выключатели";
        } else {
            String r = "Включены выключатели: ";
            for (Integer i : info) {
                r += i.intValue() + ", ";
            }
            return r.substring(0, r.length() - 2);
        }
    }
}
