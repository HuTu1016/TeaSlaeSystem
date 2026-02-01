package org.example.teasystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.teasystem.common.result.Result;
import org.example.teasystem.dto.address.AddressSaveRequest;
import org.example.teasystem.dto.address.AddressVO;
import org.example.teasystem.service.AddressService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 收货地址控制器（用户端）
 */
@Tag(name = "收货地址接口")
@RestController
@RequestMapping("/user/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "获取用户地址列表")
    @GetMapping
    public Result<List<AddressVO>> getAddresses(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<AddressVO> addresses = addressService.getUserAddresses(userId);
        return Result.success(addresses);
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AddressVO address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<Long> addAddress(Authentication authentication,
            @Valid @RequestBody AddressSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        Long addressId = addressService.addAddress(userId, request);
        return Result.success(addressId);
    }

    @Operation(summary = "修改地址")
    @PutMapping("/{addressId}")
    public Result<Void> updateAddress(Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressSaveRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.updateAddress(userId, addressId, request);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(Authentication authentication,
            @PathVariable Long addressId) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(userId, addressId);
        return Result.success();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/{addressId}/set-default")
    public Result<Void> setDefaultAddress(Authentication authentication,
            @PathVariable Long addressId) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.setDefaultAddress(userId, addressId);
        return Result.success();
    }
}
