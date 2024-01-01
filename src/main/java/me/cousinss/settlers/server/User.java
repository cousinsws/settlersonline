package me.cousinss.settlers.server;

public class User {

    private static int freeID = 0;
    //TODO: add "is connected" so we don't have weird double-tab nonsense and so we can auto-reject trades when player is not connected
    //TODO ^ related: figure out how to run an event (to update User#isConnected) on a user disconnect event in Stomp

    private static int nextFreeID() {
        return freeID++;
    }

    private final int id;
    private final String name;
    public User(String name) {
        this.name = name;
        this.id = nextFreeID();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
