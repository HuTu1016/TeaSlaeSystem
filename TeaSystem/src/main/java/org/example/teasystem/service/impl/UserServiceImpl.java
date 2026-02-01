package org.example.teasystem.service.impl;

import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.UserVO;
import org.example.teasystem.entity.User;
import org.example.teasystem.mapper.UserMapper;
import org.example.teasystem.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResponse<UserVO> getUserList(String keyword, Integer status, Integer page, Integer size) {
        int offset = (page - 1) * size;

        List<User> users = userMapper.selectList(keyword, status, offset, size);
        int total = userMapper.count(keyword, status);

        List<UserVO> voList = users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResponse.of(voList, total, page, size);
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        userMapper.updateStatus(userId, status);
    }

    /**
     * 转换为VO
     */
    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);

        // 设置性别文本
        if (user.getGender() != null) {
            switch (user.getGender()) {
                case 0:
                    vo.setGenderText("未知");
                    break;
                case 1:
                    vo.setGenderText("男");
                    break;
                case 2:
                    vo.setGenderText("女");
                    break;
                default:
                    vo.setGenderText("未知");
            }
        }

        // 设置状态文本
        if (user.getStatus() != null) {
            switch (user.getStatus()) {
                case 0:
                    vo.setStatusText("已禁用");
                    break;
                case 1:
                    vo.setStatusText("正常");
                    break;
                default:
                    vo.setStatusText("未知");
            }
        }

        return vo;
    }

    @Override
    @Transactional
    public UserVO updateUserProfile(Long userId, String nickname, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 只更新非空字段
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        }
        if (avatar != null && !avatar.trim().isEmpty()) {
            user.setAvatar(avatar.trim());
        }

        userMapper.update(user);

        // 返回更新后的用户信息
        return convertToVO(userMapper.selectById(userId));
    }
}
