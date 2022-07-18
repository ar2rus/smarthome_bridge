package com.gargon.smarthome.logger.filters;

import com.gargon.smarthome.protocol.SmarthomeMessage;

public interface Filter {

    boolean filter(SmarthomeMessage smarthomeMessage);

}
