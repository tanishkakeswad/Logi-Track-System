package com.edutech.logisticsmanagementandtrackingsystem.Controller;

import com.edutech.logisticsmanagementandtrackingsystem.entity.CargoDocument;
import com.edutech.logisticsmanagementandtrackingsystem.repository.CargoDocumentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger logger =
            LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private CargoDocumentRepository documentRepository;

    // =========================
    // DOWNLOAD DOCUMENT
    // =========================
    @GetMapping("/{id}/download")
    public ResponseEntity<FileSystemResource> download(@PathVariable Long id) {

        logger.info("DOCUMENT: Download request received | documentId={}", id);

        try {
            CargoDocument doc = documentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("DOCUMENT: Document not found in DB | documentId={}", id);
                        return new RuntimeException("Document not found");
                    });

            File file = new File(doc.getFilePath());

            if (!file.exists()) {
                logger.warn(
                    "DOCUMENT: File exists in DB but missing on disk | documentId={} | path={}",
                    id, doc.getFilePath()
                );
                return ResponseEntity.notFound().build();
            }

            logger.info(
                "DOCUMENT: File download success | documentId={} | filename={}",
                id, doc.getOriginalFileName()
            );

            FileSystemResource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(doc.getFileType()))
                    .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getOriginalFileName() + "\""
                    )
                    .body(resource);

        } catch (Exception ex) {
            logger.error(
                "DOCUMENT: Error while downloading file | documentId={} | Reason={}",
                id, ex.getMessage(), ex
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}