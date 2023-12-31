package me.cousinss.settlers.server;

import me.cousinss.settlers.client.Packet;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/connect")
    @SendTo("/client/sample")
    public Message.Sample sampleMessage(Packet.Connect connectPacket) throws Exception {
        return new Message.Sample(connectPacket.connectMessage());
    }
}
