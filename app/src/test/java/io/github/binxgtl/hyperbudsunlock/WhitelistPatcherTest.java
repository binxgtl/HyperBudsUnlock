package io.github.binxgtl.hyperbudsunlock;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class WhitelistPatcherTest {
    private List<String> known() {
        return new ArrayList<>(Arrays.asList("01011001", "01013A00", "01013B01"));
    }
    @Test public void insertsOnlyTarget() {
        List<String> x = known();
        assertTrue(WhitelistPatcher.addIfCompatible(x));
        assertEquals(4, x.size());
        assertTrue(x.contains("01013A03"));
    }
    @Test public void idempotent() {
        List<String> x = known();
        assertTrue(WhitelistPatcher.addIfCompatible(x));
        assertFalse(WhitelistPatcher.addIfCompatible(x));
        assertEquals(1, Collections.frequency(x, "01013A03"));
    }
    @Test public void rejectsUnknownShape() {
        List<String> x = new ArrayList<>(Arrays.asList("foo", "bar"));
        assertFalse(WhitelistPatcher.addIfCompatible(x));
        assertEquals(2, x.size());
    }
    @Test public void rejectsMissingSentinel() {
        List<String> x = new ArrayList<>(Arrays.asList("01011001"));
        assertFalse(WhitelistPatcher.addIfCompatible(x));
    }
    @Test public void rejectsNonLists() {
        assertFalse(WhitelistPatcher.addIfCompatible(null));
        assertFalse(WhitelistPatcher.addIfCompatible("01013A03"));
    }
}
