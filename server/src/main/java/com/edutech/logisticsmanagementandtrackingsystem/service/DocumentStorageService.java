package com.edutech.logisticsmanagementandtrackingsystem.service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class DocumentStorageService {

    @Value("${file.upload.base-path:uploads}")
    private String basePath;

    public CargoDocument storeFile(MultipartFile file, Cargo cargo) throws IOException {

        String extension = getFileExtension(file.getOriginalFilename());
        String storedFileName = UUID.randomUUID() + extension;

        Path cargoDir = Paths.get(basePath, "cargo", cargo.getId().toString(), "documents");
        Files.createDirectories(cargoDir);

        Path filePath = cargoDir.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return new CargoDocument(
                file.getOriginalFilename(),
                storedFileName,
                file.getContentType(),
                file.getSize(),
                filePath.toString(),
                cargo
        );
    }

    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
    }
}
