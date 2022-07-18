package com.gargon.smarthome;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SmarthomeDateTimeResolver implements SmarthomeDataListener {

    @Override
    public void messageReceived(SmarthomeConnection connection, SmarthomeMessage m) {
        if (m != null && m.getCommand() == SmarthomeCommand.TIME
                && (m.getDst() == SmarthomeDevice.SUPRADIN || m.getDst() == SmarthomeDevice.BROADCAST)) {

            GregorianCalendar c = new GregorianCalendar();
            int dw = c.get(Calendar.DAY_OF_WEEK) - 1;
            if (dw == 0) {
                dw = 7;
            }
            connection.sendData(m.getSrc(),
                    SmarthomeCommand.TIME_INFO,
                    new byte[]{
                            (byte) (c.get(Calendar.YEAR) - 2000),
                            (byte) (c.get(Calendar.MONTH) + 1),
                            (byte) c.get(Calendar.DAY_OF_MONTH),
                            (byte) c.get(Calendar.HOUR_OF_DAY),
                            (byte) c.get(Calendar.MINUTE),
                            (byte) c.get(Calendar.SECOND),
                            (byte) dw}
            );
        }
    }

}
