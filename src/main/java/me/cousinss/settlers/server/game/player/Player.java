package me.cousinss.settlers.server.game.player;

import me.cousinss.settlers.server.game.card.Card;
import me.cousinss.settlers.server.game.card.CardType;
import me.cousinss.settlers.server.game.card.Hand;
import me.cousinss.settlers.server.game.piece.PieceType;

import java.util.EnumSet;

public class Player {

    private final Profile profile;
    private final Hand<Card> hand;
    private final Hand<Card> cardsPlayed;
    private final Hand<PieceType> pieces;
    private final EnumSet<VictoryBonus> victoryBonuses;
    private int colonialScore;

    /**
     * Construct a new Player with no cards or structures.
     * @param profile the player profile
     */
    public Player(Profile profile) {
        this.profile = profile;
        this.hand = new Hand<>(Card.class);
        this.cardsPlayed = new Hand<>(Card.class);
        this.pieces = new Hand<>(PieceType.class);
        this.victoryBonuses = EnumSet.noneOf(VictoryBonus.class);
        this.colonialScore = 0;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public Hand<Card> getHand() {
        return this.hand;
    }

    /**
     * Count the number of cards of a certain type (i.e. all development cards) in the Player's hand.
     * @param type the card type
     * @return the number of cards of the specified type
     */
    public int countCardsInHand(CardType type) {
        return type.getCardSet().stream().map(c -> this.getHand().count(c)).reduce(Integer::sum).get();
    }

    public Hand<Card> getCardsPlayed() {
        return this.cardsPlayed;
    }

    /**
     * Counts the number of Knight cards the Player has played.
     * @return the count
     */
    public int countKnightsPlayed() {
        return this.cardsPlayed.count(Card.KNIGHT);
    }

    public Hand<PieceType> getStructures() {
        return this.pieces;
    }

    public EnumSet<VictoryBonus> getVictoryBonuses() {
        return this.victoryBonuses;
    }

    public void incrementScore() {
        this.colonialScore++;
    }

    public void setColonialScore(int colonialScore) {
        this.colonialScore = colonialScore;
    }

    public int getColonialScore() {
        return this.colonialScore;
    }

    public int getPublicScore() {
        return this.getColonialScore() + this.victoryBonuses.size()*2;
    }

    public int getTotalScore() {
        return this.getPublicScore() + this.cardsPlayed.count(Card.VICTORY_POINT);
    }

    public record Profile(String ID, PlayerType type, String name, Color color, boolean isLeader) {
        public Profile setID(String newID) {
            return new Profile(newID, type, name, color, isLeader);
        }
        public Profile setLeader(boolean b) {
            return new Profile(ID, type, name, color, b);
        }
    }

    public enum Color {
        RED("red"),
        BLUE("blue"),
        WHITE("white"),
        ORANGE("orange"),
        YELLOW("yellow"),
        GREEN("green"),
        PURPLE("purple"),
        BLACK("black");

        public final String colorStr;
        Color(String colorStr) {
            this.colorStr = colorStr;
        }
    }

    public enum PlayerType {
        HUMAN,
        ROBOT
    }
}