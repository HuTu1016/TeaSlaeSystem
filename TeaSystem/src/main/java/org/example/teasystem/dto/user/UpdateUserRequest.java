package org.example.teasystem.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 更新用户信息请求
 */
@Schema(description = "更新用户信息请求")
public class UpdateUserRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
