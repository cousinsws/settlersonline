package me.cousinss.settlers.server.game.die;

public abstract class Die {

    private final int max;
    private int face;

    protected Die(int max) {
        this.max = max;
        this.face = -1;
    }

    /**
     * Rolls the die and updates its face value to its newly rolled value.
     * @return the new face value
     */
    public int roll() {
        this.face = this.toss();
        return this.face;
    }

    /**
     * Tosses the die and returns the result of the toss.
     * @return the result of the toss
     */
    protected abstract int toss();

    public int getMax() {
        return this.max;
    }

    public int getFace() {
        return this.face;
    }
}
