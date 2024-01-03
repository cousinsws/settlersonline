package me.cousinss.settlers.client;

import me.cousinss.settlers.server.game.player.Player;

public class Packet {
    public record Connect(Player.Profile profile, String gameCode) {} //put pastSessionID in Profile.ID if have
    public record Reconnect(String name, int id) {}
    public enum BotAddType {
        ADD,
        REMOVE
    }
}
