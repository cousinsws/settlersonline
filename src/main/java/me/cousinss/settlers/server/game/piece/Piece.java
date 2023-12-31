package me.cousinss.settlers.server.game.piece;

import me.cousinss.settlers.server.game.player.Player;

public abstract class Piece {

    private final Player owner;
    private final PieceType type;

    public Piece(Player owner, PieceType type) {
        this.owner = owner;
        this.type = type;
    }

    public Player getOwner() {
        return this.owner;
    }

    public boolean belongsTo(Player player) {
        return this.getOwner().equals(player);
    }

    public PieceType getType() {
        return this.type;
    }

    public int getResourceFactor() {
        return this.getType().getResourceFactor();
    }
}
