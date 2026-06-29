package com.tfi.econexo.utils.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

public class Base64ToMultipartConverter {

    public static MultipartFile convert(String base64, String name) {
        String[] parts = base64.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];
        byte[] imageBytes = Base64.getDecoder().decode(imageString);
        return new CustomMultipartFile(imageBytes, name);
    }
}
