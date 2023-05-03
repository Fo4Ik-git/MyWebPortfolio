package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.File;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.FileRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final FileRepo fileRepo;
    @Value("${host}")
    private String host;
    @Value("${server.port}")
    private int port;

    public FileService(FileRepo fileRepo) {
        this.fileRepo = fileRepo;
    }

    public File getFile(long id) {
        return fileRepo.findById(id);
    }

    public File getFile(String link) {
        return fileRepo.findByLink(link);
    }

    public List<File> getAllFiles() {
        return fileRepo.findAll();
    }

    public List<File> getAllFilesByUser(User user) {
        return fileRepo.findAllByUser_Id(user.getId());
    }

    public void saveFile(File file) {
        fileRepo.save(file);
    }

    public void saveFile(User user, MultipartFile userFile, String name, String version) {
        try {
            Path path = Path.of("files/users/" + user.getId() + "/files/");
            Path pathToFileInDrive = Path.of(path + "/" + userFile.getOriginalFilename());
            Files.createDirectories(path);
            if (isFileExist(pathToFileInDrive)) {
                log.info("File: " + userFile.getOriginalFilename() + " already exist, saving file with new name");
                pathToFileInDrive = Path.of(path + "/" + renameIfExists(pathToFileInDrive));
                userFile.transferTo(pathToFileInDrive);
                log.info("File: " + userFile.getOriginalFilename() + " saved with new name: " + pathToFileInDrive.getFileName());
            } else {
                userFile.transferTo(pathToFileInDrive);
                log.info("File: " + userFile.getOriginalFilename() + " saved");
            }
            File savedFile = saveToDBWithFile(user, name, version);
            createUrlToDownloadFile(savedFile, pathToFileInDrive);
        } catch (Exception e) {
            log.error("Error in fileService: " + e.getMessage() + "\n" + e.getStackTrace().toString());
        }
    }

    private void createUrlToDownloadFile(File file, Path pathToFileInDrive) {
        File fileFromDB = fileRepo.findById(file.getId());
        String url = "http://" + host + ":" + port + "/api/file/" + file.getId();
        fileFromDB.addLink(url, pathToFileInDrive.toString());

        fileRepo.save(file);
    }

    private File saveToDBWithFile(User user, String name, String version) {
        File file = new File();
        file.setUser(user);
        file.setName(name);
        file.setVersion(version);
        fileRepo.save(file);
        return file;
    }

    private boolean isFileExist(Path pathToFileInDrive) {
        return pathToFileInDrive.toFile().exists();
    }

    public String renameIfExists(Path filePath) {
        String originalFileName = filePath.getFileName().toString();
        String fileName = originalFileName;
        int count = 0;

        // Check if file already exists
        while (isFileExist(filePath)) {
            count++;
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = originalFileName.substring(0, dotIndex) + count + originalFileName.substring(dotIndex);
            } else {
                fileName = originalFileName + count;
            }
            filePath = filePath.resolveSibling(fileName);
        }

        // Rename file to new name
        try {
            Files.move(filePath, filePath.resolveSibling(fileName));
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateFile(long id, User user, MultipartFile userFile, String name, String version) {
        Path path = Path.of("files/users/" + user.getId() + "/files/");
        Path pathToFileInDrive = Path.of(path + "/" + userFile.getOriginalFilename());
        Path oldFilePath = getFileNameFromDB(id);

        try {
            if (isFileExist(pathToFileInDrive)) {
                java.io.File fileInStorage = new java.io.File(pathToFileInDrive.toString());
                if (fileInStorage.delete()) {
                    log.info("File: " + pathToFileInDrive.toString() + " deleted");
                }
            } else if (isFileExist(oldFilePath)) {
                java.io.File fileInStorage = new java.io.File(oldFilePath.toString());
                if (fileInStorage.delete()) {
                    log.info("File: " + oldFilePath.toString() + " deleted");
                    pathToFileInDrive = Path.of(path + "/" + oldFilePath.getFileName());
                }
            }

            userFile.transferTo(pathToFileInDrive);
            log.info("File: " + userFile.getOriginalFilename() + " saved in: " + pathToFileInDrive.toString());
            File file = fileRepo.findById(id);
            file.setName(name);
            file.setVersion(version);
            fileRepo.save(file);
        } catch (IOException e) {
            log.error("Error in fileService updateFile: " + e.getMessage(), e);
        }
    }

    public Path getFileNameFromDB(long id) {
        Map<String, String> links = fileRepo.findById(id).getLinks();
        return Path.of(links.get(links.keySet().iterator().next()));
    }

    public File getFileById(Long id) {
        return fileRepo.findById(id).orElse(null);
    }

    public List<File> searchFilesByName(String name) {
        return fileRepo.findByNameContainingIgnoreCase(name);
    }

    public List<File> search(String query) {
        List<File> files;
        try {
            Long id = Long.parseLong(query);
            files = fileRepo.findById(id)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } catch (NumberFormatException e) {
            files = searchFilesByName(query);
        }
        return files;
    }

    public void deleteFile(long id) {
        try {
            Files.delete(getFileNameFromDB(id));
        } catch (Exception e) {
            log.error("Error in fileService deleteFile: " + e.getMessage(), e);
        }
        fileRepo.deleteById(id);
    }

    public String findUrlsByFileId(long fileId) {
        List<String> urls = fileRepo.findUrlsByFileId(fileId);
        if (urls.isEmpty()) {
            return null;
        }
        return urls.get(0);
    }

}
