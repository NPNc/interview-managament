package com.example.be_service.dto.customer;

import java.util.List;

public record CustomerListDTO(int totalUser, List<CustomerAdminDTO> customers, int totalPage ) {
    
}
