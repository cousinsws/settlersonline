package me.cousinss.settlers.server;

import me.cousinss.settlers.server.game.board.*;
import me.cousinss.settlers.server.game.card.Card;
import me.cousinss.settlers.server.game.card.Deck;
import me.cousinss.settlers.server.game.piece.PieceType;
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
            Set<RoadEnds> roadSpaces,
            Map<Card, Deck> resourceDecks,
            Set<AnchorPort> ports
    ) {} //w/ implicit first-player build phase (no vertex blocking is needed; all are open on turn 1)
        public enum GameScenario {
            THREE_FOUR,
            FIVE_SIX
        }
        public record CoordinateTile(Coordinate coordinate, Tile tile) {}
        public record AnchorPort(Coordinate anchor, Port port) {}
    //Blocking vertices that are already blocked should have no effect.
    public record BlockVertices(Set<Vertex> blockVertices) {}
    //Opening roads that are already open should have no effect.
    public record OpenRoads(Set<RoadEnds> blockRoads) {}
    //Setting a colony where there already is one (settlement->city) should overwrite the original colony.
    public record SetColony(Vertex vertex, Player.Profile player, PieceType type) {}
}