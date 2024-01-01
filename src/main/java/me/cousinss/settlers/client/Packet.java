package me.cousinss.settlers.client;

public class Packet {
    public record Connect(String name) {}
    public record Reconnect(String name, int id) {}
}
