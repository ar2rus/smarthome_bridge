package com.gargon.smarthome.model.commands.control;

import com.gargon.smarthome.model.commands.ControlCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SwitchCommand implements ControlCommand {

    public static byte[] on(int switchId) {
        return new byte[]{0x01, (byte) switchId};
    }

    public static byte[] off(int switchId) {
        return new byte[]{0x00, (byte) switchId};
    }

    public static byte[] toggle(int switchId) {
        return new byte[]{0x02, (byte) switchId};
    }

    private String switchesList(byte data) {
        List<Integer> switches = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (((data >> i) & 1) == 1) {
                switches.add(i + 1);
            }
        }
        return switches.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public String toString(byte[] data) {
        switch (data.length) {
            case 2:
                switch (data[0]) {
                    case 0x00:
                        return String.format("Отключить выключатели: %d", switchesList(data[1]));
                    case 0x01:
                        return String.format("Включить выключатель: %d", switchesList(data[1]));
                    case 0x02:
                        return String.format("Переключить выключатель: %d", switchesList(data[1]));
                }
                break;
        }
        return ControlCommand.super.toString(data);
    }

}
