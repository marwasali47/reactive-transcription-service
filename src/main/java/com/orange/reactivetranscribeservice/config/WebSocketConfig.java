package com.orange.reactivetranscribeservice.config;


import com.orange.reactivetranscribeservice.dtos.TranscribeRequest;
import com.orange.reactivetranscribeservice.service.TranscriptionProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.springframework.http.server.reactive.ServerHttpResponseDecorator.getNativeResponse;

@Configuration
//@EnableWebFluxSecurity
//@EnableWebFlux
public class WebSocketConfig {


    @Bean
    public HandlerMapping webSocketHandlerMapping(WebSocketHandler wsh) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", wsh);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }


    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter(WebSocketService webSocketService){
        return new WebSocketHandlerAdapter(webSocketService);
    }


    @Bean
    public WebSocketHandler webSocketHandler(TranscriptionProducer  transcriptionProducer ){
        return  session -> {
             Flux<WebSocketMessage> response = session
                    .receive()
                    .map( webSocketMessage -> webSocketMessage.getPayloadAsText())
                    .map(message -> new TranscribeRequest(message))
                    .flatMap(transcribeRequest -> transcriptionProducer.transcribe(transcribeRequest))
                    .map(transcribeResponse -> transcribeResponse.getMessage())
                    .map(s ->  session.textMessage(s))
                     .onErrorComplete(throwable -> throwable.getMessage()!= null);

            return session.send(response);
        };
    }

    @Bean
    public RequestUpgradeStrategy requestUpgradeStrategy(){
        return new ReactorNettyRequestUpgradeStrategy() {
            private WebsocketServerSpec maxFramePayloadLength;

            @Override
            public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler webSocketHandler, String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {
                ServerHttpResponse response = exchange.getResponse();
                HttpServerResponse reactorResponse = getNativeResponse(response);
                HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
                NettyDataBufferFactory bufferFactory = (NettyDataBufferFactory) response.bufferFactory();


              /*  var authRes = validateAuth(handshakeInfo);

                if (authResult == unauthorised) return Mono.just(reactorResponse.status(rejectedStatus))
                        .flatMap(HttpServerResponse::send);*/

                return super.upgrade(exchange,webSocketHandler,subProtocol,handshakeInfoFactory);
            }
        };
    }

    @Bean
    public WebSocketService webSocketService(RequestUpgradeStrategy upgradeStrategy) {

        return new HandshakeWebSocketService(requestUpgradeStrategy());
    }
}
