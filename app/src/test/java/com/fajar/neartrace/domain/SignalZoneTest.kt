package com.fajar.neartrace.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalZoneTest {
    @Test fun boundariesFollowBrief() {
        assertEquals(SignalZone.VERY_CLOSE, SignalZone.from(-30))
        assertEquals(SignalZone.CLOSE, SignalZone.from(-31))
        assertEquals(SignalZone.CLOSE, SignalZone.from(-50))
        assertEquals(SignalZone.GOOD, SignalZone.from(-51))
        assertEquals(SignalZone.GOOD, SignalZone.from(-70))
        assertEquals(SignalZone.WEAK, SignalZone.from(-71))
        assertEquals(SignalZone.WEAK, SignalZone.from(-80))
        assertEquals(SignalZone.VERY_WEAK, SignalZone.from(-81))
        assertEquals(SignalZone.VERY_WEAK, SignalZone.from(-90))
        assertEquals(SignalZone.LOST, SignalZone.from(-91))
    }
    @Test fun distanceGetsLargerAsSignalWeakens() {
        assertTrue(BleDevice("B", null, -80).distanceMeters > BleDevice("A", null, -40).distanceMeters)
    }
    @Test fun unknownNameHasReadableFallback() {
        assertEquals("Perangkat BLE tanpa nama", BleDevice("A", null, -40).displayName)
        assertEquals("Perangkat BLE tanpa nama", BleDevice("A", "   ", -40).displayName)
    }
    @Test fun stableIdentityPreservesProvidedName() {
        assertEquals("Watch", BleDevice("AA:BB", "Watch", -40).displayName)
    }
}
