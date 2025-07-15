package com.example.TEAM_B_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class AudioStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println("📁 업로드 폴더 생성됨: " + uploadDir + ", 성공 여부: " + created);
        } else {
            System.out.println("📁 업로드 폴더 이미 존재: " + uploadDir);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileId = UUID.randomUUID().toString();
        String newFileName = fileId + extension;

        File destination = new File(uploadDir + File.separator + newFileName);

        // 디버깅 로그 추가
        System.out.println("📝 원본 파일명: " + originalFilename);
        System.out.println("📍 저장 경로: " + destination.getAbsolutePath());
        System.out.println("📂 저장 폴더 존재?: " + destination.getParentFile().exists());
        System.out.println("📂 저장 폴더 쓰기 가능?: " + destination.getParentFile().canWrite());

        file.transferTo(destination);

        System.out.println("✅ 파일 저장 성공: " + destination.getAbsolutePath());

        return fileId;
    }
}
