package com.jparlant.controller;

import com.jparlant.common.Result;
import com.jparlant.dto.LoginRequestDTO;
import com.jparlant.dto.LoginResultDTO;
import com.jparlant.entity.User;
import com.jparlant.mapper.UserMapper;
import com.jparlant.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${jwt.secret:jparlant-secret-key-for-jwt-token-generation}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400}")
    private Long jwtExpiration;

    private static final String TOKEN_BLACKLIST_PREFIX = "auth:blacklist:";

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResultDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("登录请求: {}", request.getUsername());
        LoginResultDTO result = authService.login(request.getUsername(), request.getPassword());
        return Result.success("登录成功", result);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<LoginResultDTO.UserInfoDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(401, "未登录");
        }

        String token = authHeader.substring(7);
        Long userId = authService.validateToken(token);
        if (userId == null) {
            return Result.error(401, "token无效或已过期");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }

        LoginResultDTO.UserInfoDTO userInfo = new LoginResultDTO.UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar()
        );

        return Result.success(userInfo);
    }
}
