package me.cousinss.settlers.server;

import me.cousinss.settlers.server.game.Game;
import me.cousinss.settlers.server.game.GameSettings;
import me.cousinss.settlers.server.game.player.Player;

import java.util.*;

public class GameServer {

    public static final int GAME_CODE_LEN = 4;
    private static final String BOT_ID_PREFIX = "BOT_";
    private List<String> BOT_NAMES = new ArrayList<>(List.of(
            "Conradine",
            "Harald",
            "Lutgard",
            "Jaasir",
            "Taamira",
            "Aston",
            "Owiti",
            "Eliadah",
            "Thaddeus",
            "Susanna",
            "Festus"
    ));

    //sessionID->name (TODO replace name with more metadata (name, isleader, isbot, color)
    private final Map<String, Player.Profile> users;
    private boolean started;
    private Game game;
    //sessionID->player
    private final Map<String, Player> players;

    private int freeBotName;

    public GameServer() {
        this.users = new HashMap<>();
        this.players = new HashMap<>();
        this.game = null;
        this.started = false;
        this.freeBotName = 0;
        Collections.shuffle(BOT_NAMES); //for fun
    }

    public static String generateGameCode() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < GameServer.GAME_CODE_LEN; i++) {
            sb.append((char)('A'+Math.random()*26));
        }
        return sb.toString();
    }

    public Game startGame() {
        if(this.game != null) {
            return this.game;
        }
        List<Player> playerList = new ArrayList<>();
        users.forEach((id, profile) -> {
            Player player = new Player(profile);
            playerList.add(player);
            players.put(id, player);
        });
        this.game = new Game(playerList, new GameSettings());
        this.started = true;
        return game;
    }

    public List<Map.Entry<String, Player.Profile>> getBotProfiles() {
        int i = 0;
        String id;
        Player.Profile bot;
        List<Map.Entry<String, Player.Profile>> list = new ArrayList<>();
        while(null != (bot = this.getUser(id = BOT_ID_PREFIX + i++))) {
            list.add(new AbstractMap.SimpleEntry<>(id, bot));
        }
        return list;
    }

    public List<Map.Entry<String, Player>> getBots() {
        int i = 0;
        String id;
        Player bot;
        List<Map.Entry<String, Player>> list = new ArrayList<>();
        while(null != (bot = this.getPlayer(id = BOT_ID_PREFIX + i++))) {
            list.add(new AbstractMap.SimpleEntry<>(id, bot));
        }
        return list;
    }

    public boolean isStarted() {
        return this.started;
    }

    public List<Player.Profile> getConnectedUsers() {
        return this.users.values().stream().toList();
    }

    public boolean hasUser(String sessionID) {
        return this.users.containsKey(sessionID);
    }

    public List<String> getBotNames() {
        return List.of("BOT Joe", "BOT Bob");
    }

    public Player.Profile getUser(String sessionID) {
        return this.users.get(sessionID);
    }

    public Player getPlayer(String sessionID) {
        return this.players.get(sessionID);
    }

    /**
     * Adds the user and returns the cleaned user (server-approved).
     * @param sessionID session id
     * @param rawProfile the raw (user-inputted) profile data
     * @return the profile alias
     */
    public Player.Profile addUser(String sessionID, Player.Profile rawProfile) {
        String cleanName = cleanName(rawProfile.name());
        Player.Color cleanColor = cleanColor(rawProfile.color());
        Player.Profile cleanProfile = new Player.Profile(rawProfile.ID(), rawProfile.type(), cleanName, cleanColor, rawProfile.isLeader());
        this.users.put(sessionID, cleanProfile);
        return cleanProfile;
    }

    /**
     * Get the server-approved name.
     * @param rawName the raw name
     * @return the name
     */
    private String cleanName(String rawName) {
        if(rawName == null || rawName == "") {
            rawName = "Unnamed Player";
        }
        if(!isUserWithName(rawName)) {
            return rawName;
        }
        int aliasNum = 1;
        while(isUserWithName(rawName + " (" + aliasNum + ")")) {
            aliasNum++;
        }
        return rawName + " (" + aliasNum + ")";
    }

    private boolean isUserWithName(String name) {
        return users.values().stream().map(Player.Profile::name).anyMatch(s -> s.equals(name));
    }

    private Player.Color cleanColor(Player.Color color) {
        if(!isUserWithColor(color)) {
            return color;
        }
        for(Player.Color c : Player.Color.values()) {
            if(!isUserWithColor(c)) {
                return c;
            }
        }
        return Player.Color.BLACK;
    }

    private boolean isUserWithColor(Player.Color color) {
        return users.values().stream().map(Player.Profile::color).anyMatch(s -> s.equals(color));
    }

    /**
     * Returns the number of human users remaining (If =0, should free this server).
     * @param sessionID the sessionID of the user.
     * @return
     */
    public int removeUser(String sessionID) {
        Player.Profile profile = users.remove(sessionID);
        if(profile == null) {
            return users.size();
        }
        if(profile.isLeader()) {
            //must find new leader
            users.keySet().stream().findFirst().ifPresent(id -> users.put(id, users.get(id).setLeader(true)));
        }
        return users.size() - numBots();
    }

    protected int numBots() {
        return (int) users.values().stream().filter(profile -> profile.type().equals(Player.PlayerType.ROBOT)).count();
    }

    public boolean addBot() {
        if(users.size() == 6) { //don't plan on making Catan any larger than this
            return false;
        }
        int numBots = numBots();
        int myNum = numBots;
        String id = BOT_ID_PREFIX + myNum;
        this.users.put(id,
                new Player.Profile(
                        id,
                        Player.PlayerType.ROBOT,
                        getBotName(),
                        cleanColor(Player.Color.values()[(int)(Math.random()*Player.Color.values().length)]),
                        false));
        return true;
    }

    private String getBotName() {
        return BOT_NAMES.get((freeBotName++)%BOT_NAMES.size());
    }

    public boolean removeBot() {
        int numBots = numBots();
        if(numBots == 0) {
            return false;
        }
        this.removeUser(BOT_ID_PREFIX + (numBots - 1));
        return true;
    }
}
