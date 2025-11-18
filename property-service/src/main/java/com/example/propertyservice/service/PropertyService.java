package com.example.propertyservice.service;

import com.example.propertyservice.dto.request.PropertyImageRequest;
import com.example.propertyservice.dto.request.PropertyImageUrlsRequest;
import com.example.propertyservice.dto.request.PropertyRequest;
import com.example.propertyservice.dto.response.PropertyResponse;
import com.example.propertyservice.dto.response.UploadUrlsResponse;
import com.example.propertyservice.dto.response.UserResponse;
import com.example.propertyservice.exception.ResourceNotFoundException;
import com.example.propertyservice.mapper.PropertyMapper;
import com.example.propertyservice.model.Property;
import com.example.propertyservice.model.PropertyImage;
import com.example.propertyservice.repository.PropertyImageRepository;
import com.example.propertyservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final WebClient userServiceWebClient;
    private final PropertyMapper propertyMapper;
    private final PropertyImageBlobStorageService propertyImageBlobStorageService;
    private final PropertyImageRepository propertyImageRepository;

    public List<PropertyResponse> findAll() {
        return propertyRepository.findAll().stream().map((property) -> {
            var propertyResponse = propertyMapper.toDto(property);
            propertyResponse.propertyImages().addAll(property.getPropertyImages().stream().map(PropertyImage::getUrl).toList());
            return propertyResponse;
        }).toList();
    }

    public PropertyResponse findPropertyById(String id) {
        Property property = propertyRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException("wrong property id"));
        var propertyResponse = propertyMapper.toDto(property);
        propertyResponse.propertyImages().addAll(property.getPropertyImages().stream().map(PropertyImage::getUrl).toList());
        return propertyResponse;
    }

    public List<PropertyResponse> findAllApproved() {
        return propertyRepository.findAllApproved().stream().map((property) -> {
            var propertyResponse = propertyMapper.toDto(property);
            propertyResponse.propertyImages().addAll(property.getPropertyImages().stream().map(PropertyImage::getUrl).toList());
            return propertyResponse;
        }).toList();
    }

    @Transactional
    public PropertyResponse save(Jwt jwt, PropertyRequest request) {
        UserResponse user = userServiceWebClient
                .post()
                .uri("/api/users/get-or-create")
                .header("Authorization", "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        if (user == null) {
            throw new ResourceNotFoundException("wrong user response");
        }

        Property property = propertyMapper.toEntity(request);
        property.setUserId(UUID.fromString(user.id()));
        property.setApproved(false);
        property.getAddress().setProperty(property);

        Property savedProperty = propertyRepository.save(property);

        return propertyMapper.toDto(savedProperty);
    }

    public UploadUrlsResponse uploadUrls(Jwt jwt, String propertyId, PropertyImageRequest request) {
        Property property = propertyRepository.findById(UUID.fromString(propertyId)).orElseThrow(() -> new ResourceNotFoundException("property not found"));

        if (!jwt.getClaim("sub").equals(property.getUserId().toString())) {
            throw new AccessDeniedException("unauthorized");
        }

        List<UploadUrlsResponse.UploadUrlRow> uploadUrlRows = new ArrayList<>();
        request.propertyImages().forEach((fileName) -> {
            UploadUrlsResponse.UploadUrlRow uploadUrlRow = new UploadUrlsResponse.UploadUrlRow(
                    fileName,
                    propertyImageBlobStorageService.generateSasToken(propertyId, fileName)
            );
            uploadUrlRows.add(uploadUrlRow);
        });

        return new UploadUrlsResponse(uploadUrlRows);
    }

    @Transactional
    public PropertyResponse savePropertyImages(Jwt jwt, String propertyId,PropertyImageUrlsRequest request) {
        Property property = propertyRepository.findById(UUID.fromString(propertyId)).orElseThrow(() -> new ResourceNotFoundException("property not found"));

        if (!jwt.getClaim("sub").equals(property.getUserId().toString())) {
            throw new AccessDeniedException("unauthorized");
        }

        var propertyResponse = propertyMapper.toDto(property);
        List<PropertyImage> images = request.propertyImages().stream()
                .map(url -> new PropertyImage(url, property))
                .toList();
        propertyImageRepository.saveAll(images);
        propertyResponse.propertyImages().addAll(request.propertyImages());

        return propertyResponse;
    }

    @Transactional
    public PropertyResponse approve(String propertyId) {
        Property property = propertyRepository
                .findByIdWithAddressAndImages(UUID.fromString(propertyId))
                .orElseThrow(() -> new ResourceNotFoundException("property not found"));
        property.setApproved(true);

        propertyRepository.save(property);

        var propertyResponse = propertyMapper.toDto(property);
        propertyResponse.propertyImages().addAll(property.getPropertyImages().stream().map(PropertyImage::getUrl).toList());
        return propertyResponse;
    }

    @Transactional
    public void decline(String propertyId) {
        Property property = propertyRepository
                .findByIdWithAddressAndImages(UUID.fromString(propertyId))
                .orElseThrow(() -> new ResourceNotFoundException("property not found"));
        property.setApproved(true);

        propertyRepository.delete(property);
    }

    public List<PropertyResponse> findPropertiesByApprove(boolean approved) {
        return propertyRepository.findAllByApproved(approved).stream().map((property) -> {
            var propertyResponse = propertyMapper.toDto(property);
            propertyResponse.propertyImages().addAll(property.getPropertyImages().stream().map(PropertyImage::getUrl).toList());
            return propertyResponse;
        }).toList();
    }
}
