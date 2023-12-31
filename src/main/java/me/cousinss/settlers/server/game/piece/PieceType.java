package me.cousinss.settlers.server.game.piece;

public enum PieceType {
    ROAD(0),
    SETTLEMENT(1),
    CITY(2);

    private final int resourceFactor;

    PieceType(int resourceFactor) {
        this.resourceFactor = resourceFactor;
    }

    public int getResourceFactor() {
        return this.resourceFactor;
    }

    public boolean isBuilding() {
        return this.resourceFactor > 0;
    }
}
