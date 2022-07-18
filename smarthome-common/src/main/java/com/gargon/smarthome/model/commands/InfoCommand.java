package com.gargon.smarthome.model.commands;

public interface InfoCommand<T> extends Command {
    T getInfo(byte[] data);
    String toString(T info);
    default String toString(byte[] data) {
        T info = getInfo(data);
        return info == null ? null : toString(info);
    }
}
