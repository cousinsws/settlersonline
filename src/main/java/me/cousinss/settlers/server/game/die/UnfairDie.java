package me.cousinss.settlers.server.game.die;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An unfair die, whose rolls may be affected by its past rolls.
 */
public abstract class UnfairDie extends Die {
    private final List<Integer> pastTosses;

    protected UnfairDie(int max) {
        super(max);
        this.pastTosses = new Stack<>();
    }

    @Override
    protected int toss() {
        int toss = tossUnfair(new ArrayList<>(pastTosses));
        pastTosses.add(toss);
        return toss;
    }

    protected abstract int tossUnfair(List<Integer> pastTosses);
}
