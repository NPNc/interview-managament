package com.example.be_service.repository;

import com.example.be_service.model.UserAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findAllByUserId(String userId);

    UserAddress findOneByUserIdAndAddressId(String userId, Long id);

    Optional<UserAddress> findByIsActiveTrue();
}
