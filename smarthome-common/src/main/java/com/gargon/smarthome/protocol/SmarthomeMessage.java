package com.gargon.smarthome.protocol;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.utils.HexDataUtils;

public class SmarthomeMessage {

    private final SmarthomeDevice src;
    private final SmarthomeDevice dst;
    private final SmarthomeCommand command;

    protected byte[] data;

    public SmarthomeMessage(SmarthomeDevice src, SmarthomeDevice dst, SmarthomeCommand command, byte[] data) {
        this.src = src;
        this.dst = dst;
        this.command = command;
        this.data = data;
    }

    public SmarthomeMessage(int src, int dst, int command, byte[] data) {
        this.src = SmarthomeDevice.getByAddress(src);
        this.dst = SmarthomeDevice.getByAddress(dst);
        this.command = SmarthomeCommand.getByCode(command);
        this.data = data;
    }

    public SmarthomeDevice getSrc() {
        return src;
    }

    public SmarthomeDevice getDst() {
        return dst;
    }

    public SmarthomeCommand getCommand() {
        return command;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("SRC=%s; DST=%s; CMD=%s; DATA=%s",
                src, dst, command, HexDataUtils.bytesToHex(data));
    }

}
