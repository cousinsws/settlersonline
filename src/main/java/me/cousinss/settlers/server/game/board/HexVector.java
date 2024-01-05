package me.cousinss.settlers.server.game.board;

/**
 * Representing a vector in the hex-cube coordinate space, with bases q and r.
 */
public class HexVector extends Coordinate {
    public HexVector(int q, int r) {
        super(q, r);
    }

    public HexVector(Coordinate coordinate) {
        this(coordinate.q(), coordinate.r());
    }

    public HexVector turn() { //clockwise
        if(this.equals(Coordinate.ORIGIN)) {
            return new HexVector(Coordinate.ORIGIN); //0,0 turns to 0,0
        }
        if(this.q() != 0 && this.r() != 0) {
            return new HexVector(-this.r(), 0);
        }
        if(this.q() == 0) {
            return new HexVector(-this.r(), this.r());
        }
        //r=0
        return new HexVector(0, this.q());
    }
}
