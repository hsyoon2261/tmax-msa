package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private Environment env;

    public static class Config {
        // Put configuration properties here
    }

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }
//절차 로그인 -> token반환 -> user에게 token을 가지고 사용자 정보를 요청한다 ->서버측에서 토큰정보 열어봐서 토큰정보가 맞는지 확인 how? header(include token)
// 헤더안에 들어있는 토큰 정보를 가지고 확인을 한다.
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

//헤더에 적절한 값(인증)이 있나 테스트
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED); // 401
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0); //리스트 형태로 저장
            String jwt = authorizationHeader.replace("Bearer", "");
            //bearer 부분을 없애주고 나머지 부분을 토큰으로 계산해서 판단한다.

            //그 토큰이 맞는지 검증
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT is not valid", HttpStatus.UNAUTHORIZED); // 401
            }

            return chain.filter(exchange); //exchange 객체 반환
        };
    }
//onerror 정의
    //mono와 flux -> webflux 개념에서 나온것으로 클라이언트 요청이 들어왔을때 반환하는 타입 - 단일 : 모노 다중 플럭스
    //매개변수로 exchange&에러타입 들어오고 httpstatus반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
//isjwtvalid 정의
    private boolean isJwtValid(String jwt) {
        boolean result = true;

        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt.trim()).getBody()
                    .getSubject();
        } catch (Exception ex) {
            result = false;
        }

        if (subject == null || subject.isEmpty()) {
            result = false;
        }

        return result;
    }
}

//이방식은 spring mvc 방식이 아닌 webflux방식으로 비동기 방식으로 처리한다.