package com.example.be_service.dto.address;

import lombok.Builder;

@Builder
public record AddressDTO(Long id, String contactName, String phone, String addressLine1, String city, String zipCode,
                         Long districtId, Long stateOrProvinceId, Long countryId) {

}
