package me.cousinss.settlers.server;

import me.cousinss.settlers.server.game.player.Player;

public abstract class Message {
    public record JoinGameServer(JoinResult result, Player.Profile profile, String gameCode) {}
        public enum JoinResult {
            JOIN_LOBBY,
            BAD_CODE,
            RECONNECT_PLAYING
        }
    public record CreateGameServer(String gameCode) {}
}
