package com.youquan.wemedia.gateway.filter;

import com.youquan.wemedia.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:47
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request和response对象
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        // 2.判断是否是登录
        if (serverHttpRequest.getURI().getPath().contains("/login")) {
            return chain.filter(exchange);
        }

        // 3.获取token
        String token = serverHttpRequest.getHeaders().getFirst("token");

        // 4.判断token是否存在
        if (StringUtils.isBlank(token)) {
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverHttpResponse.setComplete();
        }

        // 5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int verifyToken = AppJwtUtil.verifyToken(claimsBody);
            if (verifyToken == 1 || verifyToken == 2) {
                serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                return serverHttpResponse.setComplete();
            } else {
                // 添加名为“id”的请求头
                Object id = claimsBody.get("id");
                serverHttpRequest.mutate().header("userId", String.valueOf(id)).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverHttpResponse.setComplete();
        }

        // 6.放行
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
