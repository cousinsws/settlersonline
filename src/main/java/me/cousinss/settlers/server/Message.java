package me.cousinss.settlers.server;

public class Message {
    public record GiveSession(String name, int id, boolean reconnected) {}
}
