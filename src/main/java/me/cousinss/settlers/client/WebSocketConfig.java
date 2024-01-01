package me.cousinss.settlers.client;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/client");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/settlers-app")
//                .setHandshakeHandler((request, response, wsHandler, attributes) -> {
//                    if (request instanceof ServletServerHttpRequest servletRequest) {
//                        HttpSession session = servletRequest
//                                .getServletRequest().getSession();
//                        attributes.put("sessionId", session.getId());
//                    }
//                    return true;
//                })
//                .withSockJS()
                ;
    }

}
