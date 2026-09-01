package com.example.gw.config;

import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.servlet.function.ServerRequest;

@Configuration
public class RateLimitConfig {

  // Redis 내부에서 실행될 밀리초(ms) 단위 토큰 버킷 Lua 스크립트
  private static final String RATE_LIMITER_LUA =
      "local key = KEYS[1]\n" +
      "local capacity = tonumber(ARGV[1])\n" +
      "local refillPerMs = tonumber(ARGV[2])\n" +
      "local now = tonumber(ARGV[3])\n" +
      "local requested = tonumber(ARGV[4])\n" +
      "\n" +
      "local lastRefillKey = key .. ':last'\n" +
      "local tokensKey = key .. ':tokens'\n" +
      "\n" +
      "local lastRefill = tonumber(redis.call('get', lastRefillKey))\n" +
      "local tokens = tonumber(redis.call('get', tokensKey))\n" +
      "\n" +
      "if lastRefill == nil or tokens == nil then\n" +
      "  tokens = capacity\n" +
      "  lastRefill = now\n" +
      "else\n" +
      "  local delta = math.max(0, now - lastRefill)\n" +
      "  tokens = math.min(capacity, tokens + (delta * refillPerMs))\n" +
      "  lastRefill = now\n" +
      "end\n" +
      "\n" +
      "if tokens >= requested then\n" +
      "  tokens = tokens - requested\n" +
      "  redis.call('set', tokensKey, tokens, 'EX', 60)\n" +
      "  redis.call('set', lastRefillKey, lastRefill, 'EX', 60)\n" +
      "  return 1\n" +
      "else\n" +
      "  redis.call('set', tokensKey, tokens, 'EX', 60)\n" +
      "  redis.call('set', lastRefillKey, lastRefill, 'EX', 60)\n" +
      "  return 0\n" +
      "end";

  @Bean
  public DefaultRedisScript<Long> rateLimiterScript() {
    DefaultRedisScript<Long> script = new DefaultRedisScript<>();
    script.setScriptText(RATE_LIMITER_LUA);
    script.setResultType(Long.class);
    return script;
  }
  
  // 어떤 기준으로 사용자를 구분하여 토큰을 차감할지 결정
  @Bean
  public Function<ServerRequest, String> userKeyResolver() {
    return request -> {
      // 헤더에서 X-User-ID를 찾아 Key로 사용
      // 로그인 사용자가 인증 필터를 거친 뒤 이 헤더가 있다고 가정한 상황임
      String userId = request.headers().firstHeader("X-User-ID");

      // 비로그인 상황이면(X-User-ID가 없으면) IP 주소를 Key로 사용
      if (userId == null) {
        userId = request.remoteAddress()
            .map(addr -> addr.getAddress().getHostAddress())
            .orElse("anonymous");
      }

      return userId;
    };
  }

  // 용량: 5개, 초당 충전: 1개 (밀리초당 0.001개) 설정 예시
  public boolean tryConsume(
      StringRedisTemplate redisTemplate, 
      DefaultRedisScript<Long> script, 
      String key
  ) {
    long capacity = 5;
    double refillPerMs = 0.001; // 1초에 1개 충전 (1000ms당 1개)
    long nowMs = System.currentTimeMillis();
    
    Long result = redisTemplate.execute(
        script,
        List.of("rate_limit:" + key),
        String.valueOf(capacity),
        String.valueOf(refillPerMs),
        String.valueOf(nowMs),
        "1"
    );
    
    return result != null && result == 1L;
  }
}