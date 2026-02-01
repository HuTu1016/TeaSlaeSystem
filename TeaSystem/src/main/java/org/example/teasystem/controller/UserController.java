package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.response.UserVO;
import org.example.teasystem.dto.user.UpdateUserRequest;
import org.example.teasystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserVO userVO = userService.getUserDetail(userId);
        return Result.success(userVO);
    }

    @Operation(summary = "更新当前用户信息")
    @PutMapping("/me")
    public Result<UserVO> updateCurrentUser(
            Authentication authentication,
            @RequestBody UpdateUserRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        UserVO userVO = userService.updateUserProfile(userId, request.getNickname(), request.getAvatar());
        return Result.success(userVO);
    }
}
