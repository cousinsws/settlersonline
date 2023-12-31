package me.cousinss.settlers.server.game.card;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Represents a collection of resources, for purposes of trading or structure build recipes.
 */
public class ResourceSet {

    private Map<Card, Integer> set;

    public ResourceSet(Card... cards) {
        Map<Card, Integer> map = new EnumMap<>(Card.class);
        for(Card c : cards) {
            map.put(c, map.getOrDefault(c, 0) + 1);
        }
        init(map);
    }

    public ResourceSet(Map<Card, Integer> map) {
        init(map);
    }

    public ResourceSet(List<Integer> list) {
        if(list.size() != CardType.RESOURCE.getCardSet().size()) {
            throw new IllegalArgumentException("Supplied resource frequency list of size " + list.size() + " is not sufficient for resource list of size " + CardType.RESOURCE.getCardSet().size());
        }
        Map<Card, Integer> map = new EnumMap<>(Card.class);
        Set<Card> rset = CardType.RESOURCE.getCardSet();
        var ri = rset.iterator();
        int i = 0;
        while(ri.hasNext()) {
            map.put(ri.next(), list.get(i++));
        }
        init(map);
    }

    //clean the map by entering zeroes where was empty and trimming non-resource card entries
    private void init(Map<Card, Integer> map) {
        for(Card c : CardType.RESOURCE.getCardSet()) {
            map.put(c, map.getOrDefault(c, 0));
        }
        for(Card c : map.keySet()) {
            if(c.getType() != CardType.RESOURCE) {
                map.remove(c);
            }
        }
        this.set = new EnumMap<>(map);
    }

    public int count(Card card) {
        if(card.getType() != CardType.RESOURCE || !this.set.containsKey(card)) {
            return 0;
        }
        return this.set.get(card);
    }

    /**
     * Frequency array according to the natural order of {@link Card}.
     * @return the set as a frequency array
     */
    public List<Integer> toArray() {
        return CardType.RESOURCE.getCardSet().stream().map(this::count).toList();
    }

    public Map<Card, Integer> toMap() {
        return this.set;
    }

    /**
     * Set the number of a specified card in the set.
     * @param card the card
     * @param count the new count
     * @return the old count.
     * @throws IllegalArgumentException if the card count is negative
     */
    public int setCount(Card card, int count) {
        if(card.getType() != CardType.RESOURCE) {
            return this.count(card);
        }
        if(count < 0) {
            throw new IllegalArgumentException("Card count cannot be negative.");
        }
        int old = this.count(card);
        this.set.put(card, count);
        return old;
    }

    public ResourceSet subtract(ResourceSet r) {
        List<Integer> f1 = this.toArray();
        List<Integer> f2 =    r.toArray();
        return new ResourceSet(IntStream.range(0, f1.size()).map(i -> f1.get(i) - f2.get(i)).boxed().toList());
    }
}
