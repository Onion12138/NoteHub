package com.ecnu.haven.socket;

import com.ecnu.onion.vo.BaseResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author HavenTong
 * @date 2020/2/14 7:45 下午
 * websocket服务器
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{email}")
public class WebSocket {

    private Session session;

    private static CopyOnWriteArrayList<WebSocket> webSockets = new CopyOnWriteArrayList<>();

    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email) {
        this.session = session;
        sessionPool.put(email, session);
        webSockets.add(this);
        log.info("email: {}, session: {}, size: {}", email, session, sessionPool.size());
        log.info("pool: {}", sessionPool);
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("closed");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("message: {}", message);
    }

    public void sendMessage(String email, String message){
        Session session = sessionPool.get(email);
        if (!Objects.isNull(session)) {
            session.getAsyncRemote().sendText(message);
        } else {
            log.info("email{} have no session", email);
        }
    }
}
