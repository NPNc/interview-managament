package com.example.be_service.controller;

import com.example.be_service.dto.address.ActiveAddressDTO;
import com.example.be_service.dto.address.AddressDetailDTO;
import com.example.be_service.dto.address.AddressPostDTO;
import com.example.be_service.dto.user_address.UserAddressDTO;
import com.example.be_service.service.UserAddressService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping("/storefront/user-address")
    public ResponseEntity<List<ActiveAddressDTO>> getUserAddresses() {
        return ResponseEntity.ok(userAddressService.getUserAddressList());
    }

    @GetMapping("/storefront/user-address/default-address")
    public ResponseEntity<AddressDetailDTO> getDefaultAddress() {
        return ResponseEntity.ok(userAddressService.getAddressDefault());
    }

    @PostMapping("/storefront/user-address")
    public ResponseEntity<UserAddressDTO> createAddress(@Valid @RequestBody AddressPostDTO addressPostDTO) {
        return ResponseEntity.ok(userAddressService.createAddress(addressPostDTO));
    }

    @DeleteMapping("/storefront/user-address/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/storefront/user-address/{id}")
    public ResponseEntity<?> chooseDefaultAddress(@PathVariable Long id) {
        userAddressService.chooseDefaultAddress(id);
        return ResponseEntity.ok().build();
    }
}
