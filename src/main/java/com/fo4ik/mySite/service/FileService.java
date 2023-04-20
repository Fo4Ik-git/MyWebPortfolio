package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.File;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.FileRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FileService {

    private final FileRepo fileRepo;

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

    public void saveFile(File file) {
        fileRepo.save(file);
    }

    public void saveFile(User user, Map<String, String> links, String name) {
        File file = new File();
        file.setUser(user);
        file.setLinks(links);
        file.setName(name);
        fileRepo.save(file);
    }

    public void saveFile(User user, String url, String pathToFileInDB, String name) {
        File file = new File();
        file.setUser(user);
        file.addLink(url, pathToFileInDB);
        file.setName(name);
        fileRepo.save(file);
    }

    public void deleteFile(long id) {
        fileRepo.deleteById(id);
    }

}
