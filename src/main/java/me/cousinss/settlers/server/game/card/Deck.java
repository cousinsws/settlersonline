package me.cousinss.settlers.server.game.card;

import java.util.Collection;

/**
 * Represents a deck of cards.
 */
public abstract class Deck implements Collection<Card> {

    /**
     * Returns the deck's card type, or {@code null} if the deck has an unspecified type.
     * @return the card type
     */
    public abstract CardType getType();

    /**
     * Returns the number of cards currently in the deck.
     * @return the size
     */
    @Override
    public abstract int size();

    /**
     * Take a card from the top of the deck.
     * @return the card
     * @throws IllegalStateException if there was no card present
     */
    public abstract Card take() throws IllegalStateException;

    /**
     * Place a card into the deck.
     * @param c the card
     * @return {@code true} if the card was successfully placed into the deck (i.e. the deck has changed), {@code false} otherwise.
     */
    public abstract boolean put(Card c);

    /**
     * Whether the deck is empty (it has no cards).
     * @return {@code true} if the deck is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.size() == 0;
    }
}
