package com.gargon.smarthome.model.enums;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class SmarthomeCommandTest {

    @Test
    public void testUniqueSmarthomeCommandCode() {
        Set<Integer> codes = new HashSet<>();
        for (SmarthomeCommand command : SmarthomeCommand.values()) {
            codes.add(command.getCode());
        }
        Assert.assertEquals("SmarthomeCommand contains non-unique codes",
                codes.size(), SmarthomeCommand.values().length);
    }

}