package com.gargon.smarthome;

import com.gargon.smarthome.model.enums.SmarthomeCommand;
import com.gargon.smarthome.model.enums.SmarthomeDevice;
import com.gargon.smarthome.protocol.SmarthomeConnection;
import com.gargon.smarthome.protocol.SmarthomeDataListener;
import com.gargon.smarthome.protocol.SmarthomeMessage;
import com.gargon.smarthome.protocol.SmarthomeMessageFilter;

import java.util.logging.Level;
import java.util.logging.Logger;


public class SmarthomeVirtualDevice {

    private int address;
    private String name;

    private SmarthomeConnection connection;

    private static final Logger LOG = Logger.getLogger(SmarthomeVirtualDevice.class.getName());

    public boolean send(SmarthomeDevice device, SmarthomeCommand command, byte[] data) {
        return connection.sendData(device, command, data);
    }

    public SmarthomeVirtualDevice(SmarthomeConnection connection, SmarthomeDevice device, SmarthomeDataListener dataListener) {
        this(connection, device.getAddress(), dataListener);
    }

    public SmarthomeVirtualDevice(SmarthomeConnection connection, final int deviceAddress, final SmarthomeDataListener dataListener) {
        try {
            if (!SmarthomeDevice.isVirtualDevice(deviceAddress)){
                throw new RuntimeException("Wrong address for virtual device: " + deviceAddress);
            }

            this.address = deviceAddress;

            SmarthomeDevice device = SmarthomeDevice.getByAddress(deviceAddress);
            this.name = device == null ? String.valueOf(deviceAddress) : device.name();

            this.connection = connection;
            this.connection.open();

            send(SmarthomeDevice.BROADCAST, SmarthomeCommand.BOOT_COMPLETED, null);
            LOG.log(Level.INFO, "VirtualDevice ID={0} created", deviceAddress);

            this.connection.bindListener(new SmarthomeDataListener() {

                @Override
                public void messageReceived(SmarthomeConnection connection, SmarthomeMessage m) {
                    switch (m.getCommand()) {
                        case DISCOVERY:
                            send(m.getSrc(), SmarthomeCommand.DISCOVERY_RESPONSE, name.getBytes());
                            break;
                        case PING:
                            send(m.getSrc(), SmarthomeCommand.PING_REPLY, m.getData());
                            break;
                        default:
                            if (dataListener != null) {
                                dataListener.messageReceived(connection, m);
                            }
                    }
                }

                @Override
                public SmarthomeMessageFilter getFilter() {
                    return new SmarthomeMessageFilter() {
                        @Override
                        public boolean filter(SmarthomeMessage message) {
                            return (deviceAddress == message.getDst().getAddress() || message.getDst() == SmarthomeDevice.BROADCAST);
                        }
                    };
                }

            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error while creating virtual device", e);

        }
    }

    public void close() {
        if (connection != null) {
            connection.close();
            LOG.log(Level.INFO, "VirtualDevice ID={0} closed", address);
        }
    }

}