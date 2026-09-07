// package com.example.gateway.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

// @Configuration
// @EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)  // 세션 유효시간 30분
// public class RedisSessionConfig {
// }

package com.example.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisIndexedWebSession;

@Configuration
@EnableRedisIndexedWebSession(maxInactiveIntervalInSeconds = 1800) // web.server 패키지!
public class RedisSessionConfig implements BeanClassLoaderAware {

    private ClassLoader loader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}