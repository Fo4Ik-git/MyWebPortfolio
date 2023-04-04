package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.LogoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LogoService {

    private static final Logger log = LoggerFactory.getLogger(LogoService.class);

    private final LogoRepo logoRepo;

    public LogoService(LogoRepo logoRepo) {
        this.logoRepo = logoRepo;
    }

    public void saveLogo(User user, MultipartFile logoFile) {


        if (logoRepo.findByUser(user) == null) {
            createLogo(user, logoFile);
        } else {
            updateLogo(user, logoFile);
        }
    }

    public Logo getLogo(String name) {
        return logoRepo.findByName(name);
    }

    public Logo getLogo(User user) {
        return logoRepo.findByUser(user);
    }

    public void delete(User user) {
        try {
            Logo logo = logoRepo.findByUser(user);
            logoRepo.delete(logo);
            log.info("Logo for user " + user.getUsername() + " has been deleted");
        } catch (Exception e) {
            log.error("Error to delete logo: " + e.getMessage());
        }
    }

    public void delete(Logo logo) {
        try {
            logoRepo.delete(logo);
            log.info("Logo " + logo.getName() + " has been deleted");
        } catch (Exception e) {
            log.error("Error to delete logo: " + e.getMessage());
        }
    }

    private void createLogo(User user, MultipartFile logoFile) {
        try {
            Path path = createLogoFile(user, logoFile);
            String name = "logo." + StringUtils.getFilenameExtension(logoFile.getOriginalFilename());
            Logo logo = new Logo(path.toString(), name, user);
            saveLogoInDB(logo, user);
            log.info("Logo for user " + user.getUsername() + " has been saved");
        } catch (Exception e) {
            log.error("Error to save logo: " + e.getMessage());
        }
    }

    private void updateLogo(User user, MultipartFile logoFile) {
        try {
            Path path = createLogoFile(user, logoFile);
            saveLogoInDB(path, user);
            log.info("Logo for user " + user.getUsername() + " has been updated");
        } catch (Exception e) {
            log.error("Error to update logo: " + e.getMessage());

        }

    }

    private Path createLogoFile(User user, MultipartFile logoFile) {
        try {
            String name = "logo." + StringUtils.getFilenameExtension(logoFile.getOriginalFilename());
            Path folder = Path.of(UserService.createUserFolder(user.getId()) + "/");
            Path path = folder.resolve(name);
            Files.write(path, logoFile.getBytes());
            return path;
        } catch (Exception e) {
            log.error("Error to create logo file: " + e.getMessage());
            return null;
        }
    }

    private void saveLogoInDB(Path saveLogoPath, User user) {
        try {
            Logo logo = logoRepo.findByUser(user);
            logo.setPath(saveLogoPath.toString());
            logoRepo.save(logo);
        } catch (Exception e) {
            log.warn("Error to save logo in DB: " + e.getMessage());
        }
    }

    private void saveLogoInDB(Logo logo, User user) {
        try {
            logoRepo.save(logo);
        } catch (Exception e) {
            log.warn("Error to save logo in DB: " + e.getMessage());
        }
    }


}
