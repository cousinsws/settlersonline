package me.cousinss.settlers.server.game.card;

import java.util.Arrays;
import java.util.EnumSet;

public enum CardType {
    RESOURCE(Card.WOOD, Card.BRICK, Card.ORE, Card.WHEAT, Card.SHEEP),
    DEVELOPMENT(EnumSet.complementOf(RESOURCE.getCardSet()));

    private final EnumSet<Card> cardSet;

    CardType(Card... cards) {
        EnumSet<Card> set = EnumSet.noneOf(Card.class);
        set.addAll(Arrays.stream(cards).toList());
        this.cardSet = set;
    }

    CardType(EnumSet<Card> set) {
        this.cardSet = set;
    }

    public EnumSet<Card> getCardSet() {
        return this.cardSet;
    }

    public static CardType getType(Card face) {
        for(CardType type : CardType.values()) {
            if(type.getCardSet().contains(face)) {
                return type;
            }
        }
        return null;
    }
}
