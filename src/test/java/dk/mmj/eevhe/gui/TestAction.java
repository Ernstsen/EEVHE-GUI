package dk.mmj.eevhe.gui;

import dk.mmj.eevhe.gui.configurer.ConfigurerManager;
import dk.mmj.eevhe.gui.fetcher.FetcherManager;
import dk.mmj.eevhe.gui.voter.VoterManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestAction {

    @Test
    public void testResolveFromString() {
        assertEquals("Failed to resolve configure action", Action.CONFIGURE, Action.valueOf("configure".toUpperCase()));
        assertEquals("Failed to resolve vote action", Action.VOTE, Action.valueOf("vote".toUpperCase()));
        assertEquals("Failed to resolve fetch action", Action.FETCH, Action.valueOf("fetch".toUpperCase()));

        assertEquals("Wrong manager created", ConfigurerManager.class, Action.CONFIGURE.produceManager().getClass());
        assertEquals("Wrong manager created", VoterManager.class, Action.VOTE.produceManager().getClass());
        assertEquals("Wrong manager created", FetcherManager.class, Action.FETCH.produceManager().getClass());
    }
}