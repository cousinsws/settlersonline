package me.cousinss.settlers.server;

import me.cousinss.settlers.client.packet.ConnectPacket;
import me.cousinss.settlers.server.message.SampleMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/connect")
    @SendTo("/client/sample")
    public SampleMessage sampleMessage(ConnectPacket connectPacket) throws Exception {
        return new SampleMessage(connectPacket.connectMessage());
    }
}
