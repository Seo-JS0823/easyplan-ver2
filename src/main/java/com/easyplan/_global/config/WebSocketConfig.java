package com.easyplan._global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.easyplan.test.ChatHnadler;
import com.easyplan.test.EchoHandler;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
	@Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
      // 메시지를 구독(수신)하는 경로의 접두사
      config.enableSimpleBroker("/topic"); 
      // 메시지를 발행(전송)할 때 사용하는 경로의 접두사
      config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
      // 클라이언트가 WebSocket에 접속할 엔드포인트 설정
      registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
  }
  
  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
      registry.addHandler(chatHandler(), "/echo")
              .setAllowedOrigins("*"); // 테스트를 위해 모든 도메인 허용
  }
  
  @Bean
  EchoHandler echoHandler() {
    return new EchoHandler();
  }
  
  @Bean
  ChatHnadler chatHandler() {
  	return new ChatHnadler();
  }
}
