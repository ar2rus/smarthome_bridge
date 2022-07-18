package com.gargon.smarthome.protocol;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;

import java.util.function.Predicate;

public interface SmarthomeMessageFilter {

    boolean filter(SmarthomeMessage message);

    static SmarthomeMessageFilter scdf(Predicate<SmarthomeDevice> src, Predicate<SmarthomeCommand> cmd, Predicate<SmarthomeDevice> dst,
                                       SmarthomeMessageFilter f) {
        return message -> (src == null || src.test(message.getSrc()))
                && (cmd == null || cmd.test(message.getCommand()))
                && (dst == null || dst.test(message.getDst()))
                && (f == null || f.filter(message));
    }

    static SmarthomeMessageFilter scdf(SmarthomeDevice srcDevice, SmarthomeCommand cmd, SmarthomeDevice dstDevice,
                                       SmarthomeMessageFilter f) {
        return scdf(s -> srcDevice == null || s == srcDevice,
                    c -> cmd == null || c == cmd,
                    d -> dstDevice == null || d == dstDevice,
                    f);
    }

    static SmarthomeMessageFilter scd(SmarthomeDevice srcDevice, SmarthomeCommand cmd, SmarthomeDevice dstDevice){
        return scdf(srcDevice, cmd, dstDevice, null);
    }

    static SmarthomeMessageFilter scr(SmarthomeDevice srcDevice, SmarthomeCommand cmd,
                                      Predicate<byte[]> responseFilter) {
        return scdf(srcDevice, cmd, null, message -> responseFilter.test(message.getData()));
    }

    static SmarthomeMessageFilter sc(SmarthomeDevice srcDevice, SmarthomeCommand cmd){
        return scdf(srcDevice, cmd, null, null);
    }

    @Deprecated
    static SmarthomeMessageFilter scl(SmarthomeDevice srcDevice, SmarthomeCommand cmd, int dataLength) {
        return scr(srcDevice, cmd, data -> data.length == dataLength);
    }

}
