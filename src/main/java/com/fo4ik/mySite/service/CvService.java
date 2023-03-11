package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Cv;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.CvRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CvService {

    private static final Logger log = LoggerFactory.getLogger(CvService.class);
    private final CvRepo cvRepo;
    private final LogoService logoService;


    public CvService(CvRepo cvRepo, LogoService logoService) {
        this.cvRepo = cvRepo;
        this.logoService = logoService;
    }

    public Cv getCv(long id) {
        return cvRepo.findById(id).orElse(null);
    }

    public Cv getCv(User user) {
        return cvRepo.findByUser(user);
    }

    public void saveCv(User user, MultipartFile file, boolean isImage) {
        try {
            Path path = Path.of("files/users/" + user.getId() + "/pdf/");
            Cv cv = getCv(user);
            if (cv == null) {
                cv = new Cv();
                cv.setUser(user);
            }
            if (isImage) {
                byte[] bytes = file.getBytes();
                BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
                Path imagePath = Path.of(path + "/" + "cv.png");
                File output = new File(String.valueOf(imagePath));


                if (logoService.getLogo("cv") != null) {
                    logoService.delete(logoService.getLogo("cv"));
                }

                cv.setImgPath(String.valueOf(imagePath));
                cvRepo.save(cv);

                ImageIO.write(bufferedImage, "png", output);
            } else {
                byte[] bytes = file.getBytes();
                String fileName = "cv.pdf";
                Path filePath = Path.of(path + "/" + fileName);
                File disk = new File(path.toString());
                if (!disk.exists()) {
                    disk.mkdirs();
                }
                cv.setFilePath(String.valueOf(filePath));
                cvRepo.save(cv);
                Files.write(Path.of(path + "/" + fileName), bytes);
            }
            cvRepo.save(cv);
        } catch (Exception e) {
            log.error("Error in saveCv: " + e.getMessage());
        }
    }
}
