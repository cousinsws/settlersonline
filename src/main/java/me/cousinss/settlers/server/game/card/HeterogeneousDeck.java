package me.cousinss.settlers.server.game.card;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A heterogeneous deck of cards of a single {@link CardType}; i.e. the Development Card deck which contains only Development Cards but whose top element may be a Knight card or a Monopoly card, etc.
 * Alternatively, a completely heterogeneous deck of cards (i.e. a discard pile) if no type is specified.
 */
public class HeterogeneousDeck extends Deck {

    private final Stack<Card> deck;
    private final CardType type;

    /**
     * Creates a new empty deck that accepts a heterogeneous input stream of cards of a specified type.
     * @param type the card type.
     */
    public HeterogeneousDeck(@Nullable CardType type) {
        this.deck = new Stack<>();
        this.type = type;
    }

    /**
     * Creates and populates a deck with cards according to a frequency map added in random order.
     * @param frequencyMap a non-empty frequency map of cards of the same {@link CardType}
     */
    public HeterogeneousDeck(Map<Card, Integer> frequencyMap) {
        this.deck = new Stack<>();
        List<Map.Entry<Card, Integer>> set = frequencyMap.entrySet().stream().toList();
        CardType t = set.get(0).getKey().getType();
        for(Card c : frequencyMap.keySet()) {
            if(c.getType() != t) {
                t = null;
                break;
            }
        }
        this.type = t;
        int sum = frequencyMap.values().stream().reduce(Integer::sum).get();
        while(sum > 0) {
            int roll = (int) (Math.random()*sum);
            int i = 0;
            Map.Entry<Card, Integer> pair = null;
            while(roll >= 0) {
                pair = set.get(i++);
                roll-=pair.getValue();
            }
            this.deck.push(pair.getKey());
            pair.setValue(pair.getValue() - 1);
            sum--;
        }
    }

    @Override
    public CardType getType() {
        return this.type;
    }

    @Override
    public int size() {
        return this.deck.size();
    }

    @Override
    public boolean contains(Object o) {
        return this.deck.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Card> iterator() {
        return this.deck.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.deck.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return this.deck.toArray(a);
    }

    @Override
    public boolean add(Card card) {
        return this.put(card);
    }

    @Override
    public boolean remove(Object o) {
        return this.deck.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.deck.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Card> c) {
        boolean changed = false;
        for(Card card : c) {
            if(this.put(card)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.deck.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.deck.retainAll(c);
    }

    @Override
    public void clear() {
        this.deck.clear();
    }

    @Override
    public Card take() throws IllegalStateException {
        try {
            return this.deck.pop();
        } catch (EmptyStackException e) {
            throw new IllegalStateException("Deck is empty.");
        }
    }

    @Override
    public boolean put(Card c) {
        if(this.getType() != null && c.getType() != this.getType()) {
//            throw new IllegalArgumentException("Card " + c + " cannot be placed into Heterogeneous Deck of type " + this.getType().name());
            return false;
        }
        this.deck.push(c);
        return true;
    }
}
