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

    @Value("${file.upload.base-path:uploads}")
    private String basePath;

    public CargoDocument storeFile(MultipartFile file, Cargo cargo) throws IOException {

        if (file == null) {
            logger.warn("DOC-STORAGE: storeFile called with null file");
            throw new IOException("File is null");
        }

        if (cargo == null || cargo.getId() == null) {
            logger.warn("DOC-STORAGE: storeFile called with invalid cargo (cargo or cargoId is null)");
            throw new IOException("Cargo is null or cargoId is null");
        }

        String originalName = file.getOriginalFilename();
        long size = file.getSize();
        String contentType = file.getContentType();

        logger.info("DOC-STORAGE: Upload start | cargoId={} | originalName={} | size={} bytes | contentType={}",
                cargo.getId(), originalName, size, contentType);

        try {
            String extension = getFileExtension(originalName);
            String storedFileName = UUID.randomUUID() + extension;

            Path cargoDir = Paths.get(basePath, "cargo", cargo.getId().toString(), "documents");
            Files.createDirectories(cargoDir);

            Path filePath = cargoDir.resolve(storedFileName);

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