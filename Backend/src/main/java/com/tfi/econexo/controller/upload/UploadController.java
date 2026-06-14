package com.tfi.econexo.controller.upload;

import com.tfi.econexo.service.upload.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
@Tag(name = "Upload", description = "Endpoint for uploading files")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping
    @Operation(summary = "Upload an image to Cloudinary")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Error processing the image")
    public ResponseEntity<Map<String, String>> uploadImage (
            @RequestParam("file")MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder){
        try{
            String url = cloudinaryService.uploadFile(file, folder);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error processing the image"));
        }
    }
}
