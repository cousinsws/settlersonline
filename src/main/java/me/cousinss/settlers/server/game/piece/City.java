package me.cousinss.settlers.server.game.piece;

import me.cousinss.settlers.server.game.player.Player;

public class City extends Colony {
    public City(Player owner) {
        super(owner, PieceType.CITY);
    }
}
