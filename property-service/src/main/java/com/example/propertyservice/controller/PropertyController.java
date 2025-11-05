package com.example.propertyservice.controller;


import com.example.propertyservice.dto.request.PropertyImageRequest;
import com.example.propertyservice.dto.request.PropertyImageUrlsRequest;
import com.example.propertyservice.dto.request.PropertyRequest;
import com.example.propertyservice.dto.response.PropertyResponse;
import com.example.propertyservice.dto.response.UploadUrlsResponse;
import com.example.propertyservice.service.PropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping()
    public ResponseEntity<PropertyResponse> save(@Valid @RequestBody PropertyRequest propertyRequest, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.save(jwt, propertyRequest));
    }

    @PostMapping("/{propertyId}/upload-urls")
    public ResponseEntity<UploadUrlsResponse> uploadUrls(
            @PathVariable String propertyId,
            @Valid @RequestBody PropertyImageRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.uploadUrls(jwt, propertyId, request));
    }

    @PostMapping("/{propertyId}/images")
    public ResponseEntity<PropertyResponse> savePropertyImages(
            @Valid @RequestBody PropertyImageUrlsRequest request,
            @PathVariable String propertyId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.savePropertyImages(jwt, propertyId, request));
    }

    @PatchMapping("/{propertyId}/approve")
    public ResponseEntity<PropertyResponse> approve(@PathVariable("propertyId") String propertyId) {
        return ResponseEntity.ok(propertyService.approve(propertyId));
    }

    @PatchMapping("/{propertyId}/decline")
    public ResponseEntity<PropertyResponse> decline(@PathVariable("propertyId") String propertyId) {
        propertyService.decline(propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<List<PropertyResponse>> findAll() {
        return ResponseEntity.ok(propertyService.findAll());
    }
}
