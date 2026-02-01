package org.example.teasystem.service;

import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取用户列表（管理员）
     */
    PageResponse<UserVO> getUserList(String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取用户详情
     */
    UserVO getUserDetail(Long userId);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户个人信息（昵称、头像）
     */
    UserVO updateUserProfile(Long userId, String nickname, String avatar);
}
