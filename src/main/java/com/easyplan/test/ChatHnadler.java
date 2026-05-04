package com.easyplan.test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatHnadler extends TextWebSocketHandler {
  private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      sessions.add(session);
      // [로그 1] 접속 시 현재 리스트 상태 출력
      System.out.println("✅ 새 연결 발생! 현재 접속자 수: " + sessions.size());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      // [로그 2] 메시지 수신 시 리스트 상태 출력
      System.out.println("📩 메시지 전파 시도 중... 대상 수: " + sessions.size());

      for (WebSocketSession s : sessions) {
          if (s.isOpen()) {
              s.sendMessage(new TextMessage(message.getPayload()));
          } else {
              // [로그 3] 닫힌 세션이 리스트에 남아있는지 확인
              System.out.println("⚠️ 닫힌 세션 발견: " + s.getId());
          }
      }
  }
  
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
      sessions.remove(session);
      System.out.println("🔌 연결 종료. 남은 인원: " + sessions.size());
  }
}
