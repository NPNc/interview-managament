package com.example.be_service.dto.address;

public record AddressPostDTO(String contactName, String phone, String addressLine1, String city, String zipCode,
                             Long districtId, Long stateOrProvinceId, Long countryId) {
}
