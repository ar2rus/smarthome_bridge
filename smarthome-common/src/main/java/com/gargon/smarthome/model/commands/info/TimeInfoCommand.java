package com.gargon.smarthome.model.commands.info;

import com.gargon.smarthome.model.commands.InfoCommand;
import com.gargon.smarthome.model.enums.SmarthomeCommand;

public class TimeInfoCommand implements InfoCommand<Integer[]> {

    @Override
    public Integer[] getInfo(byte[] data) {
        if (data.length == 7) {
            return new Integer[]{(int) data[2], (int) data[1], (int) data[0] + 2000, (int) data[3], (int) data[4], (int) data[5], (int) data[6]};
        }
        return null;
    }

    @Override
    public String toString(Integer[] info) {
        return String.format("Текущая дата: %02d-%02d-%04d %02d:%02d:%02d, %d день недели",
                info[0], info[1], info[2], info[3], info[4], info[5], info[6]);
    }
}
