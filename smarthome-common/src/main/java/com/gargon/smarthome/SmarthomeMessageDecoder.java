package com.gargon.smarthome;

import com.gargon.smarthome.model.entities.mirobo.MiroboVacuumState;
import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.entities.fm.FMDictionary;
import com.gargon.smarthome.model.entities.heatfloor.enums.HeatfloorMode;
import com.gargon.smarthome.utils.HexDataUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 *  Декодирование бинарных данных команд в текстовое представление
 */
public class SmarthomeMessageDecoder {

    private static Float decodeDS18B20Temperature(byte[] data, int offset) {
        if (data != null) {
            int v = ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF);
            if (v != 0xFFFF) {
                return v / 10f;
            }
        }
        return null;
    }

    public static Map<Integer, Integer> decodeDimmerInfo(byte[] data) {
        if (data != null && data.length > 0) {
            Map<Integer, Integer> dimmerInfo = new LinkedHashMap<>();
            int cnt = data[0] & 0xFF;
            if (data.length == cnt * 2 + 1) {
                for (int i = 1; i < data.length; i += 2) {
                    dimmerInfo.put(data[i] & 0xFF, data[i + 1] & 0xFF);
                }
            }
            return dimmerInfo;
        }
        return null;
    }

    public static Map<String, Float> decodeTemperatureInfo(byte[] data) {
        try {
            Map<String, Float> r = new HashMap<>();
            if (data.length > 0) {
                int num = data[0] & 0xFF;
                int offset = 1;
                for (int i = 0; i < num; i++) {
                    int type = data[offset++];
                    String sensorId = null;
                    Float temperatureValue = null;
                    switch (type) {
                        case 0: //1-wire
                            sensorId = HexDataUtils.bytesToHex(Arrays.copyOfRange(data, offset, offset + 8));
                            temperatureValue = decodeDS18B20Temperature(data, offset + 8);
                            offset += 10;
                            break;
                        case 1: //dht
                            sensorId = "DHT-22 (" + String.valueOf(data[offset] & 0xFF) + ")";
                            ByteBuffer bb = ByteBuffer.wrap(data, offset + 1, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            int t = bb.getShort();
                            if (t != 0xFFFF) {
                                temperatureValue = t / 10f;
                            }
                            offset += 3;
                            break;
                        case 2: //bmp/bme
                            sensorId = "BME280 (" + String.valueOf(data[offset] & 0xFF) + ")";
                            bb = ByteBuffer.wrap(data, offset + 1, 2);
                            bb.order(ByteOrder.LITTLE_ENDIAN);
                            t = bb.getShort();
                            if (t != 0xFFFF) {
                                temperatureValue = t / 100f;
                            }
                    }
                    if (temperatureValue != null) {
                        r.put(sensorId, temperatureValue);
                    }
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> decodeOneWireInfo(byte[] data) {
        try {
            List<String> r = new ArrayList<>();
            if (data.length > 0) {
                for (int i = 0; i < data.length; i += 8) {
                    r.add(HexDataUtils.bytesToHex(Arrays.copyOfRange(data, i, i + 8)));
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    /*Краткая информация о состоянии теплого пола, в консоле используется полная */
    public static Map<String, Integer> decodeHeatfloorInfo(byte[] data) {
        try {
            Map<String, Integer> r = null;
            if (data.length > 0 && data[0] >= 0 && data[0] < 8) {
                r = new HashMap<>();
                int cnt = data[0];
                if (data.length == cnt * 6 + 1) {
                    for (int i = 0; i < cnt; i++) {
                        int index = i * 6 + 1;
                        r.put(String.valueOf(i), ((data[index + 0] & 0xFF) << 8) | (data[index + 1] & 0xFF)); //канал -> (режим | состояние)
                    }
                }
            }
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    public static Float decodePressureInfo(byte[] data) {
        try {
            if (data.length >= 4) {
                ByteBuffer bb = ByteBuffer.wrap(data);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int p = bb.getInt();
                if (p != 0xFFFFFFFF) {
                    return p / 1000f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Float decodeHumidityInfo(byte[] data) {
        try {
            if (data.length >= 2) {
                ByteBuffer bb = ByteBuffer.wrap(data);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int h = bb.getChar();
                if (h != 0xFFFF) {
                    return h / 10f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Float decodeVoltageInfo(byte[] data) {
        try {
            if (data.length >= 2) {
                ByteBuffer bb = ByteBuffer.wrap(data);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                int v = bb.getChar();
                if (v != 0xFFFF) {
                    return v / 100f;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static int[] decodeLightLevelInfo(byte[] data) {
        if (data.length == 2 && (data[0] == 0 || data[0] == 1)) {
            return new int[]{data[0], data[1]};
        } else if (data.length == 3 && data[0] == 2) {
            ByteBuffer bb = ByteBuffer.wrap(data, 1, 2);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            int l = bb.getChar();
            if (l != 0xFFFF) {
                return new int[]{data[0], l};
            }
        }
        return null;
    }
    
    
    private static final int METEO_PARAM_T = 0;
    private static final int METEO_PARAM_H = 1;
    private static final int METEO_PARAM_P = 2;
    private static final int METEO_PARAM_L = 3;
    
    public static Float[] decodeMeteoInfo(byte[] data) {
        try {
            if (data.length == 9) {
                Float[] meteo = {null, null, null, null};
                ByteBuffer bb = ByteBuffer.wrap(data);
                bb.order(ByteOrder.LITTLE_ENDIAN);

                int valid = bb.get();

                int t = bb.getShort();
                if (((valid >> METEO_PARAM_T) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_T] = t / 100f;
                }

                int h = bb.getChar();
                if (((valid >> METEO_PARAM_H) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_H] = h / 10f;
                }

                int p = bb.getChar();
                if (((valid >> METEO_PARAM_P) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_P] = p / 10f;
                }

                int l = bb.getChar();
                if (((valid >> METEO_PARAM_L) & 0x01) == 0x01) {
                    meteo[METEO_PARAM_L] = (float) l;
                }
                return meteo;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static String decodeHeatfloorMode(int modeId){
        HeatfloorMode mode = HeatfloorMode.getById(modeId);
        if (mode == null){
            return "???";
        }
        return mode.getName();
    }

    public static String heatfloorModeExt(byte[] value, int startIndex) {
        int modeId = value[startIndex];
        HeatfloorMode mode = HeatfloorMode.getById(modeId);
        if (mode != null) {
            switch (mode) {
                case MANUAL:
                    return String.format("%s (t=%d °C)", mode, value[startIndex + 1]);
                case DAY:
                case DAY_FOR_TODAY:
                    return String.format("%s (Пр.=%d)", mode, value[startIndex + 1]);
                case WEEK:
                    return String.format("%s (Пp.[пн-пт]=%d; Пр.[сб]=%d; Пр.[вс]=%d)", mode, value[startIndex + 1], value[startIndex + 2], value[startIndex + 3]);
                case PARTY:
                    return String.format("%s (t=%d °C, осталось %d сек.)", mode, value[startIndex + 1], ((value[startIndex + 3] & 0xFF) << 8) | (value[startIndex + 2] & 0xFF));
            }
        }
        return null;
    }

    public static String decodeFMInfo(byte[] data) {
        try {
            String r = null;
            switch (data[0]) {
                case 0x00:  //channel info
                    if (data.length == 6) {
                        float freq = (((data[3] & 0xFF) << 8) | (data[2] & 0xFF)) / 100f;
                        FMDictionary fmDictionary = FMDictionary.getInstance();
                        String station = null;
                        if (fmDictionary != null) {
                            station = fmDictionary.getStationList().get(freq);
                        }
                        r = String.format(Locale.ROOT, "FM: канал=%s; %sчастота=%.2f МГц; уровень=%d%%; %s",
                                data[1] < 0 ? "-" : (data[1] + 1),
                                station != null ? "Станция=\"" + station + "\"; " : "",
                                freq,
                                100 * data[4] / 15,
                                data[5] > 0 ? "Стерео" : ""
                        );
                    }
                    break;
                case 0x01:  //state info
                    if (data.length == 6) {
                        r = String.format("FM: standby=%s; mute=%s; mono=%s; hcc=%s; snc=%s",
                                data[1] > 0 ? "on" : "off",
                                data[2] > 0 ? "on" : "off",
                                data[3] > 0 ? "on" : "off",
                                data[4] > 0 ? "on" : "off",
                                data[5] > 0 ? "on" : "off");
                    }
                    break;
                case 0x02:  //search info
                    break;
            }
            return r;

        } catch (Exception e) {
        }
        return null;
    }
    
    public static String toString(SmarthomeCommand command, byte[] data) {
        switch (command) {
            case DISCOVERY_RESPONSE:
                return String.format("Обнаружено устройство: %s", new String(data));
            case BOOT_COMPLETED:
                return String.format("Устройство перезагружено");
            case POWER_INFO:
                if (data.length == 1){
                    return String.format("Устройство %s", data[0] == 1 ? "включено" : "отключено");
                }
                break;
            case SWITCH_INFO:
                if (data.length == 1){
                    if (data[0] == 0){
                        return "Отключены все выключатели";
                    }else{
                        String r = "Включены выключатели: ";
                        for (int i=0; i<8; i++){
                            if (((data[0]>>i) & 1) == 1){
                                r += (i+1) + ", ";
                            }
                        }
                        return r.substring(0, r.length() - 2);
                    }
                }
                break;
            case BUTTON_INFO:
                if (data.length == 2){
                    String buttonState;
                    switch (data[1]){
                        case 0:
                           buttonState = "не нажата";
                           break;
                        case 1:
                           buttonState = "нажата";
                           break;
                        case 2:
                           buttonState = "в положении ВКЛ";
                           break;
                        case 3:
                           buttonState = "в положении ОТКЛ";
                           break;
                        default:
                            buttonState = "???";
                    }
                    return String.format("Кнопка %d: %s", data[0], buttonState);
                }
                break;
            case ROTARY_DIAL_NUMBER_INFO:
                if (data.length > 0){
                    switch (data[0]){
                        case 0: 
                            return "Начат набор номера";
                        case 1:
                            return String.format("Набран номер: %s", Arrays.toString(Arrays.copyOfRange(data, 1, data.length)));
                        case 2:
                            return String.format("Набираемый номер: %s", Arrays.toString(Arrays.copyOfRange(data, 1, data.length)));
                        case 3:
                            return "Набор номера прерван";
                    }
                }
                break;
             case TIME_INFO:
                if (data.length == 7){
                    return String.format("Текущая дата: %02d-%02d-%04d %02d:%02d:%02d, %d день недели", data[2], data[1], data[0]+2000, data[3], data[4], data[5], data[6]);
                }
                break;
            case TEMPERATURE_INFO:
                String response = "";
                Map<String, Float> t = decodeTemperatureInfo(data);
                if (t != null){
                    for (Map.Entry<String, Float> entry : t.entrySet()){
                        response += String.format(Locale.ROOT, "T[%s]=%.2f°C; ", entry.getKey(), entry.getValue());
                    }
                }
                return response;
            case ONEWIRE_INFO:
                response = "";
                List<String> ow = decodeOneWireInfo(data);
                if (ow != null) {
                    for (String o : ow) {
                        response += o + "; ";
                    }
                }
                return response;
            case HUMIDITY_INFO:
                Float h = decodeHumidityInfo(data);
                if (h != null){
                    return String.format(Locale.ROOT, "%.1f%%", h);
                }
                break;
            case PRESSURE_INFO:
                Float p = decodePressureInfo(data);
                if (p != null){
                    return String.format(Locale.ROOT, "%.3f мм рт.ст.", p);
                }
                break;
            case METEO_INFO:
                Float[] meteo = decodeMeteoInfo(data);
                if (meteo != null){
                    String meteo_s = "";
                    for (int i=0; i<meteo.length; i++){
                        if (meteo[i] != null){
                            switch (i){
                                case METEO_PARAM_T:
                                    meteo_s += String.format(Locale.ROOT, "T=%.2f°C; ", meteo[i]);
                                    break;
                                case METEO_PARAM_H:
                                    meteo_s += String.format(Locale.ROOT, "H=%.1f%%; ", meteo[i]);
                                    break;
                                case METEO_PARAM_P:
                                    meteo_s += String.format(Locale.ROOT, "P=%.1f мм рт.ст.; ", meteo[i]);
                                    break;
                                case METEO_PARAM_L:
                                    meteo_s += String.format(Locale.ROOT, "L=%d лк; ", meteo[i].intValue());
                                    break;
                            }
                        }
                    }
                    return meteo_s;
                }
                break;
            case VOLTAGE_INFO:
                Float v = decodeVoltageInfo(data);
                if (v != null){
                    return String.format(Locale.ROOT, "%.2f В", v);
                }
                break;
            case MOTION_INFO:
                if (data.length == 2) {
                    response = data[0] == 1 ? "Обнаружено движение" : "Движение отсутствует";
                    response += " (локация " + data[1] + ")";
                    return response;
                }
                break;
            case DOORS_INFO:
                if (data.length == 1) {
                    return data[0] > 0 ? "Дверь открыта" : "Дверь закрыта";
                }
                break;
            case DEVICE_STATE_INFO:
                if (data.length == 2) {
                    switch (data[0]) {
                        case 3:
                            return String.format("mirobo: %s", MiroboVacuumState.getByCode(data[1]));
                        default:
                            return String.format("Устройство %d: %s", data[0], data[1] == 1 ? "включено" : "отключено");
                    }
                }
                break;
            case LIGHT_LEVEL_INFO:
                int[] ll = decodeLightLevelInfo(data);
                if (ll != null) {
                    if (ll[0] == 0 || ll[0] == 1){
                    return String.format("Уровень освещенности %s (%d%%)", 
                            ll[0] == 1 ? "высокий" : "низкий", ll[1]);
                    }else if (ll[0] == 2){
                        return String.format("%d лк", ll[1]);
                    }
                }
                break;
            case DIMMER_INFO:
                Map<Integer, Integer> dimmerInfo = decodeDimmerInfo(data);
                if (dimmerInfo != null) {
                    String dimmerInfoString = "";
                    for (Map.Entry<Integer, Integer> e : dimmerInfo.entrySet()) {
                        dimmerInfoString += String.format("Канал %d: уровень %d (%d%%);", e.getKey(), e.getValue(), e.getValue() * 100 / 255);
                    }
                    return dimmerInfoString;
                }
                break;
            case CHARGE_INFO:
                if (data.length == 1) {
                    if (data[0] == 0x00) {
                        return "Зарядное устройство отключено";
                    }
                } else if (data.length == 3) {
                    if (data[0] == 0x01) {
                        return String.format("Зарядное устройство включено (осталось: %d секунд)",
                                ((data[2] & 0xFF) << 8) | (data[1] & 0xFF));
                    }
                }
                break;
                
            case HEATFLOOR_INFO:
                if (data.length > 0) {
                    response = "";
                    switch (data[0] & 0xFF) {
                        case 0x00:  //выкл
                            response = "Устройство отключено";
                            break;
                        case 0xFE:  //режимы
                            int cnt = (data.length - 1) / 4;
                            for (int i = 0; i < cnt; i++) {
                                response += String.format("Канал %d: %s; ", i, heatfloorModeExt(data, i * 4 + 1));
                            }
                            break;
                        case 0xF0:
                        case 0xF1:
                        case 0xF2:
                        case 0xF3:
                        case 0xF4:
                        case 0xF5:
                        case 0xF6:
                        case 0xF7:
                        case 0xF8:
                        case 0xF9:  //программы
                            response = String.format("Пр.%d: ", data[0] & 0x0F);
                            cnt = data[1];
                            if (cnt > 0 && cnt < 10){
                                for (int i=0; i<cnt; i++){
                                    int pos = i*2 + 2;
                                    response+= String.format("%02d ч.→%d °C; ", data[pos], data[pos+1]);
                                }
                            }else{
                                response += "не установлена";
                            }
                            break;
                        default:    //статус
                            cnt = data[0];
                            if (data.length == cnt * 6 + 1) {
                                for (int i = 0; i < cnt; i++) {
                                    int index = i * 6 + 1;

                                    String mode = decodeHeatfloorMode(data[index]);

                                    response += String.format("Канал %d: %s", i, mode);

                                    if (data[index] > 0) {  //не выкл
                                        String solution = "???";
                                        switch (data[index + 1]) {
                                            case 0:
                                                solution = "ожидание";
                                                break;
                                            case 1:
                                                solution = "нагрев";
                                                break;
                                            case -1:
                                                solution = "охлаждение";
                                                break;
                                            case -2:
                                                solution = "ошибка чтения датчика";
                                                break;
                                            case -3:
                                                solution = "ошибка диапазона значения датчика";
                                                break;
                                            case -4:
                                                solution = "ошибка диспетчера";
                                                break;
                                        }
                                        Float sensorT = decodeDS18B20Temperature(data, index + 2);
                                        String sensorTStr = "-";
                                        if (sensorT != null) {
                                            sensorTStr = String.format(Locale.ROOT, "%.1f°C", sensorT);
                                        }
                                        Float settingT = decodeDS18B20Temperature(data, index + 4);
                                        String settingTStr = "-";
                                        if (settingT != null) {
                                            settingTStr = String.format(Locale.ROOT, "%.1f°C", settingT);
                                        }
                                        response += String.format(" [%s (%s/%s)]", solution, sensorTStr, settingTStr);
                                    }
                                    response += "; ";
                                }
                            }
                    }
                    return response;
                }
                break;
            case FAN_INFO:
                if (data.length > 1){
                    String mode = null;
                    switch (data[0]){
                        case 0:
                            mode = "авто режим выкл.";
                            break;
                        case 1:
                            mode = "авто режим";
                            break;
                    }
                    
                    String state = null;
                    switch (data[1]){
                        case 1:
                            state = "ожидание";
                            break;
                        case 2:
                            state = "требуется включение";
                            break;
                        case 3:
                            state = "включен (авто)";
                            break;
                        case 4:
                            state = "включен (ручной)";
                            break;
                    }
                    if (mode != null && state != null){
                        return String.format("Вентилятор (%s): %s", mode, state);
                    }
                }
            break;
            case SERVO_INFO:
                if (data.length == 2){
                    int angle = ((data[1] & 0xFF) << 8) | (data[0] & 0xFF);
                    return String.format("Угол поворота сервопривода: %d°", angle);
                }
            case VOLUME_INFO:
                if (data.length == 2){
                    return String.format("Значение уровня громкости: %d %% (%d dB)", data[0], data[1]);
                }
            break;
            case CHANNEL_INFO:
                if (data.length == 1){
                    return String.format("Текущий канал: %d", data[0]);
                }
            break;
            case EQUALIZER_INFO:
                if (data.length == 3){
                    return String.format("Эквалайзер: gain: %d dB; treble: %d dB; bass: %d dB; ", data[0], data[1], data[2]);
                }
            break;
            case FM_INFO:
                return decodeFMInfo(data);
        }
        return null;
    }
    
}
