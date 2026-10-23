package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class AppLockTest {
    @Test
    public void samePinAndSaltGiveTheSameHash() {
        assertEquals(AppLock.hash("ab", "1234"), AppLock.hash("ab", "1234"));
        assertEquals(64, AppLock.hash("ab", "1234").length());
    }

    @Test
    public void anotherPinOrSaltChangesTheHash() {
        assertNotEquals(AppLock.hash("ab", "1234"), AppLock.hash("ab", "1235"));
        assertNotEquals(AppLock.hash("ab", "1234"), AppLock.hash("ac", "1234"));
    }
}
