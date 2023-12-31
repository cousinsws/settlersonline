package me.cousinss.settlers.server.game.card;

public enum Card {
    //Resource
    WOOD,
    BRICK,
    ORE,
    WHEAT,
    SHEEP,

    //Development
    KNIGHT,
    VICTORY_POINT,
    ROAD_BUILDING,
    YEAR_OF_PLENTY,
    MONOPOLY;

    public CardType getType() {
        return CardType.getType(this);
    }
}
