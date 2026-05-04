package com.easyplan.test;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class EchoHandler extends TextWebSocketHandler {
	@Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
      String payload = message.getPayload();
      System.out.println("받은 메시지: " + payload);
      
      // 받은 메시지를 그대로 다시 전송 (Echo)
      session.sendMessage(new TextMessage("Echo: " + payload));
  }
}
