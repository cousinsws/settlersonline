package me.cousinss.settlers.server.game.piece;

import me.cousinss.settlers.server.game.player.Player;

public class Settlement extends Colony {
    public Settlement(Player owner) {
        super(owner, PieceType.SETTLEMENT);
    }
}
