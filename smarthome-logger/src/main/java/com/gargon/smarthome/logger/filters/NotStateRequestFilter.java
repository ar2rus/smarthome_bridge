package com.gargon.smarthome.logger.filters;

/**
 * Отсеивает запросы на получение состояния
 */
public class NotStateRequestFilter extends DataByteFilter {

    public NotStateRequestFilter() {
        super(0, false, 0xFF);
    }

}
