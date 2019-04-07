package com.github.rudylucky.auth.cache;

import com.github.rudylucky.auth.enums.ResourcePermissionTypeEnum;
import com.github.rudylucky.auth.manager.ResourcePermissionManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.yml")
public class ResourcePermissionCacheManager {

    private ResourcePermissionManager resourcePermissionManager;
    private RedisPublisher redisPublisher;

    @Value("${redis.enabled:false}")
    private Boolean redisEnabled;

    @Autowired
    public ResourcePermissionCacheManager(ResourcePermissionManager resourcePermissionManager,
                                          RedisPublisher redisPublisher) {
        this.resourcePermissionManager = resourcePermissionManager;
        this.redisPublisher = redisPublisher;
    }

    public boolean cacheHasPermission(String userId, String resourceId, ResourcePermissionTypeEnum resourcePermissionType){
        if(redisEnabled) {
            String key = userId.substring(0, 8) + resourceId.substring(0, 8) + resourcePermissionType.name();
            String s = redisPublisher.get(key);
            if (StringUtils.isBlank(s)) {
                Boolean result = resourcePermissionManager.hasPermission(userId, resourceId, resourcePermissionType);
                redisPublisher.set(key, result ? "1" : "0");
                return result;
            } else {
                return "1".equals(s);
            }
        } else{
            return resourcePermissionManager.hasPermission(userId, resourceId, resourcePermissionType);
        }
    }
}
