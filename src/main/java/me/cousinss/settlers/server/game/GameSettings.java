package me.cousinss.settlers.server.game;

import java.util.EnumMap;
import java.util.Map;

public class GameSettings {

    private static final String ON_LABEL = "ON";
    private static final String OFF_LABEL = "OFF";
    //general
    public static final int BOOLEAN_ON = 0;
    public static final int BOOLEAN_OFF = 1;
    //board setup
    public static final int BOARD_BEGINNER = 0;
    public static final int BOARD_BALANCED = 1;
    public static final int BOARD_RANDOM = 2;
    //player order
    public static final int FIRST_PLAYER = 0;
    public static final int RANDOM_PLAYER = 1;

    public enum Setting {
        REJECT_IMPOSSIBLE_TRADES(
                "Auto-reject trades if the recipient does not have the desired cards in their hand." +
                        "Leaving this option on allows players to \"see\" opponents' hands by strategically making trade requests, " +
                        "but speeds up play significantly.",
                BOOLEAN_ON,
                ON_LABEL,
                OFF_LABEL
        ),
        BALANCE_DICE(
                "Enforce a normal distribution on dice rolls, in place of pure randomness.",
                BOOLEAN_OFF,
                ON_LABEL,
                OFF_LABEL
        ),
        BOARD_SETUP(
                "Set up the board.",
                BOARD_RANDOM,
                "Beginner's Board",
                "Balanced",
                "Completely Random"
        ),
        PLAYER_ORDER(
                "Decide who plays first.",
                FIRST_PLAYER,
                "Player 1",
                "Random Player"
        ),
        ;

        private final String[] labels;
        private final int defaultValue;
        private final String tooltip;

        Setting(String tooltip, int defaultValue, String... labels) {
            this.labels = labels;
            this.defaultValue = defaultValue;
            this.tooltip = tooltip;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public String[] getLabels() {
            return this.labels;
        }
    }

    private final Map<Setting, Integer> settings;

    /**
     * Constructs a list of game settings with the default (recommended) values.
     */
    public GameSettings() {
        this.settings = new EnumMap<>(Setting.class);
        for(Setting s : Setting.values()) {
            settings.put(s, s.defaultValue);
        }
    }

    public int getValue(Setting setting) {
        return settings.get(setting);
    }

    public String getValueLabel(Setting setting) {
        return setting.labels[getValue(setting)];
    }
}
