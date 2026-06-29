package com.huuhv.foodsndrinks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể tạo thư mục upload: " + rootLocation, e);
        }
    }

    /**
     * Saves a file under the given sub-folder and returns the URL path (e.g. /uploads/products/uuid.jpg).
     */
    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống!");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ cho phép upload file ảnh!");
        }

        String originalName = file.getOriginalFilename();
        String extension    = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase(Locale.ROOT)
                : "";
        Set<String> allowed = Set.of(".png", ".jpg", ".jpeg", ".gif", ".webp");
        if (!allowed.contains(extension)) {
            throw new IllegalArgumentException("Định dạng ảnh không hợp lệ!");
        }
        String fileName = UUID.randomUUID() + extension;

        try {
            Path destination = rootLocation.resolve(subFolder).normalize();
            if (!destination.startsWith(rootLocation)) {
                throw new IllegalArgumentException("Thư mục upload không hợp lệ!");
            }
            Files.createDirectories(destination);
            file.transferTo(destination.resolve(fileName));
        } catch (IOException e) {
            throw new IllegalStateException("Không thể lưu file: " + fileName, e);
        }

        return "/uploads/" + subFolder + "/" + fileName;
    }

    /**
     * Deletes a file given its URL path (e.g. /uploads/products/uuid.jpg).
     */
    public void delete(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) return;
        try {
            // Strip the leading /uploads/ prefix to get the relative path
            String relative = urlPath.startsWith("/uploads/") ? urlPath.substring("/uploads/".length()) : urlPath;
            Path target = rootLocation.resolve(relative).normalize();
            if (!target.startsWith(rootLocation)) {
                log.warn("Từ chối xóa file ngoài thư mục upload: {}", urlPath);
                return;
            }
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("Không thể xóa file: {}", urlPath, e);
        }
    }
}


