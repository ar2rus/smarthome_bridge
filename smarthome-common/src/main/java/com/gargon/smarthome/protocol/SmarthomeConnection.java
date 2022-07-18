package com.gargon.smarthome.protocol;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;

import java.util.List;
import java.util.function.Predicate;

public interface SmarthomeConnection {

    /**
     * Открывает соединение
     */
    boolean open();

    /**
     * Закрывает текущее соединенеи
     */
    boolean close();

    boolean bindListener(SmarthomeDataListener listener);

    boolean unbindDataListener(SmarthomeDataListener listener);

    boolean sendData(SmarthomeDevice device, SmarthomeCommand command, byte[] data);

    SmarthomeMessage sendDataAndWaitResponse(SmarthomeDevice device, SmarthomeCommand command, byte[] data,
                                             SmarthomeMessageFilter responseFilter, int responseTimeout);

    List<SmarthomeMessage> sendDataAndWaitResponses(SmarthomeDevice device, SmarthomeCommand command, byte[] data,
                                                    SmarthomeMessageFilter responseFilter, int responseCount, int responseTimeout);

    int DATA_MAX_LENGTH = 128;

    /**
     * Отправляет сообщение
     *
     * @param device  устройство, которому производится отправка
     * @param command команда для отправки
     * @param data    данные для отправки
     * @return true в случае успешной отправки сообщения
     */
    default boolean send(final SmarthomeDevice device, final SmarthomeCommand command, final byte[] data) {
        return sendData(device, command, data);
    }

    /**
     * Отправляет сообщение без дополнительных данных
     *
     * @param device  устройствo , которому производится отправка
     * @param command команда для отправки
     * @return true в случае успешной отправки сообщения
     */
    default boolean send(final SmarthomeDevice device, final SmarthomeCommand command) {
        return send(device, command, null);
    }

    /**
     * Отправляет сообщение и получает ответ.
     * Отправляет сообщение и получает ответ-подтверждение отправки соответствующее {@code responseFilter}.
     * В случае неудачи отправки, отправляет еще раз и так @param numAttempts раз.
     * Ожидание ответа после каждой отправки состовляет {@code resonseTimeout} миллисекунд.
     * Реальная польза использования подобного метода наряду однократной отправкой
     * проявлятся в случаях когда устройство-получатель подвисло или временно
     * отключило обработку прерываний в результате чего протокол clunet на этом устройстве
     * оказывается временно парализованым. Подобная ситуация возникает, например, при обращении
     * к устройствам на которых производятся длительные операции измерения (температуры, влажности...).
     * Многократный посыл команды через незначительные промежутки времени позволяет избежать коллизий
     * и "поймать" момент свободного для приема команд устройства.
     * {@code resonseTimeout} следует выбирать соответствующим предельной длительности выполнения преобразований.
     * Выполнение данной команды с {@code numAttempts} большим единицы
     * недопустимо для операций последовательного изменения какой-либо величины,
     * в отличии от перевода в какое-либо постоянное состояния. Т.к в этом случае возможно возникновение
     * ошибки в момент подвисания сети clunet именно в момент передачи подтверждающей команды.
     *
     * @param device         устройствo, которому производится отправка
     * @param command        команда для отправки
     * @param responseFilter фильтр входящих сообщений для отбора сообщения с подтверждением успешной отправки
     * @param resonseTimeout время ожидания ответа после каждой отправки, в мс
     * @param numAttempts    количество попыток отправки сообщений
     * @return ответное сообщение
     */
    default SmarthomeMessage sendResponsible(final SmarthomeDevice device, final SmarthomeCommand command, final byte[] data,
                                             SmarthomeMessageFilter responseFilter, int resonseTimeout, int numAttempts) {
        SmarthomeMessage r = null;
        while (numAttempts-- > 0 && (r = sendDataAndWaitResponse(device, command, data, responseFilter, resonseTimeout)) == null)
            ;
        return r;
    }

    default SmarthomeMessage sendControlResponsible(final SmarthomeDevice device, final SmarthomeCommand command, final byte[] controlData,
                                                    Predicate<byte[]> responseDataFilter, int resonseTimeout, int numAttempts) {
        SmarthomeCommand responseCommand = command.getResponseCommand();
        if (responseCommand == null) {
            throw new RuntimeException("Undefined response command for \"" + command.toString() + "\"");
        }
        SmarthomeMessageFilter filter = SmarthomeMessageFilter.scr(device, responseCommand, responseDataFilter);
        SmarthomeMessage r = null;
        while (numAttempts-- > 0 && (r = sendDataAndWaitResponse(device, command, controlData, filter, resonseTimeout)) == null)
            ;
        return r;
    }

    default SmarthomeMessage sendResponsible(final SmarthomeDevice device, final SmarthomeCommand command, final byte[] data,
                                             SmarthomeMessageFilter responseFilter, int resonseTimeout) {
        return sendResponsible(device, command, data, responseFilter, resonseTimeout, 1);
    }

    default List<SmarthomeMessage> discovery(final int timeout) {
        return sendDataAndWaitResponses(SmarthomeDevice.BROADCAST,
                SmarthomeCommand.DISCOVERY,
                null,
                SmarthomeMessageFilter.sc(null, SmarthomeCommand.DISCOVERY_RESPONSE),
                -1,
                timeout);
    }

    default SmarthomeMessage ping(final SmarthomeDevice device, byte[] data, final int timeout) {
        return sendResponsible(device,
                SmarthomeCommand.PING,
                data,
                SmarthomeMessageFilter.scd(device, SmarthomeCommand.PING_REPLY, SmarthomeDevice.SUPRADIN),
                timeout);
    }

    default SmarthomeMessage reboot(final SmarthomeDevice device, final int bootTimeout) {
        return sendResponsible(device,
                SmarthomeCommand.PING,
                null,
                SmarthomeMessageFilter.scd(device, SmarthomeCommand.BOOT_COMPLETED, null),
                bootTimeout);
    }

}
