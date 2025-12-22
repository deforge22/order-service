package io.dinesync.orderstream.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    private String buildKey(CachePrefix prefix, String key) {
        return prefix.getKey(key);
    }

    public void set(CachePrefix prefix, String key, Object value) {
        redisTemplate.opsForValue().set(buildKey(prefix, key), value);
    }

    public void set(CachePrefix prefix, String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(buildKey(prefix, key), value, ttl);
    }

    public <T> T get(CachePrefix prefix, String key, Class<T> type){
        var result = redisTemplate.opsForValue().get(buildKey(prefix, key));
        return type.cast(result);
    }

    public <T> T get(CachePrefix prefix, String key, ParameterizedTypeReference<T> typeRef) {
        Object result = redisTemplate.opsForValue().get(buildKey(prefix, key));

        if (result == null) {
            return null;
        }

        var javaType = objectMapper.getTypeFactory()
                .constructType(typeRef.getType());

        return objectMapper.convertValue(result, javaType);
    }

    public Boolean delete(CachePrefix prefix, String key) {
        log.info("data removed from cache for prefix: {} & key: {}", prefix.getPrefix(), key);
        return redisTemplate.delete(buildKey(prefix, key));
    }

    public Boolean exists(CachePrefix prefix, String key) {
        return redisTemplate.hasKey(buildKey(prefix, key));
    }

}
