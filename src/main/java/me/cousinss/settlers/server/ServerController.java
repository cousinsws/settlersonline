package me.cousinss.settlers.server;

import me.cousinss.settlers.client.Packet;
import me.cousinss.settlers.server.game.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ServerController {

    private final Map<String, GameServer> servers;
    private final SimpMessagingTemplate template;

    public Map<String, GameServer> getServers() {
        return this.servers;
    }

    @Autowired
    public ServerController(SimpMessagingTemplate template) {
        this.servers = new HashMap<>();
        this.template = template;
    }

    @MessageMapping("/connect")
    @SendToUser("/queue/joingameserver")
    public Message.JoinGameServer connectToGameServer(
            @Payload Packet.Connect connectPacket,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String sessionID = headerAccessor.getSessionId();
//        System.out.println("Recieved Packet.Connect " + connectPacket.toString());
        GameServer server = servers.get(connectPacket.gameCode());
        broadcastLobbyUserChange(connectPacket.gameCode(), "PLAYER_ADDED_EVT");
        if(server == null) {
            return new Message.JoinGameServer(Message.JoinResult.BAD_CODE, connectPacket.profile().setID(sessionID), connectPacket.gameCode());
        }
        if(!(connectPacket.profile().ID() == null || connectPacket.profile().ID().equals(""))) {
            //past id was entered from cookie, try to reconnect
            //TODO
        }
        connectPacket = new Packet.Connect(connectPacket.profile().setID(sessionID), connectPacket.gameCode());
        return new Message.JoinGameServer(Message.JoinResult.JOIN_LOBBY, server.addUser(sessionID, connectPacket.profile()), connectPacket.gameCode()); //TODO this ignores the possibility of rejoin (should reference Packet.Connect#pastSessionID
    }

    @GetMapping("/queue/game/{gameCode}/getPlayers")
    public ResponseEntity<List<Player.Profile>> getServerPlayers(
            @PathVariable String gameCode
    ) {
//        System.out.println("Getting players for server " + gameCode);
        List<Player.Profile> out = servers.get(gameCode).getConnectedUsers();
//        System.out.println(out);
        return ResponseEntity.ok(out);
    }

    @PostMapping("/queue/game/{gameCode}/addBot")
    public ResponseEntity<List<Boolean>> addBot(
            Packet.BotAddType postType,
            @PathVariable String gameCode
    ) {
        if(!servers.containsKey(gameCode)) {
            return ResponseEntity.ok(List.of(false));
        }
        GameServer server = servers.get(gameCode);
        boolean changed;
        if(postType.equals(Packet.BotAddType.ADD)) {
            changed = server.addBot();
        } else {
            changed = server.removeBot();
        }
        if(changed) {
            broadcastLobbyUserChange(gameCode, "BOT_CHANGE_EVT");
        }
        return ResponseEntity.ok(List.of(changed));
    }

    @PostMapping("/queue/game/{gameCode}/start")
    public ResponseEntity<Void> startGame(
            @PathVariable String gameCode
    ) {
        if(!servers.containsKey(gameCode)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GameServer server = servers.get(gameCode);
        server.startGame();
        broadcastGameStart(gameCode, server);
        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }

    private void broadcastGameStart(String gameCode, GameServer server) {
        Message.GameStart msg = new Message.GameStart(
                gameCode,
                server.getGame().getBoard().getTileMap().entrySet().stream().map(e -> new Message.CoordinateTile(e.getKey(), e.getValue())).collect(Collectors.toSet()),
                server.getConnectedUsers().size() < 5 ? Message.GameScenario.THREE_FOUR : Message.GameScenario.FIVE_SIX,
                server.getGame().getPlayers().stream().map(Player::getProfile).toList(),
                server.getGame().getBoard().getLandVertices(),
                server.getGame().getBoard().getRoadSpaces(),
                server.getGame().getResourceDecks(),
                server.getGame().getBoard().getPorts().entrySet().stream().map(e -> new Message.AnchorPort(e.getKey(), e.getValue())).collect(Collectors.toSet())
        );
        template.convertAndSend("/queue/game/" + gameCode + "/gamestart", msg);
    }

    @GetMapping("/queue/create-server")
    public ResponseEntity<List<String>> createGameServer() {
        String code;
        while(servers.containsKey(code = GameServer.generateGameCode())) {}
        servers.put(code, new GameServer());
        System.out.println("SCONT > Creating server with code " + code);
        return ResponseEntity.ok(List.of(code));
    }

    protected void broadcastLobbyUserChange(String gameCode, String debugMsg) {
        template.convertAndSend("/queue/game/" + gameCode + "/playerconnect", debugMsg);
    }

    /**
     * Delete the server and free its gameCode.
     * @param gameCode the gameCode
     */
    public void freeServer(String gameCode) {
        this.servers.remove(gameCode);
        System.out.println("SCONT > Freeing server with code " + gameCode + ". " + this.servers.size() + " servers remain operational.");
    }
}