package me.cousinss.settlers.server;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private final Map<Integer, User> userMap;

    public Server() {
        this.userMap = new HashMap<>();
    }

    public boolean hasUser(int id) {
        return this.userMap.containsKey(id);
    }

    public User getUser(int id) {
        return this.userMap.get(id);
    }

    public boolean addUser(User user) {
        return null == this.userMap.putIfAbsent(user.getID(), user);
    }
}
