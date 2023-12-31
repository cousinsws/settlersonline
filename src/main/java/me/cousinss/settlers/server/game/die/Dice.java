package me.cousinss.settlers.server.game.die;

import java.util.Arrays;
import java.util.function.Function;

public class Dice {

    public static final int DEFAULT_FACE_MAXIMUM = 6;

    private final Die[] dice;

    public Dice(Function<Integer, ? extends Die> supplier, int numDice, int faceMax) {
        this.dice = new Die[numDice];
        for(int i = 0; i < numDice; i++) {
            this.dice[i] = supplier.apply(faceMax);
        }
    }

    public Dice(Function<Integer, ? extends Die> supplier, int numDice) {
        this(supplier, DEFAULT_FACE_MAXIMUM, numDice);
    }

    /**
     * Rolls the dice and returns the sum of their faces
     * @return the sum of the roll
     */
    public int roll() {
        for(Die die : this.dice) {
            die.roll();
        }
        return this.sumFaces();
    }

    /**
     * Returns the Dice array.
     * @deprecated See {@link #getFaces()} instead.
     */
    @Deprecated
    public Die[] getDice() {
        return this.dice;
    }

    public int[] getFaces() {
        int[] faces = new int[this.dice.length];
        for(int i = 0; i < faces.length; i++) {
            faces[i] = this.dice[i].getFace();
        }
        return faces;
    }

    public int sumFaces() {
        return Arrays.stream(this.getFaces()).reduce(Integer::sum).orElse(0);
    }

    /**
     * Returns the number of distinct face permutations of {@code numDice} dice of face-maximum {@code faceMax} that could sum to {@code sum}.
     * @param numDice the number of dice
     * @param faceMax the face maximum
     * @param sum the total sum to match
     * @return the number of ways
     */
    public static int getSumWays(int numDice, int faceMax, int sum) {
        if(sum < numDice || sum > faceMax * numDice) {
            return 0;
        }
        //https://math.stackexchange.com/questions/970523/how-many-ways-can-the-sum-of-n-dice-be-s
        //https://mathworld.wolfram.com/Dice.html
        int ways = 0;
        for(int i = 0; i <= (sum - numDice)/faceMax; i++) {
            ways+=expNeg1(i) * choose(numDice, i) * choose(sum - faceMax * i - 1, numDice - 1);
        }
        return ways;
    }

    //Simulates (-1)^n.
    private static int expNeg1(int n) {
        return ((n % 2) * -2) + 1;
    }

    //"Choose" function (n k).
    private static int choose(int n, int k) {
        return factorial(n)/(factorial(k) * (factorial(n - k)));
    }

    //n*n-1*n-2*...*1.
    private static int factorial(int n) {
        if(n <= 1) {
            return 1;
        }
        return n * factorial(n-1);
    }
}
