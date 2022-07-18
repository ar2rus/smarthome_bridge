package com.gargon.smarthome.model.commands;

public interface ControlCommand extends Command {
    default byte[] requestInfo() {
        return new byte[]{(byte) 0xFF};
    }
    default String toString(byte[] data) {
        if (data.length == 1 && data[0] == 0xFF) {
            return "Запросить информацию";
        }
        return null;
    }
}
