package com.gargon.smarthome.model.commands.control;

import com.gargon.smarthome.model.commands.ControlCommand;

public class ChannelCommand implements ControlCommand {

    public byte[] nextChannel() {
        return new byte[]{(byte) 0x01};
    }

    public byte[] prevChannel() {
        return new byte[]{(byte) 0x02};
    }

    public static byte[] selectChannel(int channelNumber) {
        return new byte[]{(byte) 0x00, (byte) channelNumber};
    }

    @Override
    public String toString(byte[] data) {
        switch (data.length) {
            case 1:
                switch (data[0]) {
                    case 0x01:
                        return "Выбрать следующий канал";
                    case 0x02:
                        return "Выбрать предыдущий канал";
                }
                break;
            case 2:
                if (data[0] == 0x00) {
                    return String.format("Выбрать канал: %d", data[1]);
                }

        }
        return ControlCommand.super.toString(data);
    }
}
