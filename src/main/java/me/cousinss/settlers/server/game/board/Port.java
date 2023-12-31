package me.cousinss.settlers.server.game.board;

import me.cousinss.settlers.server.game.card.Card;

import java.util.LinkedHashSet;
import java.util.Set;

public class Port {

    /**
     * The port's special resource, or {@code null} if this is a "Wildcard" port.
     */
    private final Card resource;
    private final Set<Vertex> vertexSet;

    public static Port ofResource(Card resource, Vertex v1, Vertex v2) {
        if(resource == null) {
            throw new IllegalArgumentException("Resource port cannot have null resource.");
        }
        return new Port(resource, v1, v2);
    }

    public static Port wildcard(Vertex v1, Vertex v2) {
        return new Port(null, v1, v2);
    }

    private Port(Card resource, Vertex v1, Vertex v2) {
        this.resource = resource;
        this.vertexSet = new LinkedHashSet<>();
        this.vertexSet.add(v1);
        this.vertexSet.add(v2);
    }

    /**
     * Returns the port's special resource, or {@code null} for a "Wildcard" port.
     * @return the resource
     */
    public Card getResource() {
        return this.resource;
    }

    public Set<Vertex> getVertices() {
        return this.vertexSet;
    }
}
