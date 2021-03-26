package dk.mmj.eevhe.gui;

import dk.mmj.eevhe.gui.configurer.ConfigurerManager;
import dk.mmj.eevhe.gui.fetcher.FetcherManager;
import dk.mmj.eevhe.gui.voter.VoterManager;

import java.util.function.Supplier;

public enum Action {
    CONFIGURE("configure", ConfigurerManager::new),
    VOTE("vote", VoterManager::new),
    FETCH("fetch", FetcherManager::new);

    private final String name;
    private final Supplier<Manager> managerSupplier;

    Action(String name, Supplier<Manager> managerSupplier) {
        this.name = name;
        this.managerSupplier = managerSupplier;
    }

    public String getName() {
        return name;
    }

    public Manager produceManager() {
        return managerSupplier.get();
    }
}
