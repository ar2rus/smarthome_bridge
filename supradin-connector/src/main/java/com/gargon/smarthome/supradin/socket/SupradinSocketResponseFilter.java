package com.gargon.smarthome.supradin.socket;

/**
 * Интерфейс описывает фильтр данных ответа модуля Supradin
 */
public interface SupradinSocketResponseFilter {

    /**
     * Фильтрация данных
     *
     * @param dataReceived пакет фильтруемых данных
     * @return true если данные прошли фильтрацию
     */
    public boolean filter(byte[] dataReceived);

}
