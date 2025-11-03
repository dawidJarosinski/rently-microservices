package com.example.propertyservice.dto.response;

import java.util.List;

public record UploadUrlsResponse(List<UploadUrlRow> uploadUrls) {
    public record UploadUrlRow(String fileName, String url) {}
}
