package com.example.be_service.controller;

import com.example.be_service.dto.customer.CustomerAdminDTO;
import com.example.be_service.dto.customer.CustomerListDTO;
import com.example.be_service.dto.customer.CustomerProfileRequestDTO;
import com.example.be_service.dto.customer.CustomerDTO;
import com.example.be_service.dto.customer.GuestUserDTO;
import com.example.be_service.service.CustomerService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/backoffice/customers")
    public ResponseEntity<CustomerListDTO> getCustomers(@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo) {
        return ResponseEntity.ok(customerService.getCustomers(pageNo));
    }

    @GetMapping("/backoffice/customers/{email}")
    public ResponseEntity<CustomerAdminDTO> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @GetMapping("/storefront/customer/profile")
    public ResponseEntity<CustomerDTO> getCustomerProfile() {
        return ResponseEntity.ok(customerService.getCustomerProfile(SecurityContextHolder.getContext().getAuthentication().getName()));
    }

    @PostMapping("/storefront/customer/guest-user")
    public GuestUserDTO createGuestUser() {
        return customerService.createGuestUser();
    }


    @PutMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateProfile(@RequestBody CustomerProfileRequestDTO requestVm) {
        customerService.updateCustomers(requestVm);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/storefront")
    public String hello() {
        return "Hello World";
    }
}
