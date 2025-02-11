package com.example.be_service.dto.user_address;

import com.example.be_service.dto.address.AddressDTO;
import com.example.be_service.model.UserAddress;
import lombok.Builder;

@Builder
public record UserAddressDTO(
        Long id,
        String userId,
        AddressDTO addressGetVm,
        Boolean isActive) {
    public static UserAddressDTO fromModel(UserAddress userAddress, AddressDTO addressGetVm) {
        return UserAddressDTO.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUserId())
                .addressGetVm(addressGetVm)
                .isActive(userAddress.getIsActive())
                .build();
    }
}
