package com.gargon.smarthome.model.commands.info;

import com.gargon.smarthome.model.commands.InfoCommand;

public class BootCompletedCommand implements InfoCommand<Integer> {

    private static final int OK_MULTICAST_DEVICE_REBOOT = 0xFFFF;

    @Override
    public Integer getInfo(byte[] data) {
        if (data.length == 1){
            return (int)data[0];
        }
        return OK_MULTICAST_DEVICE_REBOOT;
    }

    @Override
    public String toString(Integer info) {
        return "Устройство загружено " + (info.equals(OK_MULTICAST_DEVICE_REBOOT) ? "" : ". MCUCSR=" + info);
    }
}
