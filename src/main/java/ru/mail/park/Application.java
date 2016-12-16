package ru.mail.park;

import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import ru.mail.park.game.config.GameSettings;
import ru.mail.park.websocket.GameSocketHandler;

import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(GameSettings.class)
@EnableWebSocket
@SpringBootApplication
public class Application implements WebSocketConfigurer {
    public static final long IDLE_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(5);
    public static final int BUFFER_SIZE_BYTES = 8192;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(gameWebSocketHandler(), "/game")
                .addInterceptors(new HttpSessionHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        final WebSocketPolicy policy = new WebSocketPolicy(WebSocketBehavior.SERVER);
        policy.setInputBufferSize(BUFFER_SIZE_BYTES);
        policy.setIdleTimeout(IDLE_TIMEOUT_MS);
        return new DefaultHandshakeHandler(new JettyRequestUpgradeStrategy(new WebSocketServerFactory(policy)));
    }

    @Bean
    public WebSocketHandler gameWebSocketHandler() {
        return new PerConnectionWebSocketHandler(GameSocketHandler.class);
    }
}
