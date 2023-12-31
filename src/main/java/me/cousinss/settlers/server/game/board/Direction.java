package me.cousinss.settlers.server.game.board;

/**
 * Represents a direction from the center of a {@link Tile} to one of its vertices.
 * Directions to edges are not represented, as edges are simply connections between vertices,
 * and there is no functional need for direction from one tile to another.
 */
public enum Direction {
    NORTH(0),
    NORTHWEST(1),
    SOUTHWEST(2),
    SOUTH(3),
    SOUTHEAST(4),
    NORTHEAST(5);

    private final int id;

    Direction(int id) {
        this.id = id;
    }

    public static int distanceAroundHex(Direction d1, Direction d2) {
        int x = Math.abs(d1.id - d2.id);
        if(x > 3) {
            return 6-x;
        }
        return x;
    }
}
