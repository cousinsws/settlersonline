package me.cousinss.settlers.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/topic");
        config.setUserDestinationPrefix("/user");
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
