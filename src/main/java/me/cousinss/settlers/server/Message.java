package me.cousinss.settlers.server;

import me.cousinss.settlers.server.game.board.Coordinate;
import me.cousinss.settlers.server.game.board.Port;
import me.cousinss.settlers.server.game.board.Tile;
import me.cousinss.settlers.server.game.board.Vertex;
import me.cousinss.settlers.server.game.card.Card;
import me.cousinss.settlers.server.game.card.Deck;
import me.cousinss.settlers.server.game.player.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Message {
    public record JoinGameServer(JoinResult result, Player.Profile profile, String gameCode) {}
        public enum JoinResult {
            JOIN_LOBBY,
            BAD_CODE,
            RECONNECT_PLAYING
        }
    public record CreateGameServer(String gameCode) {}
    public record GameStart(
            String gameCode,
            Set<CoordinateTile> tileMap,
            GameScenario scenario,
            List<Player.Profile> playerOrder, //in order of play
            Set<Vertex> landVertices,
            Map<Card, Deck> resourceDecks,
            Set<AnchorPort> ports
    ) {}
        public enum GameScenario {
            THREE_FOUR,
            FIVE_SIX
        }
        public record CoordinateTile(Coordinate coordinate, Tile tile) {}
        public record AnchorPort(Coordinate anchor, Port port) {}
}
