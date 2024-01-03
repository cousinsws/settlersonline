package me.cousinss.settlers.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Objects;

@Component
public class SocketEventListener {

    private final SimpMessagingTemplate template;
    private final ServerController serverController;

    @Autowired
    public SocketEventListener(SimpMessagingTemplate messagingTemplate, ServerController serverController) {
        this.template = messagingTemplate;
        this.serverController = serverController;
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @EventListener
    public void sessionConnectHandler(SessionConnectEvent event) {
//        System.out.println(event);
//        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
//        String name = headerAccessor.getFirstNativeHeader("name");
//        String gameCode = headerAccessor.getFirstNativeHeader("gameCode");
//        String pastSessionID = headerAccessor.getFirstNativeHeader("pastSessionID");
//        System.out.println("[" + headerAccessor.getSessionId() + "] " + name + ", " + gameCode + ", " + pastSessionID);
//        String id = Objects.requireNonNull(headerAccessor.getSessionId());
//        template.convertAndSendToUser(id, "queue/reply", new Message.JoinGameServer(Message.JoinResult.JOIN_LOBBY, name, id), createHeaders(id));
    }

    @EventListener
    public void sessionDisconnectHandler(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String id = Objects.requireNonNull(headerAccessor.getSessionId());
        for (Map.Entry<String, GameServer> entry : serverController.getServers().entrySet()) {
            String gameCode = entry.getKey();
            GameServer server = entry.getValue();
            if (server.hasUser(id)) {
                if (server.isStarted()) {
                    //TODO uuh shit idk just cry?
                } else {
                    int players = server.removeUser(id);
                    if (players == 0) {
                        serverController.freeServer(gameCode);
                        return;
                    }
                    serverController.broadcastLobbyUserChange(gameCode, "PLAYER_LEFT_EVT");
                }
                return; //same player will not be on two servers
            }
        }
    }
}
