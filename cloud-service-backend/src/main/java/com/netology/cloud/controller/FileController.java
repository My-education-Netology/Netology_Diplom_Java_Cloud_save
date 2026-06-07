package com.netology.cloud.controller;

import com.netology.cloud.model.dto.FileInfoResponse;
import com.netology.cloud.model.dto.RenameFileRequest;
import com.netology.cloud.model.entity.User;
import com.netology.cloud.security.AuthContext;
import com.netology.cloud.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Контроллер операций с файлами: /list и /file.
 */
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/list")
    public List<FileInfoResponse> listFiles(
            HttpServletRequest request,
            @RequestParam(required = false) Integer limit
    ) {
        User user = AuthContext.getCurrentUser(request);
        return fileService.listFiles(user, limit);
    }

    @PostMapping("/file")
    public ResponseEntity<Void> uploadFile(
            HttpServletRequest request,
            @RequestParam String filename,
            @RequestParam("file") MultipartFile file
    ) {
        User user = AuthContext.getCurrentUser(request);
        fileService.uploadFile(user, filename, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(
            HttpServletRequest request,
            @RequestParam String filename
    ) {
        User user = AuthContext.getCurrentUser(request);
        Resource resource = fileService.downloadFile(user, filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(
            HttpServletRequest request,
            @RequestParam String filename,
            @RequestBody RenameFileRequest body
    ) {
        User user = AuthContext.getCurrentUser(request);
        fileService.renameFile(user, filename, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(
            HttpServletRequest request,
            @RequestParam String filename
    ) {
        User user = AuthContext.getCurrentUser(request);
        fileService.deleteFile(user, filename);
        return ResponseEntity.ok().build();
    }
}
