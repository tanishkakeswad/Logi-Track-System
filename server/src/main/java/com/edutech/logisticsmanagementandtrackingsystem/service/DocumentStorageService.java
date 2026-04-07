package com.edutech.logisticsmanagementandtrackingsystem.service;

import com.edutech.logisticsmanagementandtrackingsystem.entity.Cargo;
import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentStorageService.class);

    // ✅ IMPORTANT: /tmp is writable in most hosted/virtual environments
    // You can override this in application.properties if you want:
    // file.upload.base-path=/some/path
    @Value("${file.upload.base-path:/tmp/cargowala-uploads}")
    private String basePath;

    public CargoDocument storeFile(MultipartFile file, Cargo cargo) throws IOException {

        if (file == null) {
            logger.warn("DOC-STORAGE: storeFile called with null file");
            throw new IOException("File is null");
        }

        if (file.isEmpty()) {
            logger.warn("DOC-STORAGE: storeFile called with empty file");
            throw new IOException("File is empty");
        }

        if (cargo == null || cargo.getId() == null) {
            logger.warn("DOC-STORAGE: storeFile called with invalid cargo (cargo or cargoId is null)");
            throw new IOException("Cargo is null or cargoId is null");
        }

        String originalName = file.getOriginalFilename();
        long size = file.getSize();
        String contentType = file.getContentType();

        // ✅ Safety: handle null/empty filename
        if (originalName == null || originalName.trim().isEmpty()) {
            originalName = "document";
        }

        logger.info("DOC-STORAGE: Upload start | cargoId={} | originalName={} | size={} bytes | contentType={}",
                cargo.getId(), originalName, size, contentType);

        try {
            String extension = getFileExtension(originalName);
            String storedFileName = UUID.randomUUID() + extension;

            // ✅ Normalize and use absolute path (more reliable in containers)
            Path cargoDir = Paths.get(basePath, "cargo", cargo.getId().toString(), "documents")
                    .toAbsolutePath()
                    .normalize();

            logger.info("DOC-STORAGE: Using upload directory={}", cargoDir);

            Files.createDirectories(cargoDir);

            Path filePath = cargoDir.resolve(storedFileName).normalize();

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            logger.info("DOC-STORAGE: Upload success | cargoId={} | storedFileName={} | savedPath={}",
                    cargo.getId(), storedFileName, filePath.toString());

            return new CargoDocument(
                    originalName,
                    storedFileName,
                    contentType,
                    size,
                    filePath.toString(),
                    cargo
            );

        } catch (IOException ex) {
            logger.error("DOC-STORAGE: Upload failed (IO) | cargoId={} | originalName={} | Reason={}",
                    cargo.getId(), originalName, ex.getMessage(), ex);
            throw ex;

        } catch (Exception ex) {
            logger.error("DOC-STORAGE: Upload failed (Unexpected) | cargoId={} | originalName={} | Reason={}",
                    cargo.getId(), originalName, ex.getMessage(), ex);
            throw new IOException("Unexpected error while storing file", ex);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf("."))
                : "";
    }
}
