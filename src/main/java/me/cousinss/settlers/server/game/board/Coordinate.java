package me.cousinss.settlers.server.game.board;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

public class Coordinate {

    @JsonProperty
    private final int q;
    @JsonProperty
    private final int r;

    @JsonCreator
    public Coordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    @JsonGetter
    public int q() {
        return this.q;
    }

    @JsonGetter
    public int r() {
        return this.r;
    }

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
    @JsonIgnore
    public int getS() {
        return -this.q - this.r;
    }

    @JsonIgnore
    public Set<Vertex> getVerticesOn() {
        Set<Vertex> set = new HashSet<>();
        for(Direction d : Direction.values()) {
            set.add(Vertex.of(this, d));
        }
        return set;
    }

    @JsonIgnore
    public Set<Vertex> getSharedVertices(Coordinate coordinate) {
        Set<Vertex> s1 = this.getVerticesOn();
        Set<Vertex> s2 = coordinate.getVerticesOn();
        s1.retainAll(s2); //set intersection
        return s1;
    }

    @JsonIgnore
    public Set<Coordinate> getNeighbors() {
        Set<Coordinate> s = new HashSet<>();
        s.add(this.add(BASIS_NORTHEAST));
        s.add(this.add(BASIS_EAST));
        s.add(this.add(BASIS_SOUTHEAST));
        s.add(this.add(BASIS_SOUTHWEST));
        s.add(this.add(BASIS_WEST));
        s.add(this.add(BASIS_NORTHWEST));
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Coordinate c)) {
            return false;
        }
        return c.q() == this.q() && c.r() == this.r();
    }

    @Override
    public int hashCode() {
        return 6067 * this.q() + 2311 * this.r();
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "(" + this.q + ", " + this.r + ")";
    }
}
