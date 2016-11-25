package ru.mail.park.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.model.UserProfile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RemotePointService {
    private Map<UserProfile, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    public void registerUser(UserProfile userProfile, WebSocketSession webSocketSession) {
        sessions.put(userProfile, webSocketSession);
    }

    public boolean isConnected(UserProfile userProfile) {
        return sessions.containsKey(userProfile) && sessions.get(userProfile).isOpen();
    }

    public void removeUser(UserProfile userProfile) {
        sessions.remove(userProfile);
    }

    public void cutDownConnection(UserProfile userProfile, CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(userProfile);
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException ignore) {
            }
        }
    }

    public void sendMessageToUser(UserProfile userProfile, Message message) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(userProfile);
        if (webSocketSession == null) {
            throw new IOException("no game websocket for user " + userProfile.getLogin());
        }
        if (!webSocketSession.isOpen()) {
            throw new IOException("session is closed or does not exsist");
        }
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException | WebSocketException e) {
            throw new IOException("Unnable to send message", e);
        }
    }
}
