package me.cousinss.settlers.server.game.board;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Vertex {
    //this class is so full of Java 13+ features its incredible

    @JsonIgnore
    private static final Map<VPair, Vertex> vertexMap = new HashMap<>();

    /**
     * Canonized vertex parameter tuple
     * @param coord the coordinate
     * @param dir the direction
     */
    private record VPair(Coordinate coord, Direction dir) {}

    private final Coordinate tileCoordinate;
    private final Direction direction;
    @JsonIgnore
    private final Set<Coordinate> adjacentTiles;
    @JsonIgnore
    private final Set<VPair> adjacentVPairs;
    @JsonIgnore
    private Set<Vertex> adjacentVertices = null;

    public static Vertex of(Coordinate tileCoordinate, Direction direction) {
//        System.out.println("Attempting to get Vertex with Coordinate " + tileCoordinate + ", Direction " + direction.name());
        VPair vPair = new VPair(tileCoordinate, direction);
//        System.out.println(vertexMap.containsKey(vPair) ? "VMap contains key." : "VMap does not contain key, generating...");
        vertexMap.putIfAbsent(
                vPair,
                switch (direction) {
                    //The NORTH and NORTHWEST vertices are "owned" by the central tile.
                    case NORTH, NORTHWEST -> new Vertex(tileCoordinate, direction);
                    case NORTHEAST -> new Vertex(tileCoordinate.add(Coordinate.BASIS_EAST), Direction.NORTHWEST);
                    case SOUTHEAST -> new Vertex(tileCoordinate.add(Coordinate.BASIS_SOUTHEAST), Direction.NORTH);
                    case SOUTH -> new Vertex(tileCoordinate.add(Coordinate.BASIS_SOUTHEAST), Direction.NORTHWEST);
                    case SOUTHWEST -> new Vertex(tileCoordinate.add(Coordinate.BASIS_SOUTHWEST), Direction.NORTH);
                }
        );
        return vertexMap.get(vPair);
    }

    private Vertex(Coordinate tileCoordinate, Direction direction) {
//        System.out.println("Creating canon Vertex with Coordinate " + tileCoordinate + ", Direction " + direction.name());
        this.tileCoordinate = tileCoordinate;
        this.direction = direction;
        this.adjacentTiles = new HashSet<>();
        adjacentTiles.add(this.tileCoordinate);
        adjacentTiles.add(this.tileCoordinate.add(Coordinate.BASIS_NORTHWEST));
        adjacentTiles.add(this.tileCoordinate.add(
                this.direction.equals(Direction.NORTH) ? Coordinate.BASIS_NORTHEAST : Coordinate.BASIS_WEST)
        );
        this.adjacentVPairs = new HashSet<>();
        if(direction.equals(Direction.NORTH)) {
            adjacentVPairs.add(new VPair(tileCoordinate, Direction.NORTHWEST));
            adjacentVPairs.add(new VPair(tileCoordinate.add(Coordinate.BASIS_NORTHEAST), Direction.NORTHWEST));
            adjacentVPairs.add(new VPair(tileCoordinate.add(Coordinate.BASIS_EAST), Direction.NORTHWEST));
        } else { //NW
            adjacentVPairs.add(new VPair(tileCoordinate, Direction.NORTH));
            adjacentVPairs.add(new VPair(tileCoordinate.add(Coordinate.BASIS_NORTHWEST), Direction.SOUTHWEST));
            adjacentVPairs.add(new VPair(tileCoordinate, Direction.SOUTHWEST));
        }
    }

    /**
     * Whether the vertex lies on the edge of the tile at the specified coordinate.
     * @param c the coordinate
     * @return {@code true} if the vertex touches the coordinate tile, {@code false} otherwise.
     */
    public boolean touches(Coordinate c) {
        return this.adjacentTiles.contains(c);
    }

    @JsonIgnore
    public Set<Coordinate> getTouchingTiles() {
        return new HashSet<>(this.adjacentTiles);
    }

    public boolean isAdjacent(Vertex v) {
        return this.getAdjacentVertices().contains(v);
    }

    public Set<Vertex> getAdjacentVertices() {
        if(this.adjacentVertices == null) {
            this.adjacentVertices = this.adjacentVPairs.stream().map(vPair -> Vertex.of(vPair.coord, vPair.dir)).collect(Collectors.toSet());
        }
        return this.adjacentVertices;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Vertex v)) {
            return false;
        }
        return this.direction == v.direction && this.tileCoordinate.equals(v.tileCoordinate);
    }

    public Coordinate getTileCoordinate() {
        return this.tileCoordinate;
    }

    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public int hashCode() {
        return this.tileCoordinate.hashCode() + this.direction.hashCode();
    }

    @Override
    public String toString() {
        return "V@" + this.tileCoordinate + ":" + (this.direction == Direction.NORTH ? "TOP" : "LEFT");
    }
}
