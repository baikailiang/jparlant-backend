package com.jparlant.service.impl;

import com.jparlant.dto.LoginResultDTO;
import com.jparlant.entity.User;
import com.jparlant.exception.BusinessException;
import com.jparlant.mapper.UserMapper;
import com.jparlant.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${jwt.secret:jparlant-secret-key-for-jwt-token-generation}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_TOKEN_PREFIX = "auth:user:";

    @Override
    public LoginResultDTO login(String username, String password) {
        log.info("用户登录: {}", username);

        // 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证密码（实际项目应使用BCrypt等加密验证，这里简化处理）
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException(401, "用户已被禁用");
        }

        // 生成token
        String token = generateToken(user);

        // 构建返回结果
        LoginResultDTO.UserInfoDTO userInfo = new LoginResultDTO.UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar()
        );

        log.info("用户登录成功: {}, token: {}", username, token);
        return new LoginResultDTO(token, userInfo);
    }

    @Override
    public Long validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (subject == null) {
                return null;
            }

            // 检查token是否在黑名单
            Boolean isBlacklisted = redisTemplate.hasKey(TOKEN_PREFIX + token);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                return null;
            }

            return Long.parseLong(subject);
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void logout(String token) {
        // 将token加入黑名单
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, "logout", jwtExpiration, TimeUnit.SECONDS);
        // 删除用户的token记录
        Long userId = validateToken(token);
        if (userId != null) {
            redisTemplate.delete(USER_TOKEN_PREFIX + userId);
        }
        log.info("用户登出, token: {}", token);
    }

    /**
     * 生成JWT token
     */
    private String generateToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // 将token存储到Redis
        redisTemplate.opsForValue().set(USER_TOKEN_PREFIX + user.getId(), token, jwtExpiration, TimeUnit.SECONDS);

        return token;
    }
}
