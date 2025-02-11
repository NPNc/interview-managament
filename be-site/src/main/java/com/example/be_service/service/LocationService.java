package com.example.be_service.service;


import com.example.be_service.config.ServiceUrlConfig;
import com.example.be_service.dto.address.AddressDetailDTO;
import com.example.be_service.dto.address.AddressPostDTO;
import com.example.be_service.dto.address.AddressDTO;
import com.example.be_service.exception.AccessDeniedException;
import com.example.be_service.exception.NotFoundException;
import java.net.URI;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LocationService {

    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

//    public LocationService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
//        this.webClient = webClient;
//        this.serviceUrlConfig = serviceUrlConfig;
//    }

    public LocationService(WebClient webClient) {
        this.webClient = webClient;
        this.serviceUrlConfig = new ServiceUrlConfig("test");
    }

    public List<AddressDetailDTO> getAddressesByIdList(List<Long> ids) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .queryParam("ids", ids)
            .buildAndExpand()
            .toUri();

        return webClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .onStatus(
                HttpStatus.UNAUTHORIZED::equals,
                response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
            .bodyToMono(new ParameterizedTypeReference<List<AddressDetailDTO>>() {
            })
            .block();
    }

    public AddressDetailDTO getAddressById(Long id) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.location())
            .path("/storefront/addresses/{id}")
            .buildAndExpand(id)
            .toUri();

        return webClient.get()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .retrieve()
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(String.class).map(NotFoundException::new))
            .bodyToMono(AddressDetailDTO.class)
            .block();
    }

    public AddressDTO createAddress(AddressPostDTO addressPostDTO) {
        final String jwt = ((Jwt) SecurityContextHolder.getContext().getAuthentication()
            .getPrincipal()).getTokenValue();
        final URI url = UriComponentsBuilder.fromUriString(serviceUrlConfig.location())
            .path("/storefront/addresses")
            .buildAndExpand()
            .toUri();

        return webClient.post()
            .uri(url)
            .headers(h -> h.setBearerAuth(jwt))
            .bodyValue(addressPostDTO)
            .retrieve()
            .onStatus(
                HttpStatus.UNAUTHORIZED::equals,
                response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
            .onStatus(
                HttpStatus.FORBIDDEN::equals,
                response -> response.bodyToMono(String.class).map(AccessDeniedException::new))
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(String.class).map(NotFoundException::new))
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(String.class).map(NotFoundException::new))
            .bodyToMono(AddressDTO.class)
            .block();
    }
}
