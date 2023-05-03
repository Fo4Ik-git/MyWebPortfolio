package com.fo4ik.mySite.controllers.rest;

import com.fo4ik.mySite.model.File;
import com.fo4ik.mySite.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/file")
public class FileRestController {

    private final FileService fileService;
    public FileRestController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<File> getFileData(@PathVariable("id") Long id) {
        File file = fileService.getFile(id);
        File fileData = new File();
        fileData.setId(file.getId());
        fileData.setVersion(file.getVersion());
        fileData.setName(file.getName());
        fileData.setLinks(file.getLinks());
        return ResponseEntity.ok(fileData);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") long id) {

        File file = fileService.getFile(id);
        Path pathToFileInDrive = fileService.getFileNameFromDB(id);
        java.io.File normalFile = new java.io.File(String.valueOf(pathToFileInDrive));

        Resource resource = new FileSystemResource(pathToFileInDrive.toFile());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileService.getFileNameFromDB(id).getFileName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(normalFile.length())
                .body(resource);
    }
}
