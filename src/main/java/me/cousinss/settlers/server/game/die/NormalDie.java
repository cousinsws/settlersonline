package me.cousinss.settlers.server.game.die;

import java.util.List;

/**
 * An unfair die which aims to enforce a normal distribution on its rolls.
 */
public class NormalDie extends UnfairDie {

    public NormalDie(int max) {
        super(max);
    }

    @Override
    public int tossUnfair(List<Integer> pastTosses) {
        return -1; //TODO
    }
}
