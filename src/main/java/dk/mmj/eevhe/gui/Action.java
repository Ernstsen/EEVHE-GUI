package dk.mmj.eevhe.gui;

public enum Action {
    CONFIGURE("configure"), VOTE("vote"), FETCH("fetch");

    private final String name;

    Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
