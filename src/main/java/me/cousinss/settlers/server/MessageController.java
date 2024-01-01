package me.cousinss.settlers.server;

import me.cousinss.settlers.client.Packet;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    private final Server server;

    public MessageController() {
        this.server = new Server();
    }

    @MessageMapping("/connect")
    @SendToUser("/client/givesession")
    public Message.GiveSession sampleMessage(@Payload Packet.Connect connectPacket) {
        User user = new User(connectPacket.name());
        if(!server.addUser(user)) {
            throw new RuntimeException("Server user map is full.");
        }
        return new Message.GiveSession(connectPacket.name(), user.getID(), false);
    }

    @MessageMapping("/reconnect")
    @SendToUser("/client/givesession")
    public Message.GiveSession sampleMessage(@Payload Packet.Reconnect reconnectPacket) {
        User user = server.getUser(reconnectPacket.id());
        return new Message.GiveSession(reconnectPacket.name(), user.getID(), true);
    }
}
