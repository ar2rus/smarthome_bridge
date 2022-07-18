package com.gargon.smarthome.model.enums;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SmarthomeDeviceTest {

    @Test
    public void testUniqueSmarthomeDeviceAddresses() {
        Set<Integer> addresses = new HashSet<>();
        for (SmarthomeDevice device : SmarthomeDevice.values()) {
            addresses.add(device.getAddress());
        }
        Assert.assertEquals("SmarthomeDevice contains non-unique addresses",
                addresses.size(), SmarthomeDevice.values().length);
    }

}