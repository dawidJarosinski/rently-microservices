package com.example.propertyservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PropertyImageUrlsRequest(@NotEmpty(message = "list cannot be empty")@Size(max = 20, message = "cant upload more than 20 images") List<@NotBlank(message = "url cannot be blank") String> propertyImages) {
}
