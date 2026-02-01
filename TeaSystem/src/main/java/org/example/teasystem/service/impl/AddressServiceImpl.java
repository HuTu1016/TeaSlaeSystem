package org.example.teasystem.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.teasystem.common.exception.BusinessException;
import org.example.teasystem.common.result.ResultCode;
import org.example.teasystem.dto.address.AddressSaveRequest;
import org.example.teasystem.dto.address.AddressVO;
import org.example.teasystem.entity.Address;
import org.example.teasystem.mapper.AddressMapper;
import org.example.teasystem.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 地址服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public List<AddressVO> getUserAddresses(Long userId) {
        List<Address> addresses = addressMapper.findByUserId(userId);
        return addresses.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public AddressVO getDefaultAddress(Long userId) {
        Address address = addressMapper.findDefaultByUserId(userId);
        return address != null ? convertToVO(address) : null;
    }

    @Override
    @Transactional
    public Long addAddress(Long userId, AddressSaveRequest request) {
        // 如果设置为默认，则先清除其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }

        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

        addressMapper.insert(address);
        log.info("用户添加收货地址: userId={}, addressId={}", userId, address.getId());

        return address.getId();
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Long addressId, AddressSaveRequest request) {
        // 验证地址归属
        Address existingAddress = addressMapper.findById(addressId);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "地址不存在或无权操作");
        }

        // 如果设置为默认，则先清除其他默认地址
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            addressMapper.clearDefaultByUserId(userId);
        }

        Address address = new Address();
        address.setId(addressId);
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);

        addressMapper.update(address);
        log.info("用户修改收货地址: userId={}, addressId={}", userId, addressId);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        // 验证地址归属
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "地址不存在或无权操作");
        }

        addressMapper.deleteById(addressId);
        log.info("用户删除收货地址: userId={}, addressId={}", userId, addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        // 验证地址归属
        Address address = addressMapper.findById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "地址不存在或无权操作");
        }

        // 先清除所有默认标记，再设置当前地址为默认
        addressMapper.clearDefaultByUserId(userId);
        addressMapper.setDefault(addressId);

        log.info("用户设置默认地址: userId={}, addressId={}", userId, addressId);
    }

    /**
     * 转换为 VO
     */
    private AddressVO convertToVO(Address address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}
