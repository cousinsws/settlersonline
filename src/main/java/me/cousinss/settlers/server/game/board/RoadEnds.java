package me.cousinss.settlers.server.game.board;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RoadEnds {
    private final Set<Vertex> ends;
    private final int hash;

    public RoadEnds(Vertex v1, Vertex v2) {
        this.ends = new HashSet<>();
        ends.add(v1);
        ends.add(v2);
        //min & max so that regardless of order hash is the same (match #equals)
        this.hash = 1907 * Math.max(v1.hashCode(), v2.hashCode()) - 3557 * Math.min(v1.hashCode(), v2.hashCode());
    }

    public Set<Vertex> getEnds() {
        return this.ends;
    }

    public boolean hasEnd(Vertex v) {
        return this.ends.contains(v);
    }

    public boolean sharesEnd(RoadEnds road) {
        Set<Vertex> shared = new HashSet<>(this.ends);
        shared.retainAll(road.getEnds());
        return !shared.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof RoadEnds road)) {
            return false;
        }
        return this.getEnds().stream().allMatch(road::hasEnd);
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        Iterator<Vertex> i = this.ends.iterator();
        return "{" + i.next() + " -> " + i.next() + "}";
    }
}
