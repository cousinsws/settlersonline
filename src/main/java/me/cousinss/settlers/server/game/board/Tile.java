package me.cousinss.settlers.server.game.board;

import me.cousinss.settlers.server.game.card.Card;

public class Tile {

    public static final int NO_ROLL_VALUE = -1;
    public static final Card NO_RESOURCE = null;

    private final Coordinate coordinate;
    private final TileType type;
    private int rollValue;
    private Card resource;

    public Tile(Coordinate coordinate, TileType type) {
        this.coordinate = coordinate;
        this.type = type;
        this.rollValue = NO_ROLL_VALUE;
        this.resource = NO_RESOURCE;
    }

    public Tile(Coordinate coordinate, Card resource, int rollValue) {
        this.coordinate = coordinate;
        this.type = TileType.RESOURCE;
        this.rollValue = rollValue;
        this.resource = resource;
    }

    protected Coordinate getCoordinate() {
        return this.coordinate;
    }

    public int q() {
        return this.coordinate.q();
    }

    public int r() {
        return this.coordinate.r();
    }

    public TileType getType() {
        return this.type;
    }

    public int getRollValue() {
        return this.rollValue;
    }

    public Card getResource() {
        return this.resource;
    }

    public boolean setRollValue(int rollValue) {
        if(this.getType() == TileType.DESERT) {
            return false;
        }
        this.rollValue = rollValue;
        return true;
    }

    public boolean setResource(Card resource) {
        if(this.getType() == TileType.DESERT) {
            return false;
        }
        this.resource = resource;
        return true;
    }

    public enum TileType {
        RESOURCE,
        DESERT
    }

    @Override
    public String toString() {
        return "T:" + this.type.name() + "@" + this.coordinate + (this.type.equals(TileType.DESERT) ? "" : " " + this.resource.name() + ":" + this.rollValue);
    }
}