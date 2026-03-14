package com.sphere.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        // validate size 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Photo size must be under 5MB!");
        }
        // validate type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed!");
        }

        Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "sphere/photos",
                "resource_type", "image"
        ));
        return (String) result.get("secure_url");
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        // validate size 20MB
        if (file.getSize() > 20 * 1024 * 1024) {
            throw new RuntimeException("Video size must be under 20MB!");
        }
        // validate type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new RuntimeException("Only video files are allowed!");
        }

        Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "sphere/videos",
                "resource_type", "video"
        ));
        return (String) result.get("secure_url");
    }
}