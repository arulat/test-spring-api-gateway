package kz.arulat.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtFilter extends AbstractNameValueGatewayFilterFactory {

    private static final String WWW_AUTH_HEADER = "WWW-Authenticate";
    private static final String X_JWT_SUB_HEADER = "X-jwt-sub";

    @Autowired
    private AuthServiceHelper authServiceHelper;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return ((exchange, chain) -> {
            try {
                String token = this.extractJWTToken(exchange.getRequest());
                String result = authServiceHelper.verifyJwt(token);

                ServerHttpRequest request = exchange.getRequest().mutate().header(X_JWT_SUB_HEADER, result).build();

                return chain.filter(exchange.mutate().request(request).build());

            } catch (Exception e) {

                logger.error(e.toString());
                return this.onError(exchange, e.getMessage());
            }
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err){
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(WWW_AUTH_HEADER, formatErrorMsg(err));

        return response.setComplete();
    }

    private String extractJWTToken(ServerHttpRequest request){

        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            throw new RuntimeException("Authorization header is missing");
        }

        List<String> headers = request.getHeaders().get(HttpHeaders.AUTHORIZATION);

        if(headers == null || headers.isEmpty()){
            throw new RuntimeException("Authorization header is empty");
        }

        String credentials = headers.get(0).trim();
        String[] components = credentials.split("\\s");

        if(components.length != 2) {
            throw new RuntimeException("Malformat Authorization content");
        }

        if(!components[0].equals("Bearer")){
            throw new RuntimeException("Bearer is needed");
        }

        return components[0].trim() + " " + components[1].trim();
    }

    private String formatErrorMsg(String msg)
    {
        return String.format("Bearer realm=\"acm.com\", " +
                "error=\"https://tools.ietf.org/html/rfc7519\", " +
                "error_description=\"%s\" ",  msg);
    }
}
