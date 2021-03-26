package dk.mmj.eevhe.gui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAction {

    @Test
    public void testResolveFromString() {
        assertEquals("Failed to resolve configure action", Action.CONFIGURE, Action.valueOf("configure".toUpperCase()));
        assertEquals("Failed to resolve vote action", Action.VOTE, Action.valueOf("vote".toUpperCase()));
        assertEquals("Failed to resolve fetch action", Action.FETCH, Action.valueOf("fetch".toUpperCase()));
    }
}