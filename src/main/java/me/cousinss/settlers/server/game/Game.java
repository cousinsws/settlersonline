package me.cousinss.settlers.server.game;

import me.cousinss.settlers.server.game.board.Board;
import me.cousinss.settlers.server.game.board.Coordinate;
import me.cousinss.settlers.server.game.board.Tile;
import me.cousinss.settlers.server.game.card.*;
import me.cousinss.settlers.server.game.die.Dice;
import me.cousinss.settlers.server.game.die.FairDie;
import me.cousinss.settlers.server.game.die.NormalDie;
import me.cousinss.settlers.server.game.player.Player;

import java.util.*;

public class Game {

    public static void main(String[] args) {
        Game g = new Game(new ArrayList<>(), new GameSettings());
    }

    private final GameSettings settings;
    private final Board board;
    private final List<Player> playerList;
    private final Map<Card, Deck> resourceDecks;
    private final Deck developmentDeck;
    private final Dice dice;

    public Game(List<Player> playerList, GameSettings settings) {
        this.settings = settings;
        this.board = initDefaultBoard(settings);
        this.playerList = initPlayers(playerList, settings);
        this.resourceDecks = initResourceDecks(settings);
        this.developmentDeck = initDevelopmentDeck(settings);
        this.dice = initDice(settings);
    }

    public Board getBoard() {
        return this.board;
    }

    private Board initDefaultBoard(GameSettings settings) {
        Board b = new Board();
        { //robber
            Coordinate c;
            while(Math.abs((c = new Coordinate((int)(Math.random()*4)-2, (int)(Math.random()*4)-2)).getS()) > 2) {} //generate random coordinate
            b.addTile(c, new Tile(c, Tile.TileType.DESERT));
            b.placeRobber(c); //robber starts on desert tile
        }
        //TODO use GameSettings' BOARD_SETUP
        Map<Card, Integer> resourceFrequencies = new HashMap<>();
        resourceFrequencies.put(Card.WOOD, 4);
        resourceFrequencies.put(Card.BRICK, 3);
        resourceFrequencies.put(Card.WHEAT, 4);
        resourceFrequencies.put(Card.SHEEP, 4);
        resourceFrequencies.put(Card.ORE, 3);
        Deck tileResources = new HeterogeneousDeck(resourceFrequencies);
        List<Integer> rollValues = new ArrayList<>(List.of(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
        for(int q = -2; q <= 2; q++) { //hex board with rad 2.5
            for(int r = -2; r <= 2; r++) {
                Coordinate c = new Coordinate(q, r);
                if(Math.abs(c.getS()) > 2 || b.hasTile(c)) { //outside bounds or already placed desert
                    continue;
                }
                Tile t = new Tile(c, tileResources.take(), rollValues.remove((int)(Math.random()*rollValues.size())));
                b.addTile(c, t);
            }
        }
        return b;
    }

    private List<Player> initPlayers(List<Player> players, GameSettings settings) {
        if(settings.getValue(GameSettings.Setting.PLAYER_ORDER) == GameSettings.RANDOM_PLAYER) {
            Collections.shuffle(players);
        }
        return players;
    }

    private Map<Card, Deck> initResourceDecks(GameSettings settings) {
        Map<Card, Deck> deckMap = new HashMap<>();
        for(Card resource : CardType.RESOURCE.getCardSet()) {
            deckMap.put(resource, new HomogeneousDeck(19, resource));
        }
        return deckMap;
    }

    private Deck initDevelopmentDeck(GameSettings settings) {
        Map<Card, Integer> deckMap = new HashMap<>();
        deckMap.put(Card.KNIGHT, 14);
        deckMap.put(Card.MONOPOLY, 2);
        deckMap.put(Card.ROAD_BUILDING, 2);
        deckMap.put(Card.YEAR_OF_PLENTY, 2);
        deckMap.put(Card.VICTORY_POINT, 5);
        return new HeterogeneousDeck(deckMap);
    }

    private Dice initDice(GameSettings settings) {
        return new Dice(
                settings.getValue(GameSettings.Setting.BALANCE_DICE) == GameSettings.BOOLEAN_ON ?
                        (NormalDie::new) : (FairDie::new),
                2, 6);
    }

}
