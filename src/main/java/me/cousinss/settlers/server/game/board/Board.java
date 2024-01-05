package me.cousinss.settlers.server.game.board;

import me.cousinss.settlers.server.game.piece.Colony;
import me.cousinss.settlers.server.game.piece.Settlement;
import me.cousinss.settlers.server.game.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

//The amount of this class devoted to longest road is INSANE
/**
 * Represents the state of the board, containing its tiles, placed pieces, ports, and the robber.
 * The Board object does not initialize nor regulate its own state according to the rules of the game;
 * for example, the Board object will allow two {@link Settlement}s to be placed next to each other.
 */
public class Board {
    private final Map<Coordinate, Tile> tileMap;
    private final Map<Vertex, Colony> colonyMap;
    private final Map<RoadEnds, Player> roadMap;
    private Coordinate robber;
    private final Map<Coordinate, Port> ports;

    public Board() {
        this.tileMap =  new HashMap<>();
        this.colonyMap = new HashMap<>();
        this.roadMap = new HashMap<>();
        this.robber = null; //error case - robber should be placed on the desert starting tile
        this.ports = new HashMap<>();
    }

    public Map<Coordinate, Tile> getTileMap() {
        return this.tileMap;
    }

    public Tile getTile(Coordinate c) {
        return this.tileMap.get(c);
    }

    /**
     * Returns the {@link Colony} at this {@link Vertex}, or null if there is not one present.
     * @param v the vertex to query
     * @return the colony, or {@code null} if not found
     */
    public Colony getColony(Vertex v) {
        return this.colonyMap.get(v);
    }

    /**
     * Gets a list of {@link Vertex} that can be reached by a single step along existing Road pieces in the specified set ending at the specified vertex.
     * @param v the vertex
     * @param roads the roads to search from
     * @return the road-adjacent vertices, or, if none are present, an empty list.
     */
    private static Set<Vertex> getRoadDestinations(Vertex v, Set<RoadEnds> roads) {
        return v.getAdjacentVertices().stream().filter(to -> roads.contains(new RoadEnds(v, to))).collect(Collectors.toSet());
    }

    private static Set<RoadEnds> getRoadsFrom(Vertex v, Set<RoadEnds> allRoads) {
        return v.getAdjacentVertices().stream().map(to -> new RoadEnds(v, to)).filter(allRoads::contains).collect(Collectors.toSet());
    }

    /**
     * Adds the tile to the board if the tile coordinate is empty.
     * @param c the coordinate
     * @param t the tile
     * @return {@code true} if the tile was added and {@code false} otherwise; functionally, returns whether the board changed.
     */
    public boolean addTile(Coordinate c, Tile t) {
        if(this.tileMap.containsKey(c)) {
            return false;
        }
        this.tileMap.put(c, t);
        return true;
    }

    public boolean addColony(Vertex v, Colony c) {
        return null == this.colonyMap.putIfAbsent(v, c);
    }

    public boolean addRoad(Vertex v1, Vertex v2, Player owner) {
        return null == this.roadMap.putIfAbsent(new RoadEnds(v1, v2), owner);
    }

    /**
     * Places the Robber on a present tile on the board.
     * @param c the coordinate of the tile
     * @return {@code true} if the tile exists and the robber was placed, {@code false} otherwise (the board is unchanged).
     */
    public boolean placeRobber(Coordinate c) {
        if(!this.tileMap.containsKey(c)) {
            return false;
        }
        this.robber = c;
        return true;
    }

    /**
     * Adds the port.
     * @param p the port
     * @return {@code true}
     */
    public boolean addPort(Coordinate anchor, Port p) {
        this.ports.put(anchor, p);
        return true;
    }

    public Map<Coordinate, Port> getPorts() {
        return this.ports;
    }

    public List<Port> getOwnedPorts(Player player) {
        return this.getPorts().values().stream()
                .filter(
                    port -> (port.getVertices().stream()
                                .map(this::getColony)
                                .anyMatch(c -> c != null && c.getOwner().equals(player))))
                .toList();
    }

    public boolean hasTile(Coordinate c) {
        return this.tileMap.containsKey(c);
    }

    /**
     * Returns all the coordinate-tile pairs on the board with tiles of a specified roll value.
     * Note that this method returns all such tiles, even that which contains the robber if appropriate.
     * @param value the value
     * @return a list of {@link Coordinate}{@code , }{@link Tile} entry pairs
     */
    public List<Map.Entry<Coordinate, Tile>> getWithRoll(int value) {
        return this.tileMap.entrySet().stream().filter(e -> e.getValue().getRollValue()==value).toList();
    }

    public boolean hasRobber(Coordinate c) {
        return this.robber.equals(c);
    }

    public Map<Vertex, Colony> getAdjacentColonies(Coordinate c) {
        Map<Vertex, Colony> map = new HashMap<>();
        for(Direction dir : Direction.values()) {
            Vertex vert = Vertex.of(c, dir);
            Colony colony = this.colonyMap.get(vert);
            if(colony != null) {
                map.put(vert, colony);
            }
        }
        return map;
    }

    /**
     * The longest contiguous path of road pieces on the board owned by the specified {@link Player}.
     * @param player the player
     * @return the road path, as a sequential list of {@link RoadEnds}.
     */
    public List<RoadEnds> getLongestRoad(Player player) {
        Set<RoadEnds> allRoads = getAllRoads(player);
        List<RoadEnds> longest = new ArrayList<>();
        Set<Set<RoadEnds>> roadComponents = separateComponents(allRoads, player);
        for(Set<RoadEnds> component : roadComponents) {
            Set<Vertex> heads = getRoadHeads(component);
            if(heads.isEmpty()) {
                //no heads? must be a loop! tricky...
                List<RoadEnds> loopPath = endsPath(component);
                if(loopPath.size() > longest.size()) {
                    longest = loopPath;
                }
            }
            for (Vertex headEnd : heads) {
                List<RoadEnds> path = getLongestRoad(component, headEnd, new ArrayList<>());
                if (path.size() > longest.size()) {
                    longest = path;
                }
            }
        }
        return longest;
    }

    private Set<Set<RoadEnds>> separateComponents(Set<RoadEnds> allRoads, Player player) {
        Set<Set<RoadEnds>> out = new HashSet<>();
        if(allRoads.isEmpty()) {
            return out;
        }
        //"visited" is the *global* visited set, not the individual one for a given flood - that's created inline below
        Set<RoadEnds> visited = new HashSet<>();
        Set<RoadEnds> left;
        while(!(left = setDiff(allRoads, visited)).isEmpty()) {
            Set<RoadEnds> component = floodToOpponentColonies(allRoads, player, new HashSet<>(), left.iterator().next());
            out.add(component);
            visited.addAll(component);
        }
        return out;
    }

    /**
     * Flood from the starting road to all connected friendly roads, stopping at unfriendly {@link Colony} pieces on the board.
     * @param allRoads all the roads owned by the player
     * @param player the player
     * @param visited the visited roads
     * @param start the starting road
     * @return the connected roads
     */
    private Set<RoadEnds> floodToOpponentColonies(Set<RoadEnds> allRoads, Player player, Set<RoadEnds> visited, RoadEnds start) {
        visited.add(start);
        start.getEnds().forEach(end -> {
            Colony c = getColony(end);
            if(c != null && !c.getOwner().equals(player)) {
                return;
            }
            Set<RoadEnds> nextRoads = getRoadsFrom(end, allRoads);
            nextRoads.stream().filter(road -> !visited.contains(road)).forEach(road -> floodToOpponentColonies(allRoads, player, visited, road));
        });
        return visited;
    }

    private static Set<RoadEnds> setDiff(Set<RoadEnds> large, Set<RoadEnds> small) {
        Set<RoadEnds> diff = new HashSet<>(large);
        diff.removeAll(small);
        return diff;
    }

    @NotNull
    private Set<RoadEnds> getAllRoads(Player player) {
        return roadMap.entrySet().stream()
                .filter(e -> e.getValue().equals(player))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private static Set<Vertex> getRoadHeads(Set<RoadEnds> allRoads) {
        return allRoads.stream()
                .flatMap(
                        ends -> ends.getEnds().stream()
                                .filter(
                                        end -> getRoadDestinations(end, allRoads).size() == 1
                                )
                ).collect(Collectors.toSet());
    }

    private List<RoadEnds> getLongestRoad(Set<RoadEnds> allRoads, Vertex start, List<RoadEnds> currentPath) {
//        System.out.println("Current path: " + Arrays.toString(vertexPath(currentPath).toArray()));
//        System.out.println("Starting new LR search at: " + start);
        RoadEnds lastMove = currentPath.size() == 0 ? null : currentPath.get(currentPath.size() - 1);
        Set<Vertex> travelTo = getRoadDestinations(start, allRoads);
        if(lastMove != null) {
            //remove previous move from possible next moves list (we want to traverse forward)
            travelTo = travelTo.stream().filter(to -> !(new RoadEnds(start, to)).equals(lastMove)).collect(Collectors.toSet());
        }
        int numBranches = travelTo.size();
        if(numBranches == 0) { //reached the opposite head
//            System.out.println("Path complete.");
            return currentPath;
        }
//        System.out.println("Branching to new path(s) from branch junction \"" + start + "\" with " + numBranches + " forward branches.");
        return travelTo.stream().map(
                to -> {
//                    System.out.println("Starting new path from junction at branch head: " + to);
                    List<RoadEnds> newPath = new ArrayList<>(currentPath);
                    newPath.add(new RoadEnds(start, to));
                    return getLongestRoad(allRoads, to, newPath);
                }
        ).max(Comparator.comparingInt(List::size)).get(); //nice little java api simplification here
    }

    private static List<Vertex> vertexPath(List<RoadEnds> endsPath) {
        if(endsPath.isEmpty()) {
            return new ArrayList<>();
        }
        List<Vertex> list = new ArrayList<>();
        list.add(endsPath.iterator().next().getEnds().iterator().next()); //one more vertex than road - that's the starting (or ending) one
        for(RoadEnds ends : endsPath) {
            list.addAll(ends.getEnds().stream().filter(v -> !list.contains(v)).toList());
        }
        if(getRoadHeads(new HashSet<>(endsPath)).isEmpty()) {
            list.add(list.get(0));
        }
        return list;
    }

    /**
     * Get all the land vertices on the board. Expensive call - should only call once.
     * @return the set of all vertices adjacent to at least one tile in {@link #getTileMap}.
     */
    public Set<Vertex> getLandVertices() {
        Set<Vertex> set = new HashSet<>();
        for(Coordinate c : this.getTileMap().keySet()) {
            set.addAll(c.getVerticesOn());
        }
        return set;
    }

    /**
     * Arranges the connected set of roads into an ordered path.
     * (For use in ordering cycles)
     * @param ends the connected roads
     * @return the set as a list
     */
    private static List<RoadEnds> endsPath(Set<RoadEnds> ends) {
        List<RoadEnds> path = new ArrayList<>();
        if(ends.isEmpty()) {
            return path;
        }
        List<List<RoadEnds>> lists = ends.stream().map(road -> (List<RoadEnds>) (new ArrayList<>(List.of(road)))).toList();
        while(lists.size() > 1) {
            for(List<RoadEnds> l : lists) {
                System.out.println(Arrays.toString(vertexPath(l).toArray()));
            }
            List<RoadEnds> prim = lists.get(0);
            RoadEnds tail = prim.get(prim.size() - 1);
            List<RoadEnds> sec = lists.stream().filter(list -> !list.equals(prim) && list.get(0).sharesEnd(tail)).findFirst().get();
            lists.get(0).addAll(sec);
            lists = remove(lists, sec);
        }
        return lists.get(0);
    }

    private static List<List<RoadEnds>> remove(List<List<RoadEnds>> list, List<RoadEnds> roads) {
        List<List<RoadEnds>> newList = new ArrayList<>();
        for(List<RoadEnds> j : list) {
            if (!j.equals(roads)) {
                newList.add(j);
            }
        }
        return newList;
    }

    private boolean isRoad(Vertex v1, Vertex v2) {
        return this.roadMap.containsKey(new RoadEnds(v1, v2));
    }

    /**
     * If there is a road between the specified vertices and owned by the specified {@link Player}.
     * @param v1 one vertex
     * @param v2 the other vertex (order irrelevant)
     * @param owner the owner
     * @return {@code true} if the specified player owns a road spanning the two vertices directly, {@code false} otherwise.
     */
    private boolean isRoad(Vertex v1, Vertex v2, Player owner) {
        return owner.equals(this.roadMap.get(new RoadEnds(v1, v2)));
    }

    /**
     * Whether the vertex is on an edge of a land tile on the board.
     * @param v the vertex
     * @return {@code true} if there is at least one tile on the board with the vertex on its edge, or {@code false} otherwise.
     */
    public boolean isLand(Vertex v) {
        return v.getTouchingTiles().stream().anyMatch(this.tileMap::containsKey);
    }

    /**
     * An ordered list of tile Coordinates representing the cycle of tiles immediately adjacent to the edge of the island. Begins at an arbitrary point.
     * @return the harbor ring cycle, beginning from East of centre.
     */
    public List<Coordinate> getHarborRing() {
        int q = -1;
        while(this.hasTile(new Coordinate(++q, 0))) {}
        List<Coordinate> ring = new ArrayList<>();
        Coordinate c = new Coordinate(q, 0); //east of centre start
        HexVector v = new HexVector(-1, 1); //around the loop we go (clockwise)!
        Coordinate start = c.add(Coordinate.ORIGIN); //copy (save)
        ring.add(start);
        c = c.add(v);
        while(!c.equals(start)) {
            ring.add(c);
            if(!this.hasTile(c.add(v.turn()))) {
                v = v.turn();
            }
            c = c.add(v);
        }
        return ring;
    }

    //Visual tests
    public static void main(String[] args) {
//        List<RoadEnds> path = new ArrayList<>();
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.NORTH), Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTH)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.SOUTH), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHWEST)));
//        System.out.println(Arrays.toString(vertexPath(path).toArray()));
//        Board board = new Board();
//        Player p1 = new Player(Player.PlayerType.HUMAN, "NC", 0);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN, Direction.NORTH), Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST), p1);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), p1);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTH), p1);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTH), p1);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN, Direction.SOUTH), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHWEST), p1);
//        board.addRoad(Vertex.of(Coordinate.ORIGIN.add(Coordinate.BASIS_EAST), Direction.SOUTH), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), p1);
        //component 2
//        board.addRoad(Vertex.of(new Coordinate(0, -2), Direction.NORTH), Vertex.of(new Coordinate(0, -2), Direction.NORTHEAST), p1);
//        board.addRoad(Vertex.of(new Coordinate(0, -2), Direction.NORTHEAST), Vertex.of(new Coordinate(0, -2), Direction.SOUTHEAST), p1);
//        System.out.println(Arrays.toString(vertexPath(board.getLongestRoad(p1)).toArray()));
//        System.out.println(Arrays.toString(board.getAllRoads(p1).toArray()));
//        System.out.println(getRoadHeads(board.getAllRoads(p1)));
        //enemy colony
//        Player p2 = new Player(Player.PlayerType.ROBOT, "Candamir", 1);
//        board.addColony(Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST), new Settlement(p2));
//        System.out.println(Arrays.toString(vertexPath(board.getLongestRoad(p1)).toArray()));
//        System.out.println(Arrays.toString(board.getAllRoads(p1).toArray()));
//        System.out.println(getRoadHeads(board.getAllRoads(p1)));
        //Testing loops
//        Set<RoadEnds> path = new LinkedHashSet<>();
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.NORTH), Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.NORTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.SOUTHEAST), Vertex.of(Coordinate.ORIGIN, Direction.SOUTH)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.SOUTH), Vertex.of(Coordinate.ORIGIN, Direction.SOUTHWEST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.SOUTHWEST), Vertex.of(Coordinate.ORIGIN, Direction.NORTHWEST)));
//        path.add(new RoadEnds(Vertex.of(Coordinate.ORIGIN, Direction.NORTHWEST), Vertex.of(Coordinate.ORIGIN, Direction.NORTH)));
//        System.out.println(Arrays.toString(vertexPath(endsPath(path)).toArray()));
    }
}
