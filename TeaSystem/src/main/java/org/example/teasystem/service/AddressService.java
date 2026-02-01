package org.example.teasystem.service;

import org.example.teasystem.dto.address.AddressSaveRequest;
import org.example.teasystem.dto.address.AddressVO;

import java.util.List;

/**
 * 地址服务接口
 */
public interface AddressService {

    /**
     * 获取用户的地址列表
     */
    List<AddressVO> getUserAddresses(Long userId);

    /**
     * 获取用户的默认地址
     */
    AddressVO getDefaultAddress(Long userId);

    /**
     * 新增地址
     */
    Long addAddress(Long userId, AddressSaveRequest request);

    /**
     * 修改地址
     */
    void updateAddress(Long userId, Long addressId, AddressSaveRequest request);

    /**
     * 删除地址
     */
    void deleteAddress(Long userId, Long addressId);

    /**
     * 设为默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}
