package com.jparlant.dto;

import lombok.Data;

/**
 * 登录结果DTO
 */
@Data
public class LoginResultDTO {

    private String token;
    private UserInfoDTO userInfo;

    @Data
    public static class UserInfoDTO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;

        public UserInfoDTO(Long id, String username, String nickname, String avatar) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.avatar = avatar;
        }
    }

    public LoginResultDTO(String token, UserInfoDTO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}
