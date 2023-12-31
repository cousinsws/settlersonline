package me.cousinss.settlers.server.game.board;

public record Coordinate(int q, int r) {

    public static final Coordinate ORIGIN = new Coordinate(0, 0);
    public static final Coordinate BASIS_NORTHEAST = new Coordinate(1, -1);
    public static final Coordinate BASIS_EAST = new Coordinate(1,  0);
    public static final Coordinate BASIS_SOUTHEAST = new Coordinate(0, 1);
    public static final Coordinate BASIS_SOUTHWEST = BASIS_NORTHEAST.reflectOrigin();
    public static final Coordinate BASIS_WEST = BASIS_EAST.reflectOrigin();
    public static final Coordinate BASIS_NORTHWEST = BASIS_SOUTHEAST.reflectOrigin();

    public Coordinate add(Coordinate c) {
        return new Coordinate(this.q + c.q, this.r + c.r);
    }

    public Coordinate reflectOrigin() {
        return new Coordinate(-this.q, -this.r);
    }

    /**
     * S = - Q - R.
     * @return the s coordinate
     */
    public int getS() {
        return -this.q - this.r;
    }

    @Override
    public String toString() {
        return "(" + this.q + ", " + this.r + ")";
    }
}
