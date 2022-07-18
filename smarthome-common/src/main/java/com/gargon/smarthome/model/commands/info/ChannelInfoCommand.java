package com.gargon.smarthome.model.commands.info;

import com.gargon.smarthome.model.commands.InfoCommand;

public class ChannelInfoCommand implements InfoCommand<Integer> {

    public static Integer info(byte[] data) {
        if (data.length == 1 && data[0] == 0xFF) {
            return (int) data[0];
        }
        return null;
    }

    public Integer getInfo(byte[] data) {
        return info(data);
    }

    @Override
    public String toString(Integer data) {
        return String.format("Текущий канал: %d", data);
    }

}
