package com.example.be_service.dto.address;

public record ActiveAddressDTO(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String city,
        String zipCode,
        Long districtId,
        String districtName,
        Long stateOrProvinceId,
        String stateOrProvinceName,
        Long countryId,
        String countryName,
        Boolean isActive
) {

}
