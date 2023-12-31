package me.cousinss.settlers.server.game.die;

/**
 * A fair, standard die.
 */
public class FairDie extends Die {

    public FairDie(int max) {
        super(max);
    }

    @Override
    protected int toss() {
        return ((int) (Math.random() * this.getMax())) + 1;
    }
}
