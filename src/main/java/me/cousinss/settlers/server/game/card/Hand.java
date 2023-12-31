package me.cousinss.settlers.server.game.card;

import java.lang.reflect.Array;
import java.util.*;

/**
 * An unordered hand of Enum-type elements, stored as a frequency map.
 */
public class Hand<T extends Enum<T>> implements Collection<T> {

    private final EnumMap<T, Integer> frequencyMap;
    private final Class<T> elementClass;

    public Hand(Class<T> elementClass) {
        this.elementClass = elementClass;
        this.frequencyMap = new EnumMap<>(elementClass);
        this.clear();
    }

    /**
     * Returns the total number of the specified element present in the hand.
     * @param elem the element
     * @return the number ({@code 0} if not present)
     */
    public int count(T elem) {
        return this.frequencyMap.get(elem);
    }

    /**
     * Set the total number of the specified element in the hand.
     * @param elem the element
     * @param count the new number of the specified element
     */
    public void setCount(T elem, int count) {
        this.frequencyMap.put(elem, count);
    }

    /**
     * Returns true if the specified hand is a "sub-hand" of the hand: that is, if the count of each element of the sub-hand is less than or equal to the count of the same element in the present hand.
     * @param subhand the subhand
     * @return {@code true} if the specified hand is contained entirely within the hand, {@code false} otherwise
     */
    public boolean containsAll(Hand<T> subhand) {
        for(T elem : elementClass.getEnumConstants()) {
            if(subhand.count(elem) > this.count(elem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int size() {
        return this.frequencyMap.values().stream().reduce(Integer::sum).get();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.frequencyMap.get(o) != null && this.frequencyMap.get(o) > 0;
    }

    @Override
    public Iterator<T> iterator() {
        return Arrays.stream(this.toArray((T[]) Array.newInstance(elementClass, 0))).iterator();
    }

    @Override
    public Object[] toArray() {
        return this.toArray((T[]) Array.newInstance(elementClass, 0));
    }

    @Override
    public <K> K[] toArray(K[] a) {
        K[] out = Arrays.copyOf(a, this.size());
        Iterator<Map.Entry<T, Integer>> set = this.frequencyMap.entrySet().stream().iterator();
        int c = 0;
        T elem;
        int i;
        while(set.hasNext()) {
            Map.Entry<T, Integer> entry = set.next();
            elem = entry.getKey();
            i = entry.getValue();
            while(i-- > 0) {
                out[c++] = (K) elem; //java?
            }
        }
        return out;
    }

    @Override
    public boolean add(T card) {
        this.frequencyMap.put(card, this.frequencyMap.get(card) + 1);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Integer curr = this.frequencyMap.get(o);
        if(curr == 0) {
            return false;
        }
        this.frequencyMap.put((T) o, curr - 1);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().map(this::contains).reduce((a, b) -> a&b).get();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for(T elem : c) {
            this.add(elem);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object o : c) {
            changed |= this.remove(o);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for(T elem : this) {
            if(!(c.contains(elem))) {
                this.remove(elem);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        for(T c : elementClass.getEnumConstants()) {
            this.frequencyMap.put(c, 0);
        }
    }
}