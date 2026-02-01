package org.example.teasystem.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.response.PageResponse;
import org.example.teasystem.dto.response.UserVO;
import org.example.teasystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户管理控制器
 */
@Tag(name = "管理端-用户管理")
@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<PageResponse<UserVO>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(userService.getUserList(keyword, status, page, size));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserDetail(@PathVariable Long userId) {
        return Result.success(userService.getUserDetail(userId));
    }

    @Operation(summary = "禁用用户")
    @PutMapping("/{userId}/disable")
    public Result<Void> disableUser(@PathVariable Long userId) {
        userService.updateUserStatus(userId, 0);
        return Result.success();
    }

    @Operation(summary = "启用用户")
    @PutMapping("/{userId}/enable")
    public Result<Void> enableUser(@PathVariable Long userId) {
        userService.updateUserStatus(userId, 1);
        return Result.success();
    }
}
