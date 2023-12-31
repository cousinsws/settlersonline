package me.cousinss.settlers.server.game.card;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A deck of cards of all the same {@link Card} - likely face up; i.e. the Wood resource deck.
 */
public class HomogeneousDeck extends Deck {

    private int size;
    private final Card card;

    public HomogeneousDeck(int size, Card card) {
        this.size = size;
        this.card = card;
    }

    @Override
    public CardType getType() {
        return this.card.getType();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(Object o) {
        if(!(o instanceof Card c)) {
            return false;
        }
        return c.equals(this.card) && !this.isEmpty();
    }

    @NotNull
    @Override
    public Iterator<Card> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return !isEmpty();
            }

            @Override
            public Card next() {
                return card;
            }
        };
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return this.toArray(new Object[0]);
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        if (a.length < this.size()) {
            Card[] arr = new Card[this.size()];
            Arrays.fill(arr, card);
            return (T[]) Arrays.copyOf(arr, this.size(), a.getClass());
        }
        return null; //idc about this feature
    }

    @Override
    public boolean add(Card card) {
        return put(card);
    }

    @Override
    public boolean remove(Object o) {
        if(this.isEmpty() || !this.contains(o)) {
            return false;
        }
        this.take();
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object card : c) {
            if(!this.contains(card)) {
                return false;
            }
        }
        return true;
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
        boolean changed = false;
        for(Object card : c) {
            if(this.remove(card)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        int match = 0;
        for(Object o : c) {
            if(this.contains(o)) {
                match++;
            }
        }
        int s = this.size();
        this.setSize(Math.min(match, s));
        return s == this.size();
    }

    @Override
    public void clear() {
        this.setSize(0);
    }

    @Override
    public Card take() throws IllegalStateException {
        if(this.size() == 0) {
            throw new IllegalStateException("Deck is empty.");
        }
        size--;
        return card;
    }

    @Override
    public boolean put(Card c) {
        if(c == this.card) {
            this.size++;
            return true;
        }
        return false;
    }

    /**
     * In an unchecked manner, sets the size of this homogenous deck.
     * @param size the new size
     */
    public void setSize(int size) {
        this.size = size;
    }
}
