package com.example.be_service.service;

import com.example.be_service.dto.address.ActiveAddressDTO;
import com.example.be_service.dto.address.AddressDetailDTO;
import com.example.be_service.dto.address.AddressPostDTO;
import com.example.be_service.dto.address.AddressDTO;
import com.example.be_service.dto.user_address.UserAddressDTO;
import com.example.be_service.exception.AccessDeniedException;
import com.example.be_service.exception.NotFoundException;
import com.example.be_service.model.UserAddress;
import com.example.be_service.repository.UserAddressRepository;
import com.example.be_service.utils.Constants;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final LocationService locationService;

    public UserAddressService(UserAddressRepository userAddressRepository, LocationService locationService) {
        this.userAddressRepository = userAddressRepository;
        this.locationService = locationService;
    }

    public List<ActiveAddressDTO> getUserAddressList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser"))
            throw new AccessDeniedException(Constants.ERROR_CODE.UNAUTHENTICATED);

        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        List<AddressDetailDTO> addressVmList = locationService.getAddressesByIdList(
                userAddressList.stream()
                        .map(UserAddress::getAddressId)
                        .collect(Collectors.toList()));

        List<ActiveAddressDTO> addressActiveVms = userAddressList.stream()
                .flatMap(userAddress -> addressVmList.stream()
                        .filter(
                            addressDetailDTO -> userAddress.getAddressId().equals(addressDetailDTO.id()))
                        .map(addressDetailDTO -> new ActiveAddressDTO(
                                addressDetailDTO.id(),
                                addressDetailDTO.contactName(),
                                addressDetailDTO.phone(),
                                addressDetailDTO.addressLine1(),
                                addressDetailDTO.city(),
                                addressDetailDTO.zipCode(),
                                addressDetailDTO.districtId(),
                                addressDetailDTO.districtName(),
                                addressDetailDTO.stateOrProvinceId(),
                                addressDetailDTO.stateOrProvinceName(),
                                addressDetailDTO.countryId(),
                                addressDetailDTO.countryName(),
                                userAddress.getIsActive()
                        ))
                ).toList();

        //sort by isActive
        Comparator<ActiveAddressDTO> comparator = Comparator.comparing(ActiveAddressDTO::isActive).reversed();
        return addressActiveVms.stream().sorted(comparator).collect(Collectors.toList());
    }

    public AddressDetailDTO getAddressDefault() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId.equals("anonymousUser"))
            throw new AccessDeniedException(Constants.ERROR_CODE.UNAUTHENTICATED);

        UserAddress userAddress = userAddressRepository.findByIsActiveTrue().orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.USER_ADDRESS_NOT_FOUND));

        return locationService.getAddressById(userAddress.getAddressId());
    }

    public UserAddressDTO createAddress(AddressPostDTO addressPostDTO) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        AddressDTO addressGetVm = locationService.createAddress(addressPostDTO);
        UserAddress userAddress = UserAddress.builder()
                .userId(userId)
                .addressId(addressGetVm.id())
                .isActive(false)
                .build();

        return UserAddressDTO.fromModel(userAddressRepository.save(userAddress), addressGetVm);

    }

    public void deleteAddress(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAddress userAddress = userAddressRepository.findOneByUserIdAndAddressId(userId, id);
        if (userAddress == null) {
            throw new NotFoundException(Constants.ERROR_CODE.USER_ADDRESS_NOT_FOUND);
        }
        userAddressRepository.delete(userAddress);
    }

    public void chooseDefaultAddress(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserAddress> userAddressList = userAddressRepository.findAllByUserId(userId);
        List<UserAddress> newUserAddressList = new ArrayList<>();
        for (UserAddress userAddress : userAddressList) {
            userAddress.setIsActive(Objects.equals(userAddress.getAddressId(), id));
            newUserAddressList.add(userAddress);
        }
        userAddressRepository.saveAll(newUserAddressList);
    }
}
