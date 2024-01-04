package me.cousinss.settlers.server;

import me.cousinss.settlers.server.game.board.Coordinate;
import me.cousinss.settlers.server.game.board.Tile;
import me.cousinss.settlers.server.game.player.Player;

import java.util.Map;

public abstract class Message {
    public record JoinGameServer(JoinResult result, Player.Profile profile, String gameCode) {}
        public enum JoinResult {
            JOIN_LOBBY,
            BAD_CODE,
            RECONNECT_PLAYING
        }
    public record CreateGameServer(String gameCode) {}
    public record GameStart(String gameCode, Map<Coordinate, Tile> tileMap, GameScenario scenario) {}
        public enum GameScenario {
            THREE_FOUR,
            FIVE_SIX
        }
}
